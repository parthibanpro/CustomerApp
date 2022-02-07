package com.nasi.kandar.fairos.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nasi.kandar.fairos.Helper.FoodCart;
import com.example.hrithik.btp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentOrderStatusActivity extends AppCompatActivity {

    private ImageView paymentStatusIV;
    private TextView paymentStatusTV;
    private Button backToMenuBtn;
    private ConstraintLayout layout;
    private FirebaseFirestore firebaseFirestore;
    private FoodCart cart;
    private FirebaseAuth mAuth;
    private String userID;
    private ProgressDialog order_progress;



    private static String TAG = "PaymentOrderStatusActivity";

    private static String paymentStatus = "";
    private String payment = "";
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_order_status);
        prefs = getSharedPreferences(
                "tableData", Context.MODE_PRIVATE);
        Intent intent = getIntent();

        payment = intent.getStringExtra("payment");

        paymentStatusTV = findViewById(R.id.payment_status_tv);
        paymentStatusIV = findViewById(R.id.payment_status_icon);
        backToMenuBtn = findViewById(R.id.payment_status_back_menu_btn);
        layout = findViewById(R.id.payment_status_parent_cl);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        paymentStatus = getIntent().getStringExtra("status");

        cart = FoodCart.getInstance();
        FoodCart.displayCart();

        firebaseFirestore = FirebaseFirestore.getInstance();


        //cart.foodCartList.clear();


        if(paymentStatus.equals("TXN_SUCCESS")){
            paymentStatusIV.setImageResource(R.drawable.payment_success_check_icon);
            paymentStatusTV.setText("Order Sent to staffs, Please wait.. we will prepare your food within 45 minutes after confirm your order");
            layout.setBackground(getDrawable(R.drawable.payment_status_success_border));

            sendOrderToServer();
            cart.foodCartList.clear();
        } else {
            paymentStatusIV.setImageResource(R.drawable.payment_status_failed_icon);
            paymentStatusTV.setText("Failed to food order, please contact to manager");
            layout.setBackground(getDrawable(R.drawable.payment_status_failed_border));
            cart.foodCartList.clear();
        }

        backToMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentOrderStatusActivity.this, FoodMenuActivity.class));
                finish();
            }
        });

    }


    private String orderDocID = null;

    private void sendOrderToServer() {

        final Map<String, Object> orderMap = new HashMap<>();
        int total_amt = 0;

        final ArrayList<String> orderDetailsArray = new ArrayList<>();
        for(int i=0; i<cart.foodCartList.size(); i++){

            String foodID = cart.foodCartList.get(i).getFood().getFoodCode();
            String foodCuisineCode = cart.foodCartList.get(i).getFood().getFoodCuisineCode();
            String foodImage = cart.foodCartList.get(i).getFood().getFoodImage();
            String foodName = cart.foodCartList.get(i).getFood().getFoodName();
            String foodPrice = cart.foodCartList.get(i).getFood().getFoodPrice();
            String foodQty = String.valueOf(cart.foodCartList.get(i).getQuantity());
            String foodDocId = cart.foodCartList.get(i).getFood().getFoodDocID();
            total_amt+= (Integer.parseInt(cart.foodCartList.get(i).getFood().getFoodPrice()) * cart.foodCartList.get(i).getQuantity());

            String cartDetailTxt = "#FoodName: "+foodName+"#FoodQty: "+foodQty+"#FoodPrice: "+foodPrice+"#FoodID: "+foodID+"#FoodCuisineCode: "+foodCuisineCode+"#FoodImage: "+foodImage+"#FoodDocId: "+foodDocId+"#Comment: null#Response: null";
            orderDetailsArray.add(cartDetailTxt);


        }


        try{

            order_progress = new ProgressDialog(this);

            order_progress.setTitle("Placing Order");
            order_progress.setMessage("Please wait while we place your order !");
            order_progress.setCanceledOnTouchOutside(false);
            order_progress.setCancelable(false);
            order_progress.show();


            final int finalTotal_amt = total_amt;
            firebaseFirestore.collection("Users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){
                        if(task.getResult().exists()){

                            final String name = task.getResult().getString("name");
                            final String number = task.getResult().getString("number");
                            final String uid = mAuth.getCurrentUser().getUid();
                            final String thumb_image = task.getResult().getString("thumb_image");


                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            final Date date = new Date();


                            orderMap.put("orderDetails", orderDetailsArray);
                            orderMap.put("name",name);
                            orderMap.put("number",number);
                            orderMap.put("uid",uid);
                            orderMap.put("customer_thumb_image",thumb_image);
                            orderMap.put("amount_paid", finalTotal_amt);
                            orderMap.put("timestamp",date.getTime());
                            orderMap.put("order_status","pending");


                            firebaseFirestore.collection("Pending Orders").add(orderMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                    if(task.isSuccessful()){
                                        orderDocID = task.getResult().getId();
                                        addOrderDataToUserDatabase(orderMap);
                                       // order_progress.dismiss();
                                       // Toast.makeText(PaymentOrderStatusActivity.this, "Order Placed !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        } else {
                            order_progress.dismiss();
                            Log.e("error1",""+task.getException());
                            Toast.makeText(PaymentOrderStatusActivity.this, "Oops! Something went wrong,1", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        order_progress.dismiss();
                        Toast.makeText(PaymentOrderStatusActivity.this, "Oops! Something went wrong,", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } catch (Exception e){
            order_progress.dismiss();
            Toast.makeText(PaymentOrderStatusActivity.this, "Oops! Something went wrong,", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Exception: "+e.getMessage());
        }


    }

    private String custOrderDocId = null;

    private void addOrderDataToUserDatabase(Map<String, Object> orderMap) {

        firebaseFirestore.collection("Users").document(userID).collection("Orders").add(orderMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){
                  custOrderDocId  = task.getResult().getId();
                    Log.d(TAG,"docID: "+orderDocID+" : custDocID: "+custOrderDocId);

                   // Toast.makeText(PaymentOrderStatusActivity.this, "Order Placed !", Toast.LENGTH_SHORT).show();
                    updatePendingOrderWithIDS(orderDocID, custOrderDocId);
                } else {
                    Toast.makeText(PaymentOrderStatusActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                }
                order_progress.dismiss();
            }
        });


        //firebaseFirestore.collection()


    }

    private void updatePendingOrderWithIDS(String orderDocID, String custOrderDocId) {

        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("order_doc_id",orderDocID);
        updateMap.put("cust_order_doc_id",custOrderDocId);
        updateMap.put("payment",payment);
        updateMap.put("tableNumber",prefs.getString("tableNumber", "0"));

        try {
            firebaseFirestore.collection("Pending Orders").document(orderDocID).update(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(PaymentOrderStatusActivity.this, "Order Placed !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PaymentOrderStatusActivity.this, "OOPS! Error Occured.", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e){
            Log.d(TAG, e.getMessage());
            Toast.makeText(this, "OOPS! Error Occured.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        // Do Nothing....
    }
}
