package com.example.foodorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnSignUp, btnSignIn;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        txtSlogan = (TextView)findViewById(R.id.txtSlogan);

        btnSignIn.setOnClickListener(view -> {

        });


        btnSignUp.setOnClickListener(view -> {
            Intent signUp = new Intent(MainActivity.this, SignUp.class);
            startActivity(signUp);

        });
        btnSignIn.setOnClickListener(view -> {
            Intent signIn = new Intent(MainActivity.this, SignIn.class);
            startActivity(signIn);

        });
    }
}