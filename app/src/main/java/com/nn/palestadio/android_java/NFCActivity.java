package com.nn.palestadio.android_java;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NFCActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        TextView match = findViewById(R.id.textView3);
        String matchString = getIntent().getStringExtra("EXTRA_NFC_SCANNED");
        match.setText(matchString);
    }


}
