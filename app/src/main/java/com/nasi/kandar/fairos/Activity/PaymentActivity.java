package com.nasi.kandar.fairos.Activity;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hrithik.btp.R;

public class PaymentActivity extends AppCompatActivity {

    private String custId = "", orderId = "", payment = "", amountToPay = "", tableNo = "";

    private static String TAG = "PaymentActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();

        orderId = intent.getStringExtra("orderid");
        custId = intent.getStringExtra("custid");
        amountToPay = intent.getStringExtra("amount_to_pay");
        payment = intent.getStringExtra("payment");

        //sendUserDetailToServer d1 = new sendUserDetailToServer();
        //d1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Intent statusIntent = new Intent(PaymentActivity.this, PaymentOrderStatusActivity.class);
        statusIntent.putExtra("status","TXN_SUCCESS");
        statusIntent.putExtra("payment", payment);
        statusIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(statusIntent);
        finish();
    }

}
