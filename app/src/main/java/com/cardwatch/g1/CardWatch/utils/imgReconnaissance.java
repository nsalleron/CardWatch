package com.cardwatch.g1.CardWatch.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cardwatch.g1.CardWatch.R;
import com.cardwatch.g1.CardWatch.activity.Galerie_Activity;
import com.cardwatch.g1.CardWatch.fragments.imgAffiche_Fragment;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

/**
 * Réalisé par nicolassalleron le 01/05/16.
 */
public class imgReconnaissance {


    private final ProgressBar progressBar;
    private final Context context;
    private final Mat[] ressourceMat;
    private final Activity activity;
    private long startTime;
    private final Mat srcOrig;
    private final int scaleFactor;
    private final Mat src;
    private final Handler handler;
    private Mat img1 = null;
    private Mat img2 = null;
    private double[][] resultats = new double[Ressources.ressourceString.length][2];   //L'ensemble des images avec 2 rotation
    private String couleur;
    private int finalIndex;
    private boolean ImageAbs;

    public imgReconnaissance(Bitmap selectedImage, final ProgressBar progressBar, final Context context, Mat[] ressourcesMat , Thread initThread, FragmentActivity activity) {

        this.activity = activity;
        this.progressBar = progressBar;
        this.context = context;
        this.ressourceMat = ressourcesMat;

        handler = new Handler(context.getMainLooper());
        startTime = System.currentTimeMillis();

        //Récupération de l'image
        srcOrig = new Mat(selectedImage.
                getHeight(), selectedImage.
                getWidth(), CvType.CV_8UC4);


        Imgproc.cvtColor(srcOrig, srcOrig,
                Imgproc.COLOR_BGR2RGB);


        Utils.bitmapToMat(selectedImage, srcOrig);
        scaleFactor = calcFacteurEchelle(
                srcOrig.rows(), srcOrig.cols());


        //Fin de récupération dans srcOrig

        //Traitement de l'image plus petite identique à la première = plus rapide.
        src = new Mat();

        Imgproc.resize(srcOrig, src, new
                Size(srcOrig.rows() / scaleFactor,
                srcOrig.cols() / scaleFactor));

        //Lissage de l'image
        Imgproc.GaussianBlur(src, src, new Size(5, 5), 1);

        Mat srcRes = new Mat( src.size(), src.type() );
        Mat srcGray = new Mat();

        progressBar.setMax(src.rows());
        Mat samples = new Mat(src.rows() * src.cols(), 3, CvType.CV_32F);

        //Préparation pour utilisation de kmean
        for( int y = 0; y < src.rows(); y++ ) {

            for( int x = 0; x < src.cols(); x++ ) {
                for( int z = 0; z < 3; z++) {
                    samples.put(x + y*src.cols(), z, src.get(y,x)[z]);

                }

            }

            final int finalY = y;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progressBar.getProgress()+1);
                    if(src.rows()-1 == finalY)
                        progressBar.setProgress(0);
                }
            });

        }

        int clusterCount = 2;
        Mat labels = new Mat();
        int attempts = 5;
        Mat centers = new Mat();

        Core.kmeans(samples, clusterCount, labels, new TermCriteria(TermCriteria.MAX_ITER | TermCriteria.EPS, 10000, 0.0001), attempts, Core.KMEANS_PP_CENTERS, centers);

        double dstCenter0 = calcDistanceBlanc(centers.get(0, 0)[0], centers.get(0, 1)[0], centers.get(0, 2)[0]);
        double dstCenter1 = calcDistanceBlanc(centers.get(1, 0)[0], centers.get(1, 1)[0], centers.get(1, 2)[0]);

        int paperCluster = (dstCenter0 < dstCenter1)?0:1;

        //Pour l'ensemble des clusters
        progressBar.setProgress(0);
        progressBar.setMax(src.rows());
        for( int y = 0; y < src.rows(); y++ ) {
            for( int x = 0; x < src.cols(); x++ )
            {
                int cluster_idx = (int)labels.get(x + y*src.cols(),0)[0];
                if(cluster_idx != paperCluster){
                    srcRes.put(y,x, 0, 0, 0, 255);
                } else {
                    srcRes.put(y,x, 255, 255, 255, 255);
                }
            }

            final int finalY = y;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progressBar.getProgress()+1);
                    if(src.rows()-1 == finalY)
                        progressBar.setProgress(0);
                }
            });
        }


        if(activity instanceof Galerie_Activity)
             affichageMatrice(R.id.imgTraitementImg,srcRes,srcRes.width(),srcRes.height());



        // Fermeture
        Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(3, 3));
        Imgproc.dilate(srcRes, srcRes, kernel);
        Imgproc.erode(srcRes, srcRes, kernel);

        // Ouverture de la fermeture
        Imgproc.erode(srcRes, srcRes, kernel);
        Imgproc.dilate(srcRes, srcRes, kernel);

        //Contours
        Imgproc.Canny(srcRes, srcGray, 200, 200);


        //Reconnaissances des contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(srcGray, contours, new Mat(),  Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f approxCurve = new MatOfPoint2f();

        double maxArea = -1;
        int index = 0;
        //Pour charque contour
        for (int i=0; i<contours.size(); i++)
        {
            MatOfPoint temp_contour = contours.get(i);      //On récupère le contour
            double contourarea = Imgproc.contourArea(temp_contour); //On regarde l'aire du contour

            // On compare ce contour avec le plus grand contour précédemment trouvé.
            if (contourarea > maxArea && contourarea > 3000) {  //valeur 3000 arbitraire, pour supprimer les petits contours

                // On regarde si ce conteneur est un carré
                MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
                int contourSize = (int) temp_contour.total();
                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
                Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize * 0.05, true);
                if (approxCurve_temp.total() == 4) {    // Si le nouveau contour est un carré
                    maxArea = contourarea;      //Mise à jour des valeurs
                    approxCurve = approxCurve_temp;
                    index = i;
                }
            }
        }
        if(activity instanceof Galerie_Activity)
            affichageMatrice(R.id.imgTraitementImg,srcGray,srcGray.width(),srcGray.height());

        double[] temp_double;
        temp_double = approxCurve.get(0, 0);
        if(temp_double != null) {    //Rognage de l'image, mise en place des points de coupe.
            Point p1 = new Point(temp_double[0], temp_double[1]);

            temp_double = approxCurve.get(1, 0);
            Point p2 = new Point(temp_double[0], temp_double[1]);

            temp_double = approxCurve.get(2, 0);
            Point p3 = new Point(temp_double[0], temp_double[1]);

            temp_double = approxCurve.get(3, 0);
            Point p4 = new Point(temp_double[0], temp_double[1]);

            List<Point> source = new ArrayList<Point>();

            source.add(p4);
            source.add(p1);
            source.add(p2);
            source.add(p3);

            //Affichage du contour
            MatOfPoint mat = contours.get(index);
            Rect rect = Imgproc.boundingRect(mat);
            Mat matAffichage = src.clone();
            Core.rectangle(matAffichage, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255, 0, 0, 255), 3);
            affichageMatrice(R.id.imgOriginal,matAffichage,matAffichage.width(),matAffichage.height());


            Mat startM = Converters.vector_Point2f_to_Mat(source);

            //Copie des images
            img1 = src.clone();
            img2 = src.clone();

            //Rogagne
            Mat resultImage1 = rognageImg(img1, startM);
            Mat imgColor1 = resultImage1.clone();
            Imgproc.cvtColor(resultImage1,img1,Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(img1, img1, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

            //Vérification si l'ensemble des matrices sont prêtent
            if(initThread.isAlive()){
                try {
                    initThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            source.clear();
            source.add(p1);
            source.add(p2);
            source.add(p3);
            source.add(p4);

            startM = Converters.vector_Point2f_to_Mat(source);

            //Rogagne
            Mat resultImage2 = rognageImg(img2, startM);
            Imgproc.cvtColor(resultImage2,img2,Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(img2, img2, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

            reconnaissanceImage();

        }else{
            ImageAbs = true;
        }





    }




    private static int calcFacteurEchelle(int rows, int cols){
        int idealRow, idealCol;
        if(rows<cols){
            idealRow = 240;
            idealCol = 320;
        } else {
            idealCol = 240;
            idealRow = 320;
        }
        int val = Math.min(rows / idealRow, cols / idealCol);
        if(val<=0){
            return 1;
        } else {
            return val;
        }
    }

    static double calcDistanceBlanc(double r, double g, double b){
        return Math.sqrt(Math.pow(255 - r, 2) + Math.pow(255 - g, 2) + Math.pow(255 - b, 2));
    }


    public Mat rognageImg(Mat inputMat, Mat startM)
    {

        int resultWidth = 764;
        int resultHeight = 1580;

        Point ocvPOut4 = new Point(0, 0);
        Point ocvPOut1 = new Point(0, resultHeight);
        Point ocvPOut2 = new Point(resultWidth, resultHeight);
        Point ocvPOut3 = new Point(resultWidth, 0);



        if (inputMat.height() > inputMat.width())
        {
            ocvPOut3 = new Point(0, 0);
            ocvPOut4 = new Point(0, resultHeight);
            ocvPOut1 = new Point(resultWidth, resultHeight);
            ocvPOut2 = new Point(resultWidth, 0);
        }

        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);

        List<Point> dest = new ArrayList<Point>();
        dest.add(ocvPOut1);
        dest.add(ocvPOut2);
        dest.add(ocvPOut3);
        dest.add(ocvPOut4);

        Mat endM = Converters.vector_Point2f_to_Mat(dest);




        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);


        Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight), Imgproc.INTER_CUBIC);

        return outputMat;
    }

    private void reconnaissanceImage() {

        startTime = System.currentTimeMillis();

        final Mat Img_Result = img1.clone();

        final ArrayList<Mat> images = new ArrayList<Mat>();
        images.add(img1);
        images.add(img2);

        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setMax(Ressources.ressource.length);
            }
        });

        // TODO: 10/05/16 Reconnaissance du nombre de contours pour une images classique vs Valet/Dame/Roi

        for (int i = 0; i < Ressources.ressource.length; i++) {

            for (int j = 0; j < images.size(); j++) {
                Imgproc.resize(ressourceMat[i],ressourceMat[i],images.get(j).size());

                //Différence entre les deux
                Core.absdiff(ressourceMat[i],images.get(j),Img_Result);
                //On compte les éléments
                Scalar scalar = Core.sumElems(Img_Result);
                resultats[i][j] = scalar.val[0]; //La valeur total des pixels de gris.

            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progressBar.getProgress()+1);
                }
            });

        }

        //Récupération de la plus petite valeur
        double valeur = Double.MAX_VALUE;
        int index = 0;
        boolean deuxieme = false;
        for (int i = 0; i < Ressources.ressource.length; i++) {
            for(int j = 0; j < images.size();j++){
                if(resultats[i][j] < valeur) {
                    valeur = resultats[i][j];
                    index = i;
                    if(j == 1)
                        deuxieme = true;
                    else
                        deuxieme = false;
                }
            }
        }

        finalIndex = index;

        final long endTime = System.currentTimeMillis();

        final boolean finalDeuxieme = deuxieme;

        //On détermine la couleurs suivant la classe
        couleur ="";
        if(Ressources.ressourceString[finalIndex].split(";")[0].equals("trefle"))
            couleur = "noir";
        if(Ressources.ressourceString[finalIndex].split(";")[0].equals("pique"))
            couleur = "noir";
        if(Ressources.ressourceString[finalIndex].split(";")[0].equals("carreau"))
            couleur = "rouge";
        if(Ressources.ressourceString[finalIndex].split(";")[0].equals("coeur"))
            couleur = "rouge";

        handler.post(new Runnable() {
            Bitmap imgBitmap, imgBitmapResult;

            @Override
            public void run() {
                //On replace les tv visibles
                activity.findViewById(R.id.tv3).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.tv4).setVisibility(View.VISIBLE);
                imgAffiche_Fragment frag;

                progressBar.setProgress(0);
                //La carte extraite
                int i = 0;
                if(finalDeuxieme)
                    i=1;
                Mat Img_Result = images.get(i).clone();
                imgBitmap = imgBitmapResult = Bitmap.createBitmap(Img_Result.width(), Img_Result.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(Img_Result, imgBitmap);
                Core.absdiff(ressourceMat[finalIndex],images.get(i),Img_Result);
                Utils.matToBitmap(Img_Result, imgBitmapResult);

                if(activity instanceof Galerie_Activity){


                    frag = ((imgAffiche_Fragment) ((Galerie_Activity) activity).fragments.get(0));
                    frag.setTvClasseText("La classe de la carte est : " + Ressources.ressourceString[finalIndex].split(";")[0]);
                    frag.setTvNumeroText("Le numéro de la carte est : " + Ressources.ressourceString[finalIndex].split(";")[1]);
                    frag.setTvTimeText("Temps de l'opération : "+(endTime-startTime)+" ms");
                    frag.setTvCouleurText("La couleur est "+ couleur);
                    //La carte extraite
                    if(finalDeuxieme){
                        imgBitmap = Bitmap.createBitmap(img2.width(), img2.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(img2, imgBitmap);
                    }else{
                        imgBitmap = Bitmap.createBitmap(img1.width(), img1.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(img1, imgBitmap);

                    }
                    ((ImageView)  activity.findViewById(R.id.imgExtract)).setImageBitmap(imgBitmap);
                    //Le matching
                    ((ImageView)  activity.findViewById(R.id.imgFinal)).setImageBitmap(imgBitmapResult);

                }else{  //Dans le cas de la caméra
                    ((TextView) activity.findViewById(R.id.tvClasseCarte)).setText("La classe de la carte est : " + Ressources.ressourceString[finalIndex].split(";")[0]);
                    ((TextView) activity.findViewById(R.id.tvNumeroCarte)).setText("Le numéro de la carte est : " + Ressources.ressourceString[finalIndex].split(";")[1]);
                    ((TextView) activity.findViewById(R.id.tvTime)).setText("Temps de l'opération : "+(endTime-startTime)+" ms");
                    ((TextView) activity.findViewById(R.id.tvCouleurCarte)).setText("La couleur est "+ couleur);
                    //La carte extraite
                    ((ImageView)  activity.findViewById(R.id.imgExtract)).setImageBitmap(imgBitmap);
                    //Le matching
                    ((ImageView)  activity.findViewById(R.id.imgFinal)).setImageBitmap(imgBitmapResult);
                }

                if(!ImageAbs){
                    activity.findViewById(R.id.tvClasseCarte).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.tvNumeroCarte).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.tvCouleurCarte).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.tvTime).setVisibility(View.VISIBLE);
                }else{
                    activity.findViewById(R.id.tvClasseCarte).setVisibility(View.INVISIBLE);
                    activity.findViewById(R.id.tvNumeroCarte).setVisibility(View.INVISIBLE);
                    activity.findViewById(R.id.tvCouleurCarte).setVisibility(View.INVISIBLE);
                    activity.findViewById(R.id.tvTime).setVisibility(View.INVISIBLE);

                }

            }
        });

        System.gc();

    }

    public String getResult() {
        String result;
        if(ImageAbs)
            result = "false;";
        else
            result = Ressources.ressourceString[finalIndex].split(";")[0] + ";" + Ressources.ressourceString[finalIndex].split(";")[1];// + ";" + couleur;

        return result;
    }

    private void affichageMatrice(final int id, final Mat img, final int width, final int height) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap tempImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(img, tempImage);
                ((ImageView)  activity.findViewById(id)).setImageBitmap(tempImage);
            }
        });
    }

    private ArrayList<Rect> detectLetters(final Mat img)
    {
        ArrayList<Rect> boundRect = new ArrayList<Rect>();
        progressBar.setProgress(0);
        progressBar.setMax(img.rows());

        Mat srcGray = new Mat( img.rows(), img.cols() , CvType.CV_8UC4);

        //On regarde en haut à gauche
        Point p1 = new Point(0, 0);
        Point p4 = new Point(srcGray.width()/6, srcGray.height()/9);
        Rect ROI = new Rect(p1,p4);

        final Mat resultImage1 = new Mat(img.clone(),ROI);


        //Test des couleurs
        affichageMatrice(R.id.imgOriginal,resultImage1,resultImage1.width(),resultImage1.height());


        return null;
    }
}
