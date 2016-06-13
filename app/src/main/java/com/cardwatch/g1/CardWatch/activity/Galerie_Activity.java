package com.cardwatch.g1.CardWatch.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cardwatch.g1.CardWatch.R;
import com.cardwatch.g1.CardWatch.adapter.Adapter;
import com.cardwatch.g1.CardWatch.fragments.imgAffiche_Fragment;
import com.cardwatch.g1.CardWatch.fragments.imgResult_Fragment;
import com.cardwatch.g1.CardWatch.utils.Comparaison;
import com.cardwatch.g1.CardWatch.utils.InitThreadRessource;
import com.cardwatch.g1.CardWatch.utils.Ressources;
import com.cardwatch.g1.CardWatch.utils.imgReconnaissance;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Réalisé par nicolassalleron le 01/05/16.
 */
public class Galerie_Activity extends FragmentActivity {

    private final int SELECT_PHOTO_1 = 1;
    final Mat[] ressourceMat = new Mat[Ressources.ressource.length];
    Thread initThread;


    private static final int ACTIVITY_CHOOSE_FILE = 3;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    progressBar.setMax(Ressources.ressource.length);
                    //L'ensemble des Matrices sont initialisés
                    initThread = new InitThreadRessource(ressourceMat,getApplicationContext());
                    initThread.start();

                    if(getIntent().getExtras() != null){
                        final int[] images = (int[]) getIntent().getExtras().get("images");
                        String correction = getIntent().getStringExtra("correction");
                        if(images != null && correction != null){
                            imgAffiche_fragment.setVisibilityImage(View.VISIBLE);
                            imgAffiche_fragment.setVisibilityTuto(View.GONE);
                            comparaison = new Comparaison(correction,false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < images.length; i++) {

                                        selectedImage = BitmapFactory.decodeResource(getResources(), images[i]);
                                        initInterface();
                                        imgReconnaissance img = new imgReconnaissance(selectedImage, progressBar, getApplicationContext(), ressourceMat, initThread, Galerie_Activity.this);
                                        if(img.findCard()){
                                            img.warpCardInImage();
                                            img.DetermineCard();
                                        }
                                        Log.e("Result image : ", img.getResult());
                                        resultatsGlobaux.add(img.getResult());
                                        statistique();

                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    }


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Galerie_Activity.this, "L'ensemble des images ont été traitées !", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }).start();
                        }
                    }

                    System.loadLibrary("nonfree");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    private Bitmap selectedImage;
    private ProgressBar progressBar;
    private String extension;
    public ArrayList<Object> fragments;
    private ArrayList<String> resultatsGlobaux = new ArrayList<>();
    private Comparaison comparaison;
    private imgAffiche_Fragment imgAffiche_fragment;
    private imgResult_Fragment imgResult_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imgAffiche_fragment = new imgAffiche_Fragment();
        imgResult_fragment = new imgResult_Fragment();
        fragments = new ArrayList<>();
        fragments.add(imgAffiche_fragment);
        fragments.add(imgResult_fragment);
        //Mise en place de l'adaptateur
        Adapter myPagerAdapter = new Adapter(getSupportFragmentManager(),fragments);
        ViewPager myPager = (ViewPager) findViewById(R.id.viewpager_img);
        myPager.setAdapter(myPagerAdapter);
        //Mise en place des titres
        PagerTitleStrip pts = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pts.setTextColor(getColor(R.color.MockupRedText));
        }else
            pts.setTextColor(getResources().getColor(R.color.MockupRedText));
        pts.setTextSpacing(200);


        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10,  Galerie_Activity.this,
                mOpenCVCallBack);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.charger_image1) {

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO_1);
            return true;
        }
        if( id == R.id.charger_groupe){
            boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            if (isKitKat) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,ACTIVITY_CHOOSE_FILE);

            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent,ACTIVITY_CHOOSE_FILE);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, final Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        if (resultCode ==  RESULT_OK && requestCode == SELECT_PHOTO_1) {
            imgAffiche_fragment.setVisibilityImage(View.VISIBLE);
            imgAffiche_fragment.setVisibilityTuto(View.GONE);
            try {
                Uri imageUri = imageReturnedIntent.getData();
                InputStream imageStream =  getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                initInterface();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        imgReconnaissance img = new imgReconnaissance(selectedImage,progressBar,getApplicationContext(), ressourceMat, initThread, Galerie_Activity.this);
                        resultatsGlobaux.add(img.getResult());
                    }
                }).start();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(resultCode ==  RESULT_OK && requestCode == ACTIVITY_CHOOSE_FILE) {
            imgAffiche_fragment.setVisibilityImage(View.VISIBLE);
            imgAffiche_fragment.setVisibilityTuto(View.GONE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri uri = imageReturnedIntent.getData();
                    File FilePath = new File(new File(uri.getPath()).getParent());
                    comparaison = new Comparaison(new File(uri.getPath()).getAbsolutePath(),true);
                    resultatsGlobaux = new ArrayList<String>();
                    Log.e("Directory ", FilePath.getAbsolutePath());
                    if(FilePath.isDirectory()){
                        File[] listFile = FilePath.listFiles();

                        for(int i = 0; i < FilePath.listFiles().length;i++){
                            extension = getFileExtension(listFile[i]);
                            if(extension.equals("jpg") || extension.equals("png")){
                                Log.e("Fichier : "+i, listFile[i].getAbsolutePath());
                                selectedImage = BitmapFactory.decodeFile(listFile[i].getAbsolutePath());
                                initInterface();
                                imgReconnaissance img = new imgReconnaissance(selectedImage,progressBar,getApplicationContext(), ressourceMat, initThread, Galerie_Activity.this);
                                Log.e("Result image : ", img.getResult());
                                resultatsGlobaux.add(img.getResult());
                                statistique();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Galerie_Activity.this, "L'ensemble des images ont été traitées !", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }).start();

        }

    }

    private void statistique() {
        comparaison.compute(resultatsGlobaux);
        imgResult_Fragment fragment = (imgResult_Fragment) fragments.get(1);
        fragment.setRappel(comparaison.rappel);
        fragment.setPrecision(comparaison.précision);
        fragment.setExactitude(comparaison.exactitude);
        fragment.setMoyenneHarmonique(comparaison.harmonique);
        fragment.setClasse(comparaison.reconnaissanceClasse);
        fragment.setNombre(comparaison.reconnaissanceNombre);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void initInterface(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Mise en place des textView
                findViewById(R.id.tvClasseCarte).setVisibility(View.INVISIBLE);
                findViewById(R.id.tvNumeroCarte).setVisibility(View.INVISIBLE);
                findViewById(R.id.tvCouleurCarte).setVisibility(View.INVISIBLE);
                findViewById(R.id.tvTime).setVisibility(View.INVISIBLE);
                findViewById(R.id.tv3).setVisibility(View.INVISIBLE);
                findViewById(R.id.tv4).setVisibility(View.INVISIBLE);
                findViewById(R.id.tv1).setVisibility(View.VISIBLE);
                findViewById(R.id.tv2).setVisibility(View.VISIBLE);


                //réinitialisation des imgs précédentes
                ((ImageView) findViewById(R.id.imgOriginal)).setImageBitmap(null);
                ((ImageView) findViewById(R.id.imgFinal)).setImageBitmap(null);
                ((ImageView) findViewById(R.id.imgExtract)).setImageBitmap(null);
                ((ImageView) findViewById(R.id.imgTraitementImg)).setImageBitmap(null);

                //Affichage de l'image original
                ((ImageView)  findViewById(R.id.imgOriginal)).setImageBitmap(selectedImage);
                ((ImageView)  findViewById(R.id.imgTraitementImg)).setImageBitmap(selectedImage);
            }
        });

    }

}
