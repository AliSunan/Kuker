package com.edfadsfxample.petik.kuker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edfadsfxample.petik.kuker.Admin.AdminCategoryActivity;
import com.edfadsfxample.petik.kuker.Model.Users;
import com.edfadsfxample.petik.kuker.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity
{
    private EditText InputId, InputPass;
    private Button logBtn;
    private TextView AdminLink, noAdminLink;
    private ProgressDialog loadingbar;


    private String parentDbName = "Users";
    private CheckBox cbRemember;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        logBtn = (Button) findViewById(R.id.main_login_btn);
        InputId = (EditText) findViewById(R.id.login_id);
        InputPass = (EditText) findViewById(R.id.login_password);
        AdminLink = (TextView) findViewById(R.id.login_admin_link);
        noAdminLink = (TextView) findViewById(R.id.login_not_admin_link);
        loadingbar = new ProgressDialog(this);

        cbRemember = (CheckBox) findViewById(R.id.login_remember_me);
        Paper.init(this);


        logBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }
        });


        AdminLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                logBtn.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                noAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });


        noAdminLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                logBtn.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                noAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }


    private void LoginUser()
    {
        String id = InputId.getText().toString();
        String password = InputPass.getText().toString();

        if (TextUtils.isEmpty(id))
        {
            Toast.makeText(this, "Please write you're Name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write you're password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please wait, while we are checking the credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();


            AllowAccessToAccount(id, password);
        }
    }


    public void saveInfo(View view)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", InputId.getText().toString());
        editor.putString("password", InputId.getText().toString());
        editor.apply();

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }


    private void AllowAccessToAccount(final String id, final String password)
    {
        if (cbRemember.isChecked())
        {
            Paper.book().write(Prevalent.UserIdKey, id);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(id).exists()) {
                    Users usersData = dataSnapshot.child(parentDbName).child(id).getValue(Users.class);

                    if (usersData.getId().equals(id))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Welocome admin, your logged is successfully...", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "Logged is successfully...", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingbar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorret.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account with this " + id + " id don't exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    //Toast.makeText(LoginActivity.this, "you need to create a new Account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
