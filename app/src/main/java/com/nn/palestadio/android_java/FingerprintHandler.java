package com.nn.palestadio.android_java;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Diego Zucchet on 20/03/2018.
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    public FingerprintHandler(Context context){

        this.context = context;
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
        this.update("Ya puedes acceder a la aplicación", true);
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
