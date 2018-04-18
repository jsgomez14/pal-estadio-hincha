package com.nn.palestadio.android_java;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.textViewSignin).setOnClickListener(this);
        findViewById(R.id.buttonRegister).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewSignin:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.buttonRegister:
                finish();
                startActivity(new Intent(this, HomeActivity.class ));
                break;
        }
    }
}
