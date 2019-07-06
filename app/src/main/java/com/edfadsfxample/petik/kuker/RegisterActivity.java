package com.edfadsfxample.petik.kuker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private Button CreateAccountButton;
    private EditText InputId, InputPassword, InputPhoneNumber;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButton = (Button) findViewById(R.id.create_btn);
        InputId = (EditText) findViewById(R.id.create_id);
        InputPassword = (EditText) findViewById(R.id.create_password);
        InputPhoneNumber = (EditText) findViewById(R.id.create_phone_number);
        loadingbar = new ProgressDialog(this);


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }

        });
    }


    private void CreateAccount() {
        String id = InputId.getText().toString();
        String password = InputPassword.getText().toString();
        String phone = InputPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "Please write your id...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        } else {
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please wait, while we are checking the credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidateId(id, password, phone);
        }
    }

    private void ValidateId(final String id, final String password, final String phone) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(id).exists())) {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("id", id);
                    userDataMap.put("password", password);
                    userDataMap.put("phone", phone);

                    RootRef.child("Users").child(id).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();


                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingbar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network error : Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "This " + id + " already exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another your email.", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
