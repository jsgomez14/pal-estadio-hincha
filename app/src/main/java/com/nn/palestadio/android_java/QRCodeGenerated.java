package com.nn.palestadio.android_java;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRCodeGenerated extends AppCompatActivity {
    public final static int QRcodeWidth = 500 ;
    private Bitmap bitmap;
    private ImageView QRviz;
    private Button botonVolver;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generated);

        progressBar = findViewById(R.id.progressbar);

        QRviz = findViewById(R.id.QRViz);
        String bc = getIntent().getStringExtra("EXTRA_BARCODE_SCANNED");
        botonVolver = findViewById(R.id.boton_volver);

        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(QRCodeGenerated.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        try {
            bitmap = TextToImageEncode(bc);

            QRviz.setImageBitmap(bitmap);

            uploadQRToFireBase(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void uploadQRToFireBase(Bitmap bitmap) {

    }

    public Bitmap TextToImageEncode(String Value) throws WriterException
    {
        BitMatrix bitMatrix;
        try
        {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void onBackPressed() {
        // do something on back.
        HomeActivity.stopProgressBar();
        finish();
        return;
    }
}
