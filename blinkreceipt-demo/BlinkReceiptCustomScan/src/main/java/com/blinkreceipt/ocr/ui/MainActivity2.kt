package com.blinkreceipt.ocr.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blinkreceipt.ocr.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main2)

        val intent = intent
        val name: String = intent.getStringExtra("productName").toString() //if it's a string you stored.
        var price: String = intent.getStringExtra("productPrice").toString()

        val priceorig: TextView = findViewById(R.id.textView) as TextView
        priceorig.text ="( $" + price + " )"



        val productHeading: TextView = findViewById(R.id.ProductName) as TextView
        val productDescption: TextView = findViewById(R.id.prodectDescription) as TextView
        val productDescption2: TextView = findViewById(R.id.prodectDescription2) as TextView
        val productprice: TextView = findViewById(R.id.Price) as TextView
        val productprice2: TextView = findViewById(R.id.price2) as TextView

        price = ((price.toFloat() - price.toFloat()*0.2).toString())
        val price2 = ((price.toFloat() - price.toFloat()*0.3).toString())

        val number3digits:Double = String.format("%.2f", price.toFloat()).toDouble()
        val number2digits:Double = String.format("%.2f", price2.toFloat()).toDouble()

        productprice.text = number3digits.toString()
        productprice2.text = number2digits.toString()
        productHeading.text = name


        if(name.equals("TOMATO SAUCE")){
            productDescption.text = "The simplest tomato sauce consists just of chopped tomatoes cooked down (possibly with olive oil) and simmered until it loses its raw flavor. Of course, it may be seasoned with salt, or other herbs or spices."

        }

        if(name.equals("SPAGHETTI")){
            productDescption.text = "Spaghetti is a long, thin, solid, cylindrical pasta. It is a staple food of traditional Italian cuisine. Like other pasta, spaghetti is made of milled wheat and water and sometimes enriched with vitamins and minerals. Italian spaghetti is typically made from durum wheat semolina."
        }

        if(name.equals("COCONUT OIL")){
            productDescption.text = "Coconut oil is an edible oil derived from the wick, meat, and milk of the coconut palm fruit. Coconut oil is a white solid fat, melting at warmer room temperatures of around 25° C, in warmer climates during the summer months it is a clear thin liquid oil."
        }

        productDescption2.text = productDescption.text

    }

}




