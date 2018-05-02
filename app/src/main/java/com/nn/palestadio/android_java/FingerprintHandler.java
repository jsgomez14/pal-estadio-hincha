package com.nn.palestadio.android_java;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.util.Patterns;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Diego Zucchet on 20/03/2018.
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private FirebaseAuth mAuth;

    private final static String PREF_NAME= "prefs";
    private final static String KEY_EMAIL= "email";
    private static final String KEY_PASS = "password";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    public FingerprintHandler(Context context){
        mAuth = FirebaseAuth.getInstance();
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal,0,this,null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("Hubo un error de autenticación" + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Autenticación falló", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        String password = sharedPreferences.getString(KEY_PASS, "");

        userLogin(email, password);
    }

    private void userLogin(String email, String password) {
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.isEmpty() || password.length() < 6)
        {
            Toast.makeText(context, "Error autenticación con huella, prueba con tu correo y contraseña", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        context.startActivity(new Intent(context, HomeActivity.class));
                    }else
                    {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void update(String s, boolean b) {

       // TextView secondaryLabel = (TextView) ((Activity)context).findViewById(R.id.secondaryLabel);
      //  ImageView imageView = (ImageView) ((Activity)context).findViewById(R.id.fingerprintImage);

       // secondaryLabel.setText(s);

        if(b == false){
            //secondaryLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {

            //secondaryLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
           // imageView.setImageResource(R.mipmap.action_done);

            ((Activity) context).finish();
            context.startActivity(new Intent(context, HomeActivity.class));


        }
    }
}
