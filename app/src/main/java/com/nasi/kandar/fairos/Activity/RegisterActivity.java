package com.nasi.kandar.fairos.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hrithik.btp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText name_et;
    private EditText number_et;

    private EditText reg_email;
    private EditText reg_pass;
    private EditText reg_confirm_pass;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressDialog reg_progress;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Importing UI
        name_et = findViewById(R.id.etxt_name);
        number_et = findViewById(R.id.etxt_phone);
        reg_email = findViewById(R.id.etxt_email);
        reg_pass = findViewById(R.id.etxt_password);
        reg_confirm_pass = findViewById(R.id.etxt_confirm_password);
        reg_btn = findViewById(R.id.create_new_account_btn);
        reg_login_btn = findViewById(R.id.already_have_account_btn);

        reg_progress = new ProgressDialog(this);


        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = reg_email.getText().toString();
                String pass = reg_pass.getText().toString();
                String confirm_pass = reg_confirm_pass.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass))
                {
                    if(pass.equals(confirm_pass))
                    {

                        reg_progress.setTitle("Creating New Account");
                        reg_progress.setMessage("Please wait while we create your new account !");
                        reg_progress.setCanceledOnTouchOutside(false);
                        reg_progress.show();

                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser userID = mAuth.getCurrentUser();

                                if(task.isSuccessful())
                                {


                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("name",name_et.getText().toString().trim());
                                    userMap.put("number",number_et.getText().toString().trim());
                                    userMap.put("dob","1/1/2000");
                                    userMap.put("gender","Male");
                                    userMap.put("image","default");
                                    userMap.put("thumb_image","default");

                                    firebaseFirestore.collection("Users").document(userID.getUid())
                                            .set(userMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(RegisterActivity.this, "Registered Successfully..", Toast.LENGTH_SHORT).show();
                                                    sendToMain();
                                                    Log.d("TAG", "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("TAG", "Error writing document", e);
                                                }
                                            });


                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error : "+errorMessage,Toast.LENGTH_LONG).show();

                                }

                                reg_progress.dismiss();
                            }
                        });

                    } else {

                        Toast.makeText(RegisterActivity.this,"Confirm Password and Password doesn't match! Please try Again.",Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(RegisterActivity.this,"Fill up all the details.",Toast.LENGTH_LONG).show();
                }
            }
        });





        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            sendToMain();
        }
    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this,FoodMenuActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }
}
