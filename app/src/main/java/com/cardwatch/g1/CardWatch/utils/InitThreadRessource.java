package com.cardwatch.g1.CardWatch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Réalisé par nicolassalleron le 01/05/16.
 */
public class InitThreadRessource extends Thread implements Runnable{

    Mat[] ressourceMat;
    Context context;

    public InitThreadRessource(Mat[] ressourceMat,Context ctxt) {
        this.ressourceMat = ressourceMat;
        this.context = ctxt;

    }

    @Override
    public void run() {

        for (int i = 0; i < Ressources.ressource.length; i++) {
            Bitmap selectedImage = BitmapFactory.decodeResource(context.getResources(),Ressources.ressource[i]);   // Pour chaque Image
            ressourceMat[i] = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC1);
            Utils.bitmapToMat(selectedImage,  ressourceMat[i]);
            Imgproc.cvtColor( ressourceMat[i], ressourceMat[i],Imgproc.COLOR_BGR2GRAY);
        }
        Log.e("initMat","Initialisation finie");

    }
}
