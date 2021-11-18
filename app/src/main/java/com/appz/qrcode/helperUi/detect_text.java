package com.appz.qrcode.helperUi;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.appz.qrcode.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class detect_text extends AppCompatActivity {

    // variables
    private static final int CAMERA_CODE = 200;
    private static final int STORAGE_CODE = 400;
    private static final int PICK_CAMERA_CODE = 1001;
    private static final int PICK_STORAGE_CODE = 1000;
    public static String id, name;
    private final DatabaseReference generatedTable = FirebaseDatabase.getInstance().getReference().child(AllFinal.Generated);
    private final DatabaseReference fakeTable = FirebaseDatabase.getInstance().getReference().child(AllFinal.FAKE_DATA);
    ImageView imageView;
    EditText ed_id, ed_name;
    Button add;
    ProgressDialog progressDialog;
    boolean generated = false;
    List ids = new ArrayList();
    List correctIds = new ArrayList();

    // arrays
    String[] CAMERA_PERMISSION;
    String[] STORAGE_PERMISSSION;
    //database var
    private DatabaseReference rationTable = FirebaseDatabase.getInstance().getReference().child(AllFinal.Ration_Data);
    //UI
    private Button btn_create_qrcode;
    private Uri image_uri;
    private FirebaseUser CurrentUser;
    private boolean Correct;
    private Boolean AlreadyExist;

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_text);

        findByid();
        SetPermisssions();

        generateQR();


    }

    @Override
    protected void onStart() {
        super.onStart();
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        isGenerated(generatedTable);
    }

    private void SetPermisssions() {
        CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        STORAGE_PERMISSSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void findByid() {
        btn_create_qrcode = findViewById(R.id.btn_selectImg_qrcode2);
        imageView = findViewById(R.id.img_unit_qr);
        imageView.setClipToOutline(true);
        add = findViewById(R.id.btn_add_all);
        ed_id = findViewById(R.id.ed_id);
        ed_name = findViewById(R.id.ed_name);
        progressDialog = new ProgressDialog(detect_text.this);
        progressDialog.setMessage("Please Wait ...");
        selectAndDetectImgForeQrCode();
    }

    private void showDialog() {
        String[] options = {"CAMERA", "GALLERY"};
        AlertDialog.Builder builder = new AlertDialog.Builder(detect_text.this);


        builder.setTitle("upload image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {
                            if (!checkCameraPermission())
                                requestCameraPermission();
                            else {

                            }

                        } else if (i == 1) {
                            if (!checkStoragePermission())
                                requestStoragePermission();
                            else
                                pickGallery();
                        }

                    }
                })
                .create().show();
    }

    private void msgDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(detect_text.this);
        builder.setMessage(s);
        builder.create();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_STORAGE_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(detect_text.this, CAMERA_PERMISSION, CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(detect_text.this, STORAGE_PERMISSSION, STORAGE_CODE);
    }

    private boolean checkCameraPermission() {
        boolean r1 = ContextCompat.checkSelfPermission(detect_text.this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean r2 = ContextCompat.checkSelfPermission(detect_text.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return r1 && r2;
    }

    private boolean checkStoragePermission() {
        boolean r = ContextCompat.checkSelfPermission(detect_text.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return r;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_CODE:
                if (grantResults.length > 0) {
                    boolean cam = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cam && storage) {

                    } else
                        Toast.makeText(detect_text.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

                break;

            case STORAGE_CODE:
                if (grantResults.length > 0) {
                    boolean storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storage)
                        pickGallery();
                    else
                        Toast.makeText(detect_text.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 40) {
            Uri chosenImageUri = data.getData();
            Glide.with(getApplicationContext())
                    .load(chosenImageUri.toString())
                    .placeholder(R.drawable.img_no)
                    .into(imageView);

            Bitmap mBitmap = null;
            try {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(getContentResolver().openInputStream(chosenImageUri));
                } catch (IOException e) {
                    Toast.makeText(this, e.getCause() + "", Toast.LENGTH_LONG).show();
                    return;
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
                Bitmap bmRotated = rotateBitmap(mBitmap, orientation);


                recognizeText(bmRotated);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = activityResult.getUri();
//
//                imageView.setImageURI(resultUri);
//
//
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
//                Bitmap bitmap =  bitmapDrawable.getBitmap();
//
//                TextRecognizer textRecognizer = new TextRecognizer.Builder(
//                        getApplicationContext()).build();
//
//                if (!textRecognizer.isOperational())
//                    Toast.makeText(detect_text.this, "Error", Toast.LENGTH_SHORT).show();
//                else {
//                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
//
//
//
//                    StringBuilder sb = new StringBuilder();
//
//
//                    if(items.size()==0)
//                        Toast.makeText(detect_text.this , "no text found" , Toast.LENGTH_SHORT).show();
//                    else
//                    {
//                        for (int i = 0; i < items.size(); i++) {
//                            TextBlock textBlock = items.valueAt(i);
//                            sb.append(textBlock.getValue());
//                            //sb.append("\n");
//                        }
//
//                        String text = sb.toString().trim();
//                        String temp ="";
//
//                        // id text
//                        for(int i = 0 ; i < text.length()-2;++i)
//                        {
//
//                            if(text.charAt(i)=='I'&&text.charAt(i+1)=='D'&&text.charAt(i+2)==' ')
//                            {
//                                int x = i+3;
//                                for (int b = i+3;b-x<17;b++) {
//                                    temp+=text.charAt(b);
//                                }
//
//                                break;
//                            }
//                        }
//                        ed_id.setText(temp);
//
//
//
//                        // name text
//                        temp ="";
//                        for(int i = 0 ; i < text.length()-3;++i)
//                        {
//
//                            if(text.charAt(i)=='N'&&text.charAt(i+1)=='a'&&text.charAt(i+2)=='m'&&text.charAt(i+3)=='e')
//                            {
//
//                                for (int b = i+4;;b++) {
//
//                                    if(text.charAt(b)=='A'&&text.charAt(b+1)=='d'&&text.charAt(b+2)=='d'&&
//                                            text.charAt(b+3)=='r'&&text.charAt(b+4)=='e'&&text.charAt(b+5)=='s'&& text.charAt(b+6)=='s')
//                                        break;
//                                    temp+=text.charAt(b);
//                                }
//
//                                break;
//                            }
//                        }
//                        ed_name.setText(temp);
//
//
//                    }
//
//                }
//            }
//            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//
//                Exception exception = activityResult.getError();
//                Toast.makeText(detect_text.this , ""+exception , Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    private void recognizeText(Bitmap bitmap) {
        progressDialog.show();
        //تحويل بيتماب الى بيتماب قابل للتعديل
        final Bitmap mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        final Canvas canvas = new Canvas(mBitmap);
        //الحصول على الكود
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        // انتهى بنجاح
//                        Paint redPaint = getPaint(Color.RED, .5f);
//                        Paint blackPaint = getPaint(Color.BLACK, .5f);
//                        Paint bluePaint = getPaint(Color.CYAN, .5f);
                        String text = "";

                        //تجلب البلوكس
                        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                            //احدثيات النص
                            Rect boundingBox = block.getBoundingBox();
                            //Padding


                            boundingBox.top = boundingBox.top - 5;
                            boundingBox.bottom = boundingBox.bottom + 5;
                            //رسم مستطيل احمر حول النص
                            //canvas.drawRect(boundingBox,redPaint);
                            //تجلب الاسطر داخل كل بلوك
                            for (FirebaseVisionText.Line line : block.getLines()) {
                                //اخد كل سطر و الانتقال للسطر التالي
                                text += line.getText() + "\n";
                                //Padding
                                line.getBoundingBox().top = line.getBoundingBox().top - 2;
                                line.getBoundingBox().bottom = line.getBoundingBox().bottom + 2;
                                line.getBoundingBox().right = line.getBoundingBox().right + 2;
                                //رسم مستطيل اسود حول كل سطر
                                // canvas.drawRect(line.getBoundingBox(), blackPaint);
                                //تجلب العناصر او الكلمات داخل كل سطر
                                for (FirebaseVisionText.Element element : line.getElements()) {
                                    //رسم مستطيل ازرق حول كل كلمة
                                    // canvas.drawRect(element.getBoundingBox(), bluePaint);
                                }
                            }
                        }
                        //اضافة النص للعنصر
                        String[] split1 = text.split("\n");

                        for (String s : split1) {
                            Log.d("wwwwwwww111", s);
                        }
                        if (split1.length <= 0 || split1.length < 3) {
                            progressDialog.dismiss();
                            Toast.makeText(detect_text.this, "failed image change image and try again", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String textwithid = split1[2];

//                        if (split2.length <= 0 || split2.length < 2) {
//                            progressDialog.dismiss();
//                            Toast.makeText(detect_text.this, "failed image change image and try again", Toast.LENGTH_SHORT).show();
//                            return;
//                        }


                        Log.d("wwwwwwwwllllllll", textwithid.length() + " ");
                        if (!isNumeric(textwithid) || textwithid.length() != 14) {
                            progressDialog.dismiss();
                            Toast.makeText(detect_text.this, "failed image change image and try again", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ed_name.setText(split1[4]);
                        ed_id.setText(textwithid);
                        progressDialog.dismiss();
                        //وضع الصورة على العنصر
                        // imageView.setImageBitmap(mBitmap);


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // خطا ما وقع
                Toast.makeText(getApplicationContext(), "Sorry, something went wrong!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


    }

    private void generateQR() {

        add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                WorkManager.getInstance().cancelAllWork();
                Toast.makeText(detect_text.this, "Rest points is canceled", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(ed_id.getText().toString().trim()) || TextUtils.isEmpty(ed_name.getText().toString().trim())) {
                    Toast.makeText(detect_text.this, "select image first and try again", Toast.LENGTH_SHORT).show();
                    return;
                }
                id = ed_id.getText().toString().trim();
                name = ed_name.getText().toString().trim();
                Correct = false;
                AlreadyExist = false;


                progressDialog.show();


                if (generated) {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "You Can Generate one Qr Code", Toast.LENGTH_LONG).show();

                } else {

                    isCorrect(fakeTable);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (Correct) {
                                isAlreadyExist(rationTable);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (AlreadyExist) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(), "This ID is Already Generated Before", Toast.LENGTH_LONG).show();
                                        } else {
                                            rationTable.child(id).child("name").setValue(name);
                                            rationTable.child(id).child("uid").setValue(CurrentUser.getUid());
                                            SharedPreferences preferences = getSharedPreferences("id", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("id", id);
                                            editor.commit();

                                            setAndRestPoints();
                                            rationTable.child(id).child("points").setValue(50.0);
                                            generatedTable.child(CurrentUser.getUid()).child("id").setValue(id);


                                            Intent intent = new Intent(detect_text.this, QrActivity.class);
                                            intent.putExtra("idCard", id);
                                            progressDialog.dismiss();
                                            startActivity(intent);
                                        }


                                    }
                                }, 1000);

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "your id card not exist in database", Toast.LENGTH_LONG).show();
                            }

                        }
                    }, 1000);


                }

            }
        });
    }

    private void setAndRestPoints() {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        // OneTimeWorkRequest request=new OneTimeWorkRequest.Builder(WorkerRestPoints.class).
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(WorkerRestPoints.class, 43200, TimeUnit.MINUTES)
                .addTag("task")
                .setConstraints(constraints).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("task1", ExistingPeriodicWorkPolicy.KEEP, request);
    }

    private void selectAndDetectImgForeQrCode() {

        btn_create_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 40);

            }
        });
    }

    private void isAlreadyExist(DatabaseReference ref) {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AlreadyExist = dataSnapshot.hasChild(id);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void isGenerated(DatabaseReference ref) {


        ref.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                generated = dataSnapshot.hasChild(CurrentUser.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });


    }

    private void isCorrect(DatabaseReference ref) {
        ref.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Correct = dataSnapshot.hasChild(id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

}
