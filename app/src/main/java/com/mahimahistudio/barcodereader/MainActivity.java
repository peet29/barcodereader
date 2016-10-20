package com.mahimahistudio.barcodereader;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            beepManager.playBeepSoundAndVibrate();
            DialogShow();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private void DialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(lastText)
                .setTitle(R.string.dailog_title);
        builder.setCancelable(false);


        String positiveText = null;
        switch (lastText.substring(0, 4).toUpperCase()) {
            case "SMS:":
            case "SMST":
                positiveText = "send sms";
                builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lastText.toLowerCase()));
                        startActivity(intent);

                        lastText = "";
                    }
                });
                break;
            case "TEL:":
                positiveText = "call";
                builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lastText.toLowerCase()));
                        startActivity(intent);

                        lastText = "";
                    }
                });

                break;
            case "MAIL":
                positiveText = "send email";
                builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lastText.toLowerCase()));
                        startActivity(intent);

                        lastText = "";
                    }
                });
                break;
            case "HTTP":
                positiveText = "open link";
                builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lastText.toLowerCase()));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        lastText = "";
                    }
                });
                break;
            case "BEGI":
            case "GEO:":
            case "MECA":
            case "WIFI":
                positiveText = "open";
                break;

        }
        builder.setNegativeButton(R.string.dailog_close,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                lastText = "";
            }
        });
        builder.setNeutralButton("COPY",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label",lastText);
                clipboard.setPrimaryClip(clip);
                lastText = "";
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        beepManager = new BeepManager(this);


    }

    private void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        } else {
            barcodeView.resume();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
