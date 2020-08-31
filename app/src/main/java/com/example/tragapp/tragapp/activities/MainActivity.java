package com.example.tragapp.tragapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tragapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;
    private TextView txtReset;
    private EditText emailSU, passwordSU;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        emailSU=findViewById(R.id.emailLogin);
        passwordSU=findViewById(R.id.passwordLogin);
        btnLogin=findViewById(R.id.btnLogin);
        txtReset=findViewById(R.id.reset_text);
        txtReset.setText(Html.fromHtml("<u>Dimenticato la password?</u>"));
        btnRegister=findViewById(R.id.btnRegister);

        firebaseAuth =FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    firebaseAuth.signInWithEmailAndPassword(emailSU.getText().toString(), passwordSU.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("firstrun",false);
                                        editor.apply();
                                        finish(); //cosi seclicca il pulsante indietro non torna più sulla schermata iniziale
                                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }catch(Exception e) {
                    Toast.makeText(MainActivity.this, getString(R.string.needinfo), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                finish();
                startActivity(intent);
            }
        });

        txtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = emailSU.getText().toString().trim();
                if (email.isEmpty()) {
                    emailSU.setError("Email required");
                    emailSU.requestFocus();
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), getString(R.string.resetmessage),
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.emailnotexist), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);

        if(preferences.getBoolean("firstrun", true)) {
        }
        else{
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }
}