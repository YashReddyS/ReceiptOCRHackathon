package com.blinkreceipt.ocr.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.blinkreceipt.ocr.R;
import com.microblink.core.Product;
import com.microblink.core.internal.TypeValueUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView name;
    private final TextView total;
    private final TextView qauntity;
    private final TextView unitPrice;

    ProductsAdapter.onNoteListener onNoteListener;

    ProductsViewHolder(@NonNull View itemView, ProductsAdapter.onNoteListener onNoteListener) {
        super( itemView );

        name = itemView.findViewById( R.id.name );
        total = itemView.findViewById( R.id.total );
        qauntity = itemView.findViewById( R.id.quantity );
        unitPrice = itemView.findViewById( R.id.unitPrice );


        this.onNoteListener= onNoteListener;

        itemView.setOnClickListener(this);
    }

    void bind(@NonNull Product product) {
        Context context = itemView.getContext();
        ;

        name.setText( TypeValueUtils.value( product.description() ) );

        total.setText( context.getString( R.string.total_price, TypeValueUtils.value(product.totalPrice()) ) );


        String quantity = context.getString( R.string.total_price, TypeValueUtils.value(product.quantity()) );
        qauntity.setText( quantity );

        String unitprice = context.getString( R.string.total_price, TypeValueUtils.value(product.unitPrice()) );
        unitPrice.setText( unitprice);
    }

    @Override
    public void onClick(View view) {
        onNoteListener.onNoteClick(getBindingAdapterPosition());
    }
}
