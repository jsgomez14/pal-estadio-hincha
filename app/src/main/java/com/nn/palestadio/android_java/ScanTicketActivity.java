package com.nn.palestadio.android_java;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;



public class ScanTicketActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler{
    private ZBarScannerView mScannerView;
    private FirebaseFirestore db;
    private String value;
    private String cedula;

    private final static String KEY_CEDULA = "cedula";
    private final static String PREF_NAME = "prefs";

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        db = FirebaseFirestore.getInstance();
        setContentView(mScannerView);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        getWindow().setBackgroundDrawable(null);
        cedula = sharedPreferences.getString(KEY_CEDULA, "");

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "¡Escanea el código de barras de tu boleta!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {
        // Do something with the result here
        value = result.getContents().trim();
        db.collection("boleteria")
                .whereEqualTo("valor",value)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Lo sentimos, no es un código de barras correcto.", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                onResume();
                            } else {
                                for (DocumentSnapshot document : task.getResult()) {

                                    String[] array = document.getData().toString().split(",");
                                    //Cedula actual
                                    String[] ced = array[7].split("=");
                                    if(!ced[1].equals("-1")) {
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Lo sentimos, esa boleta ya fue registrada", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        Intent HomeActivity = new Intent(ScanTicketActivity.this, HomeActivity.class);
                                        startActivity(HomeActivity);
                                    } else {
                                        DocumentReference boleta = db.collection("boleteria").document(document.getId());
                                        boleta.update("cedula",cedula);
                                        Intent HomeActivity = new Intent(ScanTicketActivity.this, HomeActivity.class);
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Se ha creado la boleta con éxito", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        startActivity(HomeActivity);
                                    }
                                }
                            }

                        } else {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Existen problemas con la base de datos", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            startActivity(new Intent(ScanTicketActivity.this, HomeActivity.class));
                        }
                    }
                });

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }





}
