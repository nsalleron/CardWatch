package com.cardwatch.g1.CardWatch.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.cardwatch.g1.CardWatch.R;
import com.cardwatch.g1.CardWatch.activity.Details_Activity;
import com.cardwatch.g1.CardWatch.activity.Galerie_Activity;
import com.cardwatch.g1.CardWatch.adapter.GridViewAdapter;
import com.cardwatch.g1.CardWatch.utils.ImageItem;

import java.util.ArrayList;

/**
 * Réalisé par nicolassalleron le 10/05/16.
 */

@SuppressWarnings("deprecation")
public class base_images_Fragment extends Fragment {



    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private Button btn1;
    private int index;
    private ProgressBar progressBar;
    private int[] ressource;
    private String correction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_base,container,false);
        progressBar = (ProgressBar) v.findViewById(R.id.pb);
        gridView = (GridView) v.findViewById(R.id.gridView);
        btn1 = (Button) v.findViewById(R.id.btnR1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, getData(ressource));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gridView.setAdapter(gridAdapter);
                    }
                });

            }
        }).start();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(getActivity(), Details_Activity.class);
                intent.putExtra("title", index+";"+item.getTitle());


                //Start details activity
                startActivity(intent);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Galerie_Activity.class);
                intent.putExtra("images", ressource);
                intent.putExtra("correction",correction);
                //Start details activity
                startActivity(intent);
            }
        });

        return v;
    }


    // Prepare some dummy data for gridview
    private ArrayList<ImageItem> getData(int[] ressource) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        progressBar.setMax(ressource.length);
        for (int i = 0; i < ressource.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), ressource[i]);
            imageItems.add(new ImageItem(getResizedBitmap(bitmap,100), "Image#" + i));
            final int finalI = i;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(finalI);
                }
            });
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });

        return imageItems;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void setRessource(int[] ressource) {
        this.ressource = ressource;
    }

    public void setCorrection(String correction) {
        this.correction = correction;
    }

    public void setIndex(int i) {
        this.index = i;
    }
}