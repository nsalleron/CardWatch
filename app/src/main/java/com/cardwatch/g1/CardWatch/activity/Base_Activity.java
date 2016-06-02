package com.cardwatch.g1.CardWatch.activity;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import com.cardwatch.g1.CardWatch.R;
import com.cardwatch.g1.CardWatch.adapter.AdapterBase;
import com.cardwatch.g1.CardWatch.fragments.base_images_Fragment;
import com.cardwatch.g1.CardWatch.utils.Ressources;

import java.util.ArrayList;

/**
 * Réalisé par nicolassalleron le 10/05/16.
 * Mise en place des fragments pour la base des images.
 */

@SuppressWarnings("deprecation")
public class Base_Activity extends FragmentActivity {


    private ArrayList<Fragment> fragments;
    base_images_Fragment frag1,frag2,frag3,frag4;
    private base_images_Fragment frag5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_base);

        frag1 = new base_images_Fragment();
        frag1.setRessource(Ressources.imageExterneEtInterne);
        frag1.setCorrection(Ressources.correctionImageExterneEtInterne);
        frag1.setIndex(1);
        frag2 = new base_images_Fragment();
        frag2.setRessource(Ressources.imageInterne);
        frag2.setCorrection(Ressources.correctionImageInterne);
        frag2.setIndex(2);
        frag3 = new base_images_Fragment();
        frag3.setRessource(Ressources.imageSombreInterne);
        frag3.setCorrection(Ressources.correctionImageSombre);
        frag3.setIndex(3);
        frag4 = new base_images_Fragment();
        frag4.setRessource(Ressources.imageInterneFlash);
        frag4.setCorrection(Ressources.correctionImageFlash);
        frag4.setIndex(4);
        frag5 = new base_images_Fragment();
        frag5.setRessource(Ressources.imageExterne);
        frag5.setCorrection(Ressources.correctionExterne);
        frag5.setIndex(5);

        fragments = new ArrayList<>();
        fragments.add(frag1);
        fragments.add(frag2);
        fragments.add(frag3);
        fragments.add(frag4);
        fragments.add(frag5);

        //Mise en place de l'adaptateur
        AdapterBase myPagerAdapter = new AdapterBase(getSupportFragmentManager(),fragments);
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

    }


}