package com.nn.palestadio.android_java;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    EditText editTextEmail, editTextName, editTextId, editTextPassword;
    String name;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);

        progressBar = findViewById(R.id.progressbar);

        findViewById(R.id.textViewSignin).setOnClickListener(this);
        findViewById(R.id.buttonRegister).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        name = editTextName.getText().toString().trim();
        String cedula = editTextName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if(email.isEmpty())
        {
            editTextEmail.setError("Correo electrónico requerido ");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Por favor ingresa un correo válido");
        }

        if(name.isEmpty()){
            editTextName.setError("Por favor ingresa nombre completo");
        }

        if(cedula.isEmpty())
        {
            editTextId.setError("Por favor ingresa la cédula de ciudadania");
        }

        if(password.isEmpty())
        {
            editTextPassword.setError("Contraseña requerida");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            editTextPassword.setError("Tu contraseña debe contener al menos 6 caracteres");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException)
                {
                    editTextEmail.setError("El correo electrónico ya está registrado.");
                    editTextEmail.requestFocus();
                }
                updateUserInfo();
            }
        });


    }

    private void updateUserInfo() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        verificationEmail(user);
                        finish();
                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));                        }
                    else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void verificationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SignUpActivity.this, "Te enviamos un correo de verificación", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewSignin:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.buttonRegister:
                registerUser();
                break;
        }
    }
}
