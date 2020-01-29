package com.example.agripool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {


    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar);
        editText = findViewById(R.id.editTextCode);
        String phoneNumber = getIntent().getStringExtra("phonenumber");

        sendVerificationCode(phoneNumber);

        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Manual
                String code = editText.getText().toString().trim();

                if(code.isEmpty() || code.length()<6){
                    editText.setError("Enter Code...");
                    editText.requestFocus();
                    return;
                }


                verifyCode(code);
            }
        });
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){
                    Intent startIntent = new Intent(getApplicationContext(),HomeActivity.class);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(startIntent);
                    finish();
                    Toast.makeText(VerifyPhone.this, "Login Successfull!!",Toast.LENGTH_LONG ).show();

                }else {
                    Toast.makeText(VerifyPhone.this, task.getException().getMessage(),Toast.LENGTH_LONG ).show();
                }
            }
        });
    }

    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        //Toast.makeText(getApplicationContext(),"3111" ,Toast.LENGTH_SHORT ).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
        //Toast.makeText(getApplicationContext(),"4" ,Toast.LENGTH_SHORT ).show();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(getApplicationContext(),"Code Sent" ,Toast.LENGTH_SHORT ).show();
            verificationId = s;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                editText.setText(code);
               //Toast.makeText(getApplicationContext(),"1" ,Toast.LENGTH_SHORT ).show();
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhone.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    };
}
