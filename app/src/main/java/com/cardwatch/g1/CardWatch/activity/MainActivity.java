package com.cardwatch.g1.CardWatch.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cardwatch.g1.CardWatch.R;

import java.util.ArrayList;

/**
 * Réalisé par nicolassalleron le 01/05/16.
 */
public class MainActivity extends Activity {
    private Button btnCamera, btnGalerie, btnBase;
    private AlertDialog.Builder Dialog;
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private boolean show;
    private ArrayList<String> permRestante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnGalerie = (Button) findViewById(R.id.btnGalerie);
        btnBase = (Button) findViewById(R.id.btnBase);

        btnGalerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Galerie_Activity.class);
                startActivity(intent);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Camera_Activity.class);
                startActivity(intent);
            }
        });

        btnBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Base_Activity.class);
                startActivity(intent);
            }
        });

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        //Recherche des différentes permissions
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           final String permissions[], int[] grantResults) {

        permRestante = new ArrayList<String>();

        for (int i = 0; i < permissions.length; i++){
            if(grantResults[i]== PackageManager.PERMISSION_DENIED)
                permRestante.add(permissions[i]);
        }

        //Construction du tableau des permissions refusées restante
        String[] askPermission = new String[permRestante.size()];
        for (int i = 0;i<permRestante.size();i++){
            askPermission[i]=permRestante.get(i);
        }

        if(askPermission.length>0){
            showDialogOK(askPermission);
        }

    }

    private void showDialogOK(final String[] i) {

        show = true;

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(MainActivity.this, i, 1);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getApplicationContext(), "Il est nécessaire d'accepter les permissions dans les préférences ou de relancer l'application.", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
            }
        };

        Dialog = new AlertDialog.Builder(this)
                .setMessage("Il est nécessaire d'accepter les permissions pour le bon fonctionnement de l'application.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
        ;

        Dialog.create();
        Dialog.show();



    }

}
