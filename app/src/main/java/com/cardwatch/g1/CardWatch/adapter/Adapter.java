package com.cardwatch.g1.CardWatch.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Réalisé par nicolassalleron le 01/05/16.
 */
public class Adapter extends FragmentPagerAdapter {


    private final List fragments;

    /**
     * On fournit la list des fragment à afficher
     * @param fragmentManager le fragment manager
     * @param fragments la liste des fragments
     */
    public Adapter(FragmentManager fragmentManager, List fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) { //La position des fragments dans la liste.
        return (Fragment) this.fragments.get(position);
    }

    @Override
    public int getCount() {     //La taille total des fragments
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {    //Titre de chaque fragment
        switch (position) {
            case 0:
                return "Les images";
            case 1:
                return "Les statistiques";
        }
        return null;
    }
}