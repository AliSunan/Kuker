package com.edfadsfxample.petik.kuker.Admin;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.edfadsfxample.petik.kuker.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class AdminAddProductActivity extends AppCompatActivity
{

    private String CatagoryName, Description, Price, ProdukName, saveCurrentDate, saveCurrentTime;
    private Button AddProduk;
    private ImageView InputImage;
    private EditText InputProdukName, InputProdukDesc, InputProdukPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        CatagoryName = getIntent().getExtras().get("category").toString();
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Products Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        AddProduk = (Button) findViewById(R.id.produk_create);
        InputImage = (ImageView) findViewById(R.id.select_image);
        InputProdukName = (EditText) findViewById(R.id.produk_name);
        InputProdukDesc = (EditText) findViewById(R.id.produk_desc);
        InputProdukPrice = (EditText) findViewById(R.id.produk_price);
        loadingbar = new ProgressDialog(this);
        ;


        InputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddProduk.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                ValidateProdukData();
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            InputImage.setImageURI(ImageUri);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ValidateProdukData() {
        Description = InputProdukDesc.getText().toString();
        Price = InputProdukPrice.getText().toString();
        ProdukName = InputProdukName.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(this, "Products Image is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Please write product price...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ProdukName)) {
            Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }

    }


    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void StoreProductInformation() {
        loadingbar.setTitle("Adding New Products");
        loadingbar.setMessage("Dear Admin, please wait while we are adding new product.");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filepath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddProductActivity.this, "Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddProductActivity.this, "got the Products image Url Successfully", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CatagoryName);
        productMap.put("price", Price);
        productMap.put("produkname", ProdukName);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(AdminAddProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingbar.dismiss();
                            Toast.makeText(AdminAddProductActivity.this, "Products is added successfully...", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingbar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
