package com.blinkreceipt.ocr.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blinkreceipt.ocr.R;
import com.blinkreceipt.ocr.Utility;
import com.blinkreceipt.ocr.adapter.ProductsAdapter;
import com.blinkreceipt.ocr.adapter.ProductsAdapter.onNoteListener;
import com.blinkreceipt.ocr.presenter.MainPresenter;
import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.BlinkReceiptSdk;
import com.microblink.CameraScanActivity;
import com.microblink.Media;
import com.microblink.core.Product;
import com.microblink.core.ScanResults;
import com.microblink.core.StringType;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,onNoteListener{

    public static final String SCAN_OPTIONS_EXTRA = "scanOptionsExtra";

    private static final int PERMISSIONS_REQUEST_CODE = 1000;

    private static final int CAMERA_SCAN_REQUEST_CODE = 1001;

    List<Product> products;

    private static final String[] requestPermissions = {
            Manifest.permission.CAMERA
    };

    private MainViewModel viewModel;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        presenter = new MainPresenter();

        TextView recieptTotal = findViewById(R.id.recieptTotal);

        final RecyclerView recyclerView = findViewById(R.id.products);

        final ProductsAdapter adapter = new ProductsAdapter(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, manager.getOrientation()));

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);

        viewModel.scanItems().observe(this, results -> {
            if (results != null) {
                if (presenter.exception(results)) {
                    Throwable e = results.e();

                    Toast.makeText(MainActivity.this, e != null ? e.toString() :
                            getString(R.string.no_products_found_on_receipt), Toast.LENGTH_LONG).show();

                    return;
                }

                 products = presenter.products(results);

                recieptTotal.setText(results.results().total().value()+"$");

                if (Utility.isNullOrEmpty(products)) {
                    Toast.makeText(MainActivity.this, R.string.no_products_found_on_receipt, Toast.LENGTH_SHORT).show();

                    return;
                }

                adapter.addAll(products);
            } else {
                Toast.makeText(MainActivity.this, R.string.no_products_found_on_receipt, Toast.LENGTH_SHORT).show();
            }
        });

        BlinkReceiptSdk.debug(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sdk_version) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.sdk_version_dialog_title)
                    .setMessage(BlinkReceiptSdk.versionName(this))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();

            return true;
        }

        if (item.getItemId() == R.id.camera) {
            if (EasyPermissions.hasPermissions(this, requestPermissions)) {
                startCameraScanForResult();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.permissions_rationale),
                        PERMISSIONS_REQUEST_CODE, requestPermissions);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                startCameraScanForResult();

                break;
            case CAMERA_SCAN_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        ScanResults results = data.getParcelableExtra(CameraScanActivity.DATA_EXTRA);

                        Media media = data.getParcelableExtra(CameraScanActivity.MEDIA_EXTRA);

                        viewModel.scanItems(new RecognizerResults(results, media));
                    } else {
                        viewModel.scanItems(new RecognizerResults(new Exception(getString(R.string.scan_results_error))));
                    }
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> permissions) {
        startCameraScanForResult();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> permissions) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, permissions)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void startCameraScanForResult() {
        startActivityForResult(new Intent(this, CameraActivity.class)
                .putExtra(SCAN_OPTIONS_EXTRA, viewModel.scanOptions()), CAMERA_SCAN_REQUEST_CODE);
    }

    @Override
    public void onNoteClick(int position) {

        Intent myIntent = new Intent(MainActivity.this, MainActivity2.class);
        String name = products.get(position).description().value().toString();
        myIntent.putExtra("productName", name); //Optional parameters
        Float price = products.get(position).unitPrice().value();
        myIntent.putExtra("productName", name);
        myIntent.putExtra("productPrice", String.valueOf(price));
        MainActivity.this.startActivity(myIntent);
        //Toast.makeText(MainActivity.this, products.get(position)+"", Toast.LENGTH_SHORT).show();
    }
}
