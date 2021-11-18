package com.appz.qrcode.helperUi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appz.qrcode.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrActivity extends AppCompatActivity {
    private static final int STORAGE_CODE = 400;
    //ui
    Button genebtn;
    Button savebtn;
    EditText edtTxt;
    ImageView imgqr;

    //var
    String inputvale;
    String tag = "GenrateQrCode";
    BitmapDrawable bitmapDrawable;
    Bitmap img;
    String[] STORAGE_PERMISSSION;
    FileOutputStream outputStream;
    QRGEncoder qrgEncoder;
    boolean ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findview();

        generateQRCode();

        savebtn();

    }

    private void SetPermisssions() {

        STORAGE_PERMISSSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    private void findview() {
        SetPermisssions();
        ok = false;
        imgqr = findViewById(R.id.imgview);
        savebtn = findViewById(R.id.btnsave);
    }

    private void generateQRCode() {
        Intent intent = getIntent();
        inputvale = intent.getStringExtra("idCard");
        if (inputvale.length() > 0) {

            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int w = point.x;
            int h = point.y;
            int smallerone = w < h ? w : h;
            qrgEncoder = new QRGEncoder(inputvale, null, QRGContents.Type.TEXT, smallerone);

            try {
                img = qrgEncoder.encodeAsBitmap();
                imgqr.setImageBitmap(img);
                ok = true;
            } catch (Exception e) {
                Log.v(tag, e.toString());
                ok = false;
            }


        } else {
            edtTxt.setError("Required");
            ok = false;
        }
    }

    private void savebtn() {

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();

                    return;

                }


                if (ok) {
//                    bitmapDrawable = (BitmapDrawable) imgqr.getDrawable();
//                    img = bitmapDrawable.getBitmap();
//
//                    File filepath = Environment.getExternalStorageDirectory();
//                    File dir = new File(filepath.getAbsolutePath() + "/ration service/");
//                    dir.mkdir();
//
//                    File imdge = new File(dir, System.currentTimeMillis() + ".jpg");
//                    try {
//                        outputStream = new FileOutputStream(imdge);
//                        outputStream.flush();
//                        outputStream.close();
//                    } catch (FileNotFoundException e) {
//                       Log.d("eeeeeee1",e.getMessage()+" "+e.getCause());
//                    } catch (IOException e) {
//                        Log.d("eeeeeee2",e.getMessage()+" "+e.getCause());
//                        e.printStackTrace();
//                    }
//                    //img.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                    saveImage(img, FirebaseAuth.getInstance().getCurrentUser().getUid());

                } else {
                    edtTxt.setError("Required");
                    ok = false;
                }


            }
        });
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(QrActivity.this, STORAGE_PERMISSSION, STORAGE_CODE);
    }

    private boolean checkStoragePermission() {
        boolean r = ContextCompat.checkSelfPermission(QrActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return r;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {


            case STORAGE_CODE:
                if (grantResults.length > 0) {
                    boolean storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storage)
                        saveImage(img, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    else
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        String fname = "Image-" + image_name + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(getBaseContext(), "QR Saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage() + "", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }
}
