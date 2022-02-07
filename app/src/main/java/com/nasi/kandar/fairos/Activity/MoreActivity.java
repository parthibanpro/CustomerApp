package com.nasi.kandar.fairos.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hrithik.btp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MoreActivity extends AppCompatActivity {
    String[] mobileArray = {"Profile","Rate Us","Contact Us",
            "Sign Out"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_more);

        ArrayAdapter adapter = new ArrayAdapter<String>(Objects.requireNonNull(getApplicationContext()),android.R.layout.simple_list_item_1, mobileArray);
        ListView listView =  findViewById(R.id.lv);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 3)
                {
                    FirebaseAuth.getInstance().signOut();
                    Intent otpv = new Intent(getApplicationContext(), LoginActivity.class);
                    otpv.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(otpv);


                } else if(position == 0){

                    startActivity(new Intent(getApplicationContext(), SetupActivity.class));

                } else if(position==1){
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                } else if(position==2){
                    String phone = "+601158455745";
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }



            }
        });
    }
}
