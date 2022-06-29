package com.example.foodorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodorder.Model.User;
import com.example.foodorder.common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    EditText editPhone, editName, editPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editName = (EditText)findViewById(R.id.editName);
        editPhone = (EditText)findViewById(R.id.editPhone);
        editPassword = (EditText)findViewById(R.id.editPassword);

        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        //        init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please waiting ....");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

//                        check if already user phone
                            if (snapshot.child(editPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone Number already register", Toast.LENGTH_LONG).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(editName.getText().toString(),
                                        editPassword.getText().toString());
                                table_user.child(editPhone.getText().toString()).setValue(user);

                                Toast.makeText(SignUp.this, "Sign Up successfully", Toast.LENGTH_LONG).show();

                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else{
                    Toast.makeText(SignUp.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}