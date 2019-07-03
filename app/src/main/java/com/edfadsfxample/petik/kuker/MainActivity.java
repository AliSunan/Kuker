package com.edfadsfxample.petik.kuker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.edfadsfxample.petik.kuker.Model.Users;
import com.edfadsfxample.petik.kuker.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
{
    private Button joinBtn, loginBtn;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinBtn = (Button) findViewById(R.id.main_join_now_btn);
        loginBtn = (Button) findViewById(R.id.main_login_btn);
        loadingbar = new ProgressDialog(this);

        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String UserIdKey = Paper.book().read(Prevalent.UserIdKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        if (UserIdKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserIdKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserIdKey, UserPasswordKey);

                loadingbar.setTitle("Already Logged in");
                loadingbar.setMessage("Please wait.....");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
            }
        }
    }


    private void AllowAccess(final String id, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Users").child(id).exists()) {
                    Users usersData = dataSnapshot.child("Users").child(id).getValue(Users.class);

                    if (usersData.getId().equals(id))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Please wait, your already logged in...", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            loadingbar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorret.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Account with this " + id + " id don't exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
//                    Toast.makeText(LoginActivity.this, "you need to create a new Account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
