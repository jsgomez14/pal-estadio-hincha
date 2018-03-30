package com.nn.palestadio.android_java;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScanTicketActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler{
    private ZBarScannerView mScannerView;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
        Toast.makeText(this, "¡Escanea el código de barras de tu boleta!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {
        // Do something with the result here
        Toast.makeText(this, "¡Escaneado!: "+ result.getContents(), Toast.LENGTH_SHORT).show();

        Intent QRCode = new Intent(ScanTicketActivity.this, QRCodeGenerated.class);
        QRCode.putExtra("EXTRA_BARCODE_SCANNED", result.getContents());
        startActivity(QRCode);

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }
}
