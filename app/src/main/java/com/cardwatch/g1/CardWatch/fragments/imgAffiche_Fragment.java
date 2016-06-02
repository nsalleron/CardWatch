package com.cardwatch.g1.CardWatch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cardwatch.g1.CardWatch.R;

/**
 * Réalisé par nicolassalleron le 01/05/16.
 */
public class imgAffiche_Fragment extends Fragment {


    private TextView tvClasseCarte;
    private TextView tvNumeroCarte;
    private TextView tvCouleurCarte;
    private TextView tvTime;
    private LinearLayout tuto, image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_image,container,false);
        tvClasseCarte = (TextView) v.findViewById(R.id.tvClasseCarte);
        tvNumeroCarte = (TextView)  v.findViewById(R.id.tvNumeroCarte);
        tvCouleurCarte = (TextView)  v.findViewById(R.id.tvCouleurCarte);
        tvTime = (TextView)  v.findViewById(R.id.tvTime);
        tvClasseCarte.setVisibility(View.INVISIBLE);
        tvNumeroCarte.setVisibility(View.INVISIBLE);
        tvCouleurCarte.setVisibility(View.INVISIBLE);
        tvTime.setVisibility(View.INVISIBLE);

        tuto = (LinearLayout) v.findViewById(R.id.tutoLinearLayout);
        image = (LinearLayout) v.findViewById(R.id.imageLinearLayout);

        image.setVisibility(View.GONE);

        v.findViewById(R.id.tv1).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.tv2).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.tv3).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.tv4).setVisibility(View.INVISIBLE);

        Log.e("Fin init","Fin");

        return v;

    }

    public void setTvClasseText(String tvClasseText) {
        tvClasseCarte.setText(tvClasseText);
    }
    public void setTvNumeroText(String tvClasseText) {
        tvNumeroCarte.setText(tvClasseText);
    }
    public void setTvCouleurText(String tvClasseText) {
        tvCouleurCarte.setText(tvClasseText);
    }
    public void setTvTimeText(String tvClasseText) {
        tvTime.setText(tvClasseText);
    }

    public void setVisibilityImage(int visible){
        image.setVisibility(visible);
    }
    public void setVisibilityTuto(int visibilityTuto){
        tuto.setVisibility(visibilityTuto);
    }
}
