package com.edfadsfxample.petik.kuker.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.edfadsfxample.petik.kuker.R;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView manis;
    private ImageView asin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        manis = (ImageView) findViewById(R.id.manis);
        asin = (ImageView) findViewById(R.id.asin);


        manis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "Kue Manis");
                startActivity(intent);
            }
        });

        asin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "Kue Asin");
                startActivity(intent);
            }
        });
    }
}
