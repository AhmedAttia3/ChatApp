package com.eaststar.chatapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eaststar.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText login_email,login_pass;
    TextView already_have_account_link;
    Button create_account_btn;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitializeFields();
        mAuth = FirebaseAuth.getInstance();


        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });

        already_have_account_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
//                startActivity(i);
//                finish();
                onBackPressed();
            }
        });




    }

    private void CreateAccount() {
        if(validation()){
            progressDialog.setTitle("Creating a New Account");
            progressDialog.setMessage("Please wait, while we creating a new account for you...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            String email = login_email.getText().toString();
            String pass = login_pass.getText().toString();

            mAuth.createUserWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Account created successfully...", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(i);
                            }else {
                                String message = task.getException().getMessage().toString();
                                Toast.makeText(RegisterActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private Boolean validation() {
        Boolean i = true;
        if(TextUtils.isEmpty(login_email.getText().toString())){
            Toast.makeText(RegisterActivity.this,"Email cant be empty",Toast.LENGTH_LONG).show();
            i = false;
        }
        if(TextUtils.isEmpty(login_pass.getText().toString())){
            Toast.makeText(RegisterActivity.this,"Password cant be empty",Toast.LENGTH_LONG).show();
            i = false;
        }else if(login_pass.getText().toString().length()<6){
            Toast.makeText(RegisterActivity.this,"Password is very week",Toast.LENGTH_LONG).show();
            i = false;
        }
        return i;
    }

    private void InitializeFields() {

        login_email = findViewById(R.id.login_email);
        login_pass = findViewById(R.id.login_pass);
        already_have_account_link = findViewById(R.id.already_have_account_link);
        create_account_btn = findViewById(R.id.create_account_btn);
        progressDialog = new ProgressDialog(RegisterActivity.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }
}
