package com.tech.qrcodeexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tech.qrcodeexample.model.GenerateQrObject;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etFullName, etID;
    Button btnGenerateQr, btnScanQr;
    ImageView ivQr;
    //For Generate Qr
    Bitmap bitmap;
    public final static int QRcodeWidth = 500;
    //For Scan Qr
    private IntentIntegrator qrScan;
    //For Gson
    Gson gson = new Gson();
    GenerateQrObject generateQrObject;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing scan object
        qrScan = new IntentIntegrator(this);

        etFullName = (EditText) findViewById(R.id.etFullName);
        etID = (EditText) findViewById(R.id.etID);

        btnGenerateQr = (Button) findViewById(R.id.btnGenerateQr);
        btnScanQr = (Button) findViewById(R.id.btnScanQr);

        ivQr = (ImageView) findViewById(R.id.ivQr);
        btnGenerateQr.setOnClickListener(this);
        btnScanQr.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGenerateQr:
                String strFullName = etFullName.getText().toString();
                String strId = etID.getText().toString();
                if (!TextUtils.isEmpty(strFullName) && !TextUtils.isEmpty(strId)) {
                    try {
                        generateQrObject = new GenerateQrObject(strFullName, strId);
                        String strQr = gson.toJson(generateQrObject, GenerateQrObject.class);
                        //convert text to QR here
                        bitmap = TextToImageEncode(strQr);
                        ivQr.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnScanQr:
                //initiating the qr code scan
                qrScan.initiateScan();
                break;
            default:
                break;
        }
    }

    //For Image Processing here
    public Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
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
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    //For getting scan result here
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //getting Json Data here
        IntentResult qrResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && qrResult != null) {
            try {
                JSONObject jsonObject = new JSONObject(qrResult.getContents());
                generateQrObject = gson.fromJson(jsonObject.toString(), GenerateQrObject.class);
                Toast.makeText(this, "Name is=" + generateQrObject.getFullName() +"\nId is=" + generateQrObject.getId(), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
        }
    }
}

