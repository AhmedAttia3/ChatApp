package com.eaststar.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eaststar.chatapp.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    EditText phoneNumberInput, codeInput;
    Button sendCodeBtn, verifyBtn;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks poneAuthCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        codeInput = findViewById(R.id.codeInput);
        sendCodeBtn = findViewById(R.id.sendCodeBtn);
        verifyBtn = findViewById(R.id.verifyBtn);
        progressDialog = new ProgressDialog(PhoneLoginActivity.this);

        poneAuthCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneLoginActivity.this, "invalid phone number, please enter write phone number with your country code ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                sendCodeBtn.setVisibility(View.VISIBLE);
                phoneNumberInput.setVisibility(View.VISIBLE);
                codeInput.setVisibility(View.INVISIBLE);
                verifyBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("phoneAuth", "onCodeSent:" + verificationId);
                Toast.makeText(PhoneLoginActivity.this, "code has been send", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


                sendCodeBtn.setVisibility(View.INVISIBLE);
                phoneNumberInput.setVisibility(View.INVISIBLE);
                codeInput.setVisibility(View.VISIBLE);
                verifyBtn.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                // ...
            }
        };

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = phoneNumberInput.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneLoginActivity.this, "phone number is required..", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Phone Verification");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            poneAuthCallbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = codeInput.getText().toString();
                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(PhoneLoginActivity.this, "please enter verify code", Toast.LENGTH_SHORT).show();
                } else {

                    progressDialog.setTitle("Verification Code");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("phoneAuth", "signInWithCredential:success");
                            progressDialog.dismiss();
                            FirebaseUser user = task.getResult().getUser();
                            Intent i = new Intent(PhoneLoginActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("phoneAuth", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                            progressDialog.dismiss();
                            String m = task.getException().getMessage().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error :" + m, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
