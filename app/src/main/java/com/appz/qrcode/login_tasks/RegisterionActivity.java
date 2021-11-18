package com.appz.qrcode.login_tasks;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appz.qrcode.R;
import com.appz.qrcode.helperUi.AllFinal;
import com.appz.qrcode.login_tasks.models.LoginModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterionActivity extends AppCompatActivity {


    // ui
    private EditText ed_name, ed_email, ed_phone, ed_password;
    private Spinner sp_gender;
    private ProgressDialog progressDialog;


    // var
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference(AllFinal.ROOT_LOGIN_FIRE);
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerion);


        buildView();
    }

    private void showProgress(boolean s) {
        if (s == true) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private void buildView() {
        progressDialog = new ProgressDialog(RegisterionActivity.this);
        progressDialog.setMessage("Wait Please");
        ed_name = findViewById(R.id.ed_name_s);
        ed_email = findViewById(R.id.ed_email_s);
        ed_phone = findViewById(R.id.ed_phone);
        ed_password = findViewById(R.id.ed_pass_s);
        sp_gender = findViewById(R.id.gender_spinner);


        mAuth = FirebaseAuth.getInstance();


    }

    public void signup(View view) {


        if (checkEditedText()) {
            showProgress(true);
            if (gender.equals(AllFinal.CLIENT)) {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    setDataOfUserToFireBase(mAuth.getCurrentUser().getUid());


                                } else {

                                    Log.d("ezzat", task.getException().getMessage());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    showProgress(false);

                                }


                            }
                        });


            } else if (gender.equals(AllFinal.SELLER)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    setDataOfUserToFireBaseSeller(mAuth.getCurrentUser().getUid());


                                } else {


                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }


                            }
                        });


            }
        }


    }

    private void setDataOfUserToFireBaseSeller(String uid) {
        LoginModel clientModel = new LoginModel(
                uid, name, email, gender, phone
        );
        reference.child(AllFinal.SELLER).child(uid)
                .setValue(clientModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegisterionActivity.this, "success ", Toast.LENGTH_SHORT).show();
                showProgress(false);
                onBackPressed();

            }
        });
    }

    private void setDataOfUserToFireBase(String id) {

        LoginModel clientModel = new LoginModel(
                id, name, email, gender, phone
        );
        reference.child(AllFinal.CLIENT).child(id)
                .setValue(clientModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegisterionActivity.this, "success ", Toast.LENGTH_SHORT).show();
                showProgress(false);
                onBackPressed();

            }
        });
    }

    private Boolean checkEditedText() {
        boolean s1 = false;
        boolean s2 = false;
        boolean s3 = false;
        boolean s4 = false;

        email = ed_email.getText().toString().trim();
        password = ed_password.getText().toString().trim();
        phone = ed_phone.getText().toString();
        gender = sp_gender.getSelectedItem().toString();
        name = ed_name.getText().toString();

        if (name.length() != 0) {
            s3 = true;

        } else {
            s3 = false;
            ed_name.setError("write name first !");
            return false;
        }
        if (Pattern.matches(Patterns.EMAIL_ADDRESS.toString(), email)) {
            s1 = true;

        } else {
            s1 = false;
            ed_email.setError("write valid email !");
            return false;
        }
        if (password.length() < 6) {
            ed_password.setError("write valid password minmum 6 letters");
            s2 = false;
        } else {
            s2 = true;
        }


        if (phone.length() < 11) {
            ed_phone.setError("write valid phone minmum 11 number");
            s4 = false;
        } else {
            s4 = true;
        }

        return s1 && s2 && s3 && s4;


    }

}
