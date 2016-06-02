package com.cardwatch.g1.CardWatch.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Réalisé par nicolassalleron le 01/05/16.
 */
public class AdapterBase extends FragmentPagerAdapter {


    private final List fragments;

    /**
     * On fournit la list des fragment à afficher
     * @param fragmentManager le fragment manager
     * @param fragments la liste des fragments
     */
    public AdapterBase(FragmentManager fragmentManager, List fragments) {
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
                return "Images interne/Externe";
            case 1:
                return "Images interne normal";
            case 2:
                return "Images interne sombre";
            case 3:
                return "Images interne avec flash";
            case 4:
                return "Images Externe mixte";
        }
        return null;
    }
}