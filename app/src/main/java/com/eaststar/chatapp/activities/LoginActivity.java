package com.eaststar.chatapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eaststar.chatapp.PhoneLoginActivity;
import com.eaststar.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    EditText login_email, login_pass;
    TextView forget_pass_ling, need_new_account_link;
    Button login_btn, phone_login_button;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeFields();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        need_new_account_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
//                finish();
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInUser();
            }
        });

        phone_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void LogInUser() {
        if (validation()) {
            progressDialog.setTitle("Sign In");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            String email = login_email.getText().toString();
            String pass = login_pass.getText().toString();

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                progressDialog.dismiss();
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "user or password in valid.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }

    private void InitializeFields() {

        login_email = findViewById(R.id.login_email);
        login_pass = findViewById(R.id.login_pass);
        forget_pass_ling = findViewById(R.id.forget_pass_ling);
        login_btn = findViewById(R.id.login_btn);
        need_new_account_link = findViewById(R.id.need_new_account_link);
        phone_login_button = findViewById(R.id.phone_login_button);
        progressDialog = new ProgressDialog(LoginActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private Boolean validation() {
        Boolean i = true;
        if (TextUtils.isEmpty(login_email.getText().toString())) {
            Toast.makeText(LoginActivity.this, "Email cant be empty", Toast.LENGTH_LONG).show();
            i = false;
        }
        if (TextUtils.isEmpty(login_pass.getText().toString())) {
            Toast.makeText(LoginActivity.this, "Password cant be empty", Toast.LENGTH_LONG).show();
            i = false;
        } else if (login_pass.getText().toString().length() < 6) {
            Toast.makeText(LoginActivity.this, "Password is very week", Toast.LENGTH_LONG).show();
            i = false;
        }
        return i;
    }


}
