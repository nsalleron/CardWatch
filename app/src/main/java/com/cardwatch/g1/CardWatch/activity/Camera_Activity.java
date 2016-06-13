package com.cardwatch.g1.CardWatch.activity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cardwatch.g1.CardWatch.R;
import com.cardwatch.g1.CardWatch.utils.InitThreadRessource;
import com.cardwatch.g1.CardWatch.utils.Ressources;
import com.cardwatch.g1.CardWatch.utils.imgReconnaissance;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;


/**
 * Réalisé par nicolassalleron le 01/05/16.
 */
public class Camera_Activity extends FragmentActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    // Tag pour la sortie
    private static final String TAG = "Galerie_Activity";
    // Clé pour sauvegarder l'index de la caméra
    private static final String STATE_CAMERA_INDEX = "cameraIndex";
    // Index actif de la caméra
    private int mCameraIndex;
    // Si la caméra est en front, elle est en mirror.
    private boolean mIsCameraFrontFacing;
    // Le nombre de caméras
    private int mNumCameras;
    // La Caméra
    private CameraBridgeViewBase mCameraView;

    // Matrice de ressource
    final Mat[] ressourceMat = new Mat[Ressources.ressource.length];
    private InitThreadRessource initThread;

    //CallBack
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(final int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:

                    pb.setMax(Ressources.ressource.length);
                    //L'ensemble des Matrices sont initialisés
                    initThread = new InitThreadRessource(ressourceMat,getApplicationContext());
                    initThread.start();

                    mCameraView.setMaxFrameSize(320,240);   //Pour alléger les calculs.
                    mCameraView.enableFpsMeter();
                    mCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    private ProgressBar pb;
    private boolean premiereFois = true;
    private ArrayList<String> resultatsGlobaux = new ArrayList<>();
    private Bitmap selectedImage;
    private Mat rgba;
    private Handler handler;
    private ImageView img;
    private ImageView imgPreview;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window window = getWindow();

        setContentView(R.layout.activity_camera);
        mCameraView = (JavaCameraView) findViewById(R.id.JavaCameraView);
        mCameraView.setCameraIndex(mCameraIndex);

        mCameraView.setCvCameraViewListener(this);
        //mCameraView.setMaxFrameSize();

        pb = (ProgressBar) findViewById(R.id.filterProgress);

        //Initialisationn de OpenCv
        if(!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (savedInstanceState != null) {
            mCameraIndex = savedInstanceState.getInt(STATE_CAMERA_INDEX, 0);
        } else {
            mCameraIndex = 0;
        }
        //On récupère le nombre de camera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraIndex, cameraInfo);

            mIsCameraFrontFacing = (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
            mNumCameras = Camera.getNumberOfCameras();
        } else { // pre-Gingerbread
            // Dans le cas ou il n'y avait qu'une seule caméra
            mIsCameraFrontFacing = false;
            mNumCameras = 1;
        }

        handler = new Handler();
        img = ((ImageView) findViewById(R.id.imgTraitementImg));
        imgPreview = ((ImageView) findViewById(R.id.imgPrev));


    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Sauvegarde l'index courant de la camera
        savedInstanceState.putInt(STATE_CAMERA_INDEX, mCameraIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        rgba = inputFrame.rgba();
        if(premiereFois){   //Si faux alors détection en cours
            premiereFois = false;
            selectedImage = Bitmap.createBitmap(rgba.width(), rgba.height(), Bitmap.Config.ARGB_8888);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Utils.matToBitmap(rgba,selectedImage);
                    imgReconnaissance img = new imgReconnaissance(rotateBitmap(selectedImage,90),pb,getApplicationContext(), ressourceMat, initThread, Camera_Activity.this);
                    if(img.findCard()){
                        img.warpCardInImage();
                        img.DetermineCard();
                    }
                    resultatsGlobaux.add(img.getResult());
                    premiereFois = true;
                }
            }).start();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    Mat gray = new Mat(rgba.rows(),rgba.cols(),rgba.type());
                    Imgproc.cvtColor(rgba,gray,Imgproc.COLOR_RGB2GRAY);
                    Imgproc.Canny(gray,gray,50, 150);
                    Utils.matToBitmap(gray,selectedImage);
                    img.setImageBitmap(rotateBitmap(selectedImage,90));
                    Utils.matToBitmap(rgba,selectedImage);
                    imgPreview.setImageBitmap(rotateBitmap(selectedImage,90));
                    ((TextView)findViewById(R.id.fpsFrame)).setText(mCameraView.getFpsMeter());
                }catch (Exception ignored){}




            }
        });
        return null;
    }


    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
