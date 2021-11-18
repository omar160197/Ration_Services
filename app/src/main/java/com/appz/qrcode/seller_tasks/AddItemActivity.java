package com.appz.qrcode.seller_tasks;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appz.qrcode.R;
import com.appz.qrcode.helperUi.AllFinal;
import com.appz.qrcode.seller_tasks.models.ItemModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {
    private static final int CAMERA_CODE = 200;
    private static final int STORAGE_CODE = 400;
    private static final int PICK_CAMERA_CODE = 1001;
    private static final int PICK_STORAGE_CODE = 1000;
    private static String name, img_url = "";
    // ui
    private EditText ed_name, ed_point, ed_num_item;
    private ProgressDialog progressDialog;
    private Button btnAdd;
    private ImageView img;
    // var
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference(AllFinal.STORE_ITEMS);
    private FirebaseStorage mStorageRef = FirebaseStorage.getInstance();
    private StorageReference imagesRef;
    private int num_item;
    private double points;
    private String[] CAMERA_PERMISSION;
    private String[] STORAGE_PERMISSSION;
    private boolean is_ubdate = false;

    private ItemModel itemModel2;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);


        buildUi();
    }


    private void checkIsUpdate(Bundle intent) {
        Log.d("wwwwwwww", intent.getBoolean(AllFinal.UPDATE) + "");
        if (intent.getBoolean(AllFinal.UPDATE)) {
            is_ubdate = intent.getBoolean(AllFinal.UPDATE);
        }

        if (is_ubdate) {
            btnAdd.setText("Update");
            itemModel2 = intent.getParcelable("item");

            ed_name.setText(itemModel2.getName());
            ed_num_item.setText(itemModel2.getNumber_units() + "");
            ed_point.setText(itemModel2.getPoint() + "");

            if (itemModel2.getImg_url().equals("")) {
                Glide.with(getApplicationContext())
                        .load(getResources().getDrawable(R.drawable.img_no))
                        .into(img);
            } else {
                Glide.with(getApplicationContext())
                        .load(itemModel2.getImg_url())
                        .into(img);
            }

        }
    }

    private Boolean checkInternetConnection() {
        Boolean internetConnection = false;
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connection = manager.getActiveNetworkInfo();

        if (connection != null) {
            if (connection.getType() == ConnectivityManager.TYPE_WIFI)
                return internetConnection = true;
            else if (connection.getType() == ConnectivityManager.TYPE_MOBILE)
                return internetConnection = true;
            else
                return internetConnection = false;
        }

        return internetConnection;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buildUi() {
        StorageReference storageRef = mStorageRef.getReference();
        imagesRef = storageRef;
        btnAdd = findViewById(R.id.btn_add);

        progressDialog = new ProgressDialog(AddItemActivity.this);
        progressDialog.setMessage("Wait Please");
        //mStorageRef = FirebaseStorage.getInstance().getReference();
        ed_name = findViewById(R.id.ed_name_unit_store);
        ed_num_item = findViewById(R.id.ed_all_unit_store);
        ed_point = findViewById(R.id.ed_point_unit_store);
        img = findViewById(R.id.img_unit_store);
        img.setClipToOutline(true);

        if (!getIntent().getStringExtra("bundle1").equals("")) {
            checkIsUpdate(getIntent().getExtras().getBundle("bundle"));
        }


        SetPermisssions();
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(AddItemActivity.this, CAMERA_PERMISSION, CAMERA_CODE);
    }

    private void SetPermisssions() {
        CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        STORAGE_PERMISSSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void showProgress(boolean s) {
        if (s == true) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    public void addNewItem(View view) {

        if (!checkInternetConnection()) {
            Toast.makeText(this, "check internet connection ", Toast.LENGTH_SHORT).show();
            return;

        }

        if (TextUtils.isEmpty(ed_name.getText()) || TextUtils.isEmpty(ed_num_item.getText()) || TextUtils.isEmpty(ed_point.getText())) {
            Toast.makeText(this, "fill all fields first please !", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress(true);
        name = ed_name.getText().toString().trim();
        points = Double.parseDouble(ed_point.getText().toString());
        num_item = Integer.parseInt(ed_num_item.getText().toString());


        ItemModel model = new ItemModel(name, img_url, points
                , num_item);
        if (!is_ubdate) {
            reference.child(mAuth.getCurrentUser().getUid())
                    .child(AllFinal.ITEMS)
                    .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddItemActivity.this, "success added", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    showProgress(false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("wwwwwwwwfire", e.getMessage());
                    Toast.makeText(AddItemActivity.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            });
        } else {

            if (img_url.equals("")) {
                model.setImg_url(itemModel2.getImg_url());

            } else {
                model.setImg_url(img_url);

            }

            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put(itemModel2.getId(), model);


            reference.child(mAuth.getCurrentUser().getUid())
                    .child(AllFinal.ITEMS)
                    .updateChildren(modelMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddItemActivity.this, "success updated", Toast.LENGTH_SHORT).show();

                    showProgress(false);
                    onBackPressed();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddItemActivity.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                    showProgress(false);

                }
            });

        }


    }

    private boolean checkStoragePermission() {
        boolean r = ContextCompat.checkSelfPermission(AddItemActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return r;
    }

    public void select(View view) {
        if (!checkInternetConnection()) {
            Toast.makeText(this, "check internet connection ", Toast.LENGTH_SHORT).show();
            return;

        }
        if (!checkStoragePermission())
            requestStoragePermission();
        else
            pickGallery();


    }

    private void uploadFile(Bitmap bitmap, final Uri uri2) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://graduation-project-12577.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("images/" + mAuth.getCurrentUser().getUid() + SystemClock.currentThreadTimeMillis() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showProgress(false);
                Toast.makeText(AddItemActivity.this, exception.getCause() + "", Toast.LENGTH_SHORT).show();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        showProgress(false);
                        Glide.with(getApplicationContext())
                                .load(uri2)
                                .placeholder(R.drawable.img_no)
                                .into(img);
                        Toast.makeText(AddItemActivity.this, "succesc upload image", Toast.LENGTH_SHORT).show();
                        img_url = uri.toString();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgress(false);
                        Toast.makeText(AddItemActivity.this, e.getCause() + "", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_STORAGE_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(AddItemActivity.this, STORAGE_PERMISSSION, STORAGE_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {


            case STORAGE_CODE:
                if (grantResults.length > 0) {
                    boolean storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storage)
                        pickGallery();
                    else
                        Toast.makeText(AddItemActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_STORAGE_CODE) {

                Uri uri = data.getData();


                showProgress(true);

//                Uri builder = new Uri.Builder().appendPath(mAuth.getCurrentUser()
//                        .getUid()).appendPath(uri.toString()).appendPath(SystemClock.currentThreadTimeMillis() + "").build();
//
//                img_url = builder.toString();
                img_url = uri.toString();

                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    uploadFile(bitmap, imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }
    }
}
