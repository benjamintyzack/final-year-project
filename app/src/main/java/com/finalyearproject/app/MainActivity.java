package com.finalyearproject.app;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private TextView register;
    private EditText emailText, passwordText;
    private Button signIn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        emailText = (EditText) findViewById(R.id.emailAddress);
        passwordText = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        
        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.register:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.signIn:
                signInUser();
                break;
        }
    }

    private void signInUser() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if(email.isEmpty()) {
            emailText.setError("An email is required");
            emailText.requestFocus();
            return;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Please input a valid email address");
            emailText.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordText.setError("A password is required");
            passwordText.requestFocus();
            return;
        }

        progressBar.setVisibility(VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (!user.isEmailVerified()) {
                            user.sendEmailVerification();
                            Toast.makeText(MainActivity.this, "Check your email to verify account", Toast.LENGTH_LONG).show();
                        }
                        startActivity(new Intent(this, WorkoutPage.class));
                    }else {
                        Toast.makeText(MainActivity.this, "Failed to login, please try again", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(GONE);
                });
    }
}