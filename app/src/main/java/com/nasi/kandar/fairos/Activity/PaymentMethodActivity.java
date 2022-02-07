package com.nasi.kandar.fairos.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.nasi.kandar.fairos.NothingSelectedSpinnerAdapter;
import com.example.hrithik.btp.R;

public class PaymentMethodActivity extends AppCompatActivity {

    private String custId = "", orderId = "", amountToPay = "";
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnContinue;
    Spinner tableSpinner;


    String[] tableItems = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19","20"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        Intent intent = getIntent();

        orderId = intent.getExtras().getString("orderid");
        custId = intent.getExtras().getString("custid");
        amountToPay = intent.getExtras().getString("amount_to_pay");

        addListenerOnButton();


        tableSpinner = (Spinner) findViewById(R.id.table);

        ArrayAdapter<CharSequence> adapter4 = new ArrayAdapter<CharSequence>(getApplicationContext(), R.layout.spinner_text, tableItems );
        adapter4.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        tableSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter4,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        this));
    }


    public void addListenerOnButton() {

        radioGroup = (RadioGroup) findViewById(R.id.radio);
        btnContinue = (Button) findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean valid = true;
                String tableSpinnerSelectedItem = (String) tableSpinner.getSelectedItem();
                if ( TextUtils.isEmpty(tableSpinnerSelectedItem)) {
                    TextView errorText = (TextView)tableSpinner.getSelectedView();
                    errorText.setError("Add one");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Choose one!");//changes the selected item text to this
                    valid = false;
                }
                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton = (RadioButton) findViewById(selectedId);

                SharedPreferences prefs = getSharedPreferences(
                        "tableData", Context.MODE_PRIVATE);
                if(valid) {
                    if (radioButton.getText().equals("Online Payment")) {

                        Intent intent = new Intent(PaymentMethodActivity.this, PayNow.class);
                        intent.putExtra("orderid", orderId);
                        intent.putExtra("custid", custId);
                        intent.putExtra("amount_to_pay", amountToPay);
                        prefs.edit().putString("tableNumber", tableSpinnerSelectedItem).apply();
                        startActivity(intent);
                    } else {

                        Intent intent = new Intent(PaymentMethodActivity.this, PaymentActivity.class);
                        intent.putExtra("orderid", orderId);
                        intent.putExtra("custid", custId);
                        intent.putExtra("payment", "Not Paid");
                        intent.putExtra("amount_to_pay", amountToPay);
                        prefs.edit().putString("tableNumber", tableSpinnerSelectedItem).apply();
                        startActivity(intent);

                    }
                    finish();
                }
            }

        });

    }
}
