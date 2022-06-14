package com.example.doan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpActivity extends AppCompatActivity {
    private Button btnSignUp;
    private EditText userName, email, pass, confirmPass;
    private ImageView btnback;
    private FirebaseAuth mAuth;
    private String emailStr, passStr,nameStr, confirmPassStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnSignUp = findViewById(R.id.btnSignUp);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirmPassword);
        btnback = findViewById(R.id.ivBack);
        mAuth = FirebaseAuth.getInstance();
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(validate()){
                   SignupUser();
               }

            }
        });

    }
    private boolean validate(){
         emailStr = email.getText().toString().trim();
         passStr = pass.getText().toString().trim();
        confirmPassStr = confirmPass.getText().toString().trim();
        nameStr = userName.getText().toString().trim();
        if(emailStr.isEmpty()){
            email.setError("enter email");
            return false;
        }
        if(passStr.isEmpty()){
            pass.setError("enter password");
            return false;
        }
        if(nameStr.isEmpty()){
            userName.setError("enter your name");
            return false;
        }
        if(confirmPassStr.isEmpty()){
            confirmPass.setError("enter password");
            return false;
        }
        if(passStr.compareTo(confirmPassStr)!=0){
            Toast.makeText(SignUpActivity.this,"password and confirm password should be same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void SignupUser(){
        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUpActivity.this,"Sign Up cuccessfull",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            SignUpActivity.this.finish();
                           // FirebaseUser user = mAuth.getCurrentUser();
                          } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignUpActivity.this, "Sign Up failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}