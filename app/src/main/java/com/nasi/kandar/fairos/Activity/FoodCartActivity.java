package com.nasi.kandar.fairos.Activity;

import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.nasi.kandar.fairos.Adapters.CartAdapter;
import com.nasi.kandar.fairos.Helper.FoodCart;
import com.nasi.kandar.fairos.Model.FoodCartQtyHelper;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hrithik.btp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FoodCartActivity extends AppCompatActivity {

    private static final String TAG = "FoodCartActivity";
    private static ArrayList<FoodCartQtyHelper> cartList;
    private CartAdapter cartAdapter;
    private RecyclerView cartRecyclerView;
    private static TextView totalItemsTV;
    private static TextView totalCartAmountTV;
    private static TextView totalCartCalTV;
    private ImageView cartBackIcon;
    private Button checkoutBtn;
    private FirebaseAuth mAuth;
    private ConstraintLayout constraintLayout;


    private static String amountToPay = "";
    private static String amountOfCal = "";


    private String orderID = "", custID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_cart);


        cartRecyclerView = findViewById(R.id.food_cart_recycler_view);
        totalCartAmountTV = findViewById(R.id.total_cart_amount_tv);
        totalCartCalTV = findViewById(R.id.total_cart_cal_tv);

        totalItemsTV = findViewById(R.id.cart_total_qty_tv);
        cartBackIcon = findViewById(R.id.back_arrow_cart);
        checkoutBtn = findViewById(R.id.food_cart_checkout_btn);
        constraintLayout = findViewById(R.id.food_cart_constraint_layout);


        FoodCart cart = FoodCart.getInstance();
        if(cart.foodCartList.isEmpty()){
            checkoutBtn.setAlpha(0.5f);
            checkoutBtn.setEnabled(false);
        } else {
            checkoutBtn.setAlpha(1f);
            checkoutBtn.setEnabled(true);
        }



        cartBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cartList = cart.foodCartList;

        mAuth = FirebaseAuth.getInstance();

        cartAdapter = new CartAdapter(FoodCartActivity.this,FoodCartActivity.this, cartList);
        LinearLayoutManager cartManager = new LinearLayoutManager(FoodCartActivity.this, LinearLayoutManager.VERTICAL, false);
        cartRecyclerView.setLayoutManager(cartManager);
        cartRecyclerView.setAdapter(cartAdapter);

        updateCartDetails();




            checkoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mAuth.getCurrentUser() != null) {

                        custID = mAuth.getCurrentUser().getUid();
                        Log.d(TAG,"Customer ID: "+custID);
                        orderID = getAlphaNumericString(20);

                        Intent intent = new Intent(FoodCartActivity.this, PaymentMethodActivity.class);
                        intent.putExtra("orderid", orderID);
                        intent.putExtra("custid", custID);
                        intent.putExtra("amount_to_pay", amountToPay);
                        startActivity(intent);

                    }  else {
                        Snackbar snackbar = Snackbar.make(constraintLayout, "Please Sign In first.", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            });











    }

    public static void updateCartDetails(){
        FoodCart cart = FoodCart.getInstance();
        int size = 0;
        for(int i=0; i<cart.foodCartList.size(); i++){
            size+= cart.foodCartList.get(i).getQuantity();
        }

        totalItemsTV.setText("Total Items: "+size);
        int totalAmount = 0;
        for(int i=0; i<cartList.size(); i++){
            totalAmount+= (Integer.parseInt(cartList.get(i).getFood().getFoodPrice()) * cartList.get(i).getQuantity());
        }

        amountToPay = String.valueOf(totalAmount);

        totalCartAmountTV.setText("Amount To Be Paid: RM "+totalAmount);


        int totalCal = 0;
        for(int i=0; i<cartList.size(); i++){
            totalCal+= (Integer.parseInt(cartList.get(i).getFood().getFoodCal()) * cartList.get(i).getQuantity());
        }

        amountOfCal = String.valueOf(totalCal);
        totalCartCalTV.setText("Total Calories: "+totalCal);

    }

    // function to generate a random string of length n
    static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
