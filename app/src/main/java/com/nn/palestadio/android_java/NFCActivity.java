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
        TextView date = findViewById(R.id.textView5);
        String infoString = getIntent().getStringExtra("EXTRA_NFC_SCANNED");
        String[] arguments = infoString.split(";");


        match.setText(arguments[0]);
        date.setText(arguments[1]);
    }


}
