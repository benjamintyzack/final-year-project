package com.finalyearproject.app;

import static android.view.View.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements OnClickListener {

    private TextView emailEditText, banner;
    private Button resetPasswordButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = (TextView) findViewById(R.id.emailAddress);
        resetPasswordButton = (Button) findViewById(R.id.resetPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        banner = (TextView) findViewById(R.id.banner);

        mAuth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(this);
        banner.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.resetPassword:
                resetPassword();
                break;

        }

    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if(email.isEmpty()) {
            emailEditText.setError("Email address is required");
            emailEditText.requestFocus();
            return;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please input a valid email address");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ForgotPassword.this, "Reset password email sent", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(ForgotPassword.this, "Failed to send email, please try again", Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(INVISIBLE);
        });
    }
}