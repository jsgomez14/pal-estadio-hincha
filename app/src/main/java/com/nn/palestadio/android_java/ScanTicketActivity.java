package com.nn.palestadio.android_java;

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
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {
        // Do something with the result here
        //Log.v("Result", result.getContents()); // Prints scan results
        //Log.v("Result Format", result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
        if(result.getBarcodeFormat().equals())
        Toast.makeText(this, result.getContents() + "Format: "+result.getBarcodeFormat(), Toast.LENGTH_SHORT).show();
        
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}
