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
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements OnClickListener{

    private TextView banner2, register;
    private EditText editTextFullName, editTextEmail, editTextPassword, editConfirmPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner2 = (TextView) findViewById(R.id.banner2);
        banner2.setOnClickListener(this);

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.newUserPassword);
        editConfirmPassword = (EditText) findViewById(R.id.confirmUserPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.banner2:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.register:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if(fullName.isEmpty()) {
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            editTextPassword.setError("Password needs to be longer then 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if(!password.equals(confirmPassword)) {
            editTextPassword.setError("Please make sure both passwords are the same");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        User user = new User(fullName, email);

                        FirebaseDatabase.getInstance("https://finalyearproject-e1d79-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(this, MainActivity.class));
                                    }else {
                                        Toast.makeText(RegisterUser.this, "Failed to register user", Toast.LENGTH_LONG).show();
                                    }
                            progressBar.setVisibility(GONE);
                        });
                    }else {
                        Toast.makeText(RegisterUser.this, "Failed to authenticate user. Please try again", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(GONE);
                    }
                });
    }
}