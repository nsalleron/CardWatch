package com.cardwatch.g1.CardWatch.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.cardwatch.g1.CardWatch.R;
import com.cardwatch.g1.CardWatch.utils.AnimTextView;
import com.liulishuo.magicprogresswidget.MagicProgressCircle;

/**
 * Réalisé par nicolassalleron le 01/05/16.
 */
public class imgResult_Fragment extends Fragment {

    private MagicProgressCircle rmpc, pmpc, empc, hmpc,cmpc,nmpc;
    private AnimTextView ratv, patv, eatv,hatv,catv,natv;
    private AnimatorSet set;
    private TextView tv1, tv2, tv3;
    private Handler handler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result,container,false);

        rmpc = (MagicProgressCircle) v.findViewById(R.id.rmpc);
        pmpc = (MagicProgressCircle) v.findViewById(R.id.pmpc);
        empc = (MagicProgressCircle) v.findViewById(R.id.empc);
        hmpc = (MagicProgressCircle) v.findViewById(R.id.hmpc);
        cmpc = (MagicProgressCircle) v.findViewById(R.id.cmpc);
        nmpc = (MagicProgressCircle) v.findViewById(R.id.nmpc);

        ratv = (AnimTextView) v.findViewById(R.id.ratv);
        patv = (AnimTextView) v.findViewById(R.id.patv);
        eatv = (AnimTextView) v.findViewById(R.id.eatv);
        hatv = (AnimTextView) v.findViewById(R.id.hatv);
        catv = (AnimTextView) v.findViewById(R.id.catv);
        natv = (AnimTextView) v.findViewById(R.id.natv);

        set = new AnimatorSet();

        handler = new Handler();

        return v;
    }

    public void setRappel(double rappel) {
        defineMagicCircle(rmpc,ratv, (float) (rappel/101f),(int)rappel);    //101 pour corriger un probleme dans le cas de 100%

    }

    public void setPrecision(double precision) {
        defineMagicCircle(pmpc,patv, (float) (precision/101f),(int)precision);  //101 pour corriger un probleme dans le cas de 100%

    }

    public void setExactitude(double exactitude) {
        defineMagicCircle(empc,eatv, (float) (exactitude/101f),(int)exactitude);    //101 pour corriger un probleme dans le cas de 100%

    }
    /**
     * Permet de définir les propriétés du cercle ainsi que de la valeur numérique du cercle
     * @param mpc l'élément MagicProgressCircle que l'on doit définir
     * @param tv  l'élément AnimTextView (texte qui change dynamiquement) que l'on doit définir
     */
    private void defineMagicCircle(final MagicProgressCircle mpc, final AnimTextView tv, final float cercle, final int pourcentage) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                tv.setMax(100);
                mpc.setStartColor(14430770);
                mpc.setEndColor(14430770);
                set.play(ObjectAnimator.ofFloat(mpc, "percent", 0, cercle));
                set.play(ObjectAnimator.ofInt(tv, "progress", 0, pourcentage));
                set.setDuration(0);
                set.setInterpolator(new AccelerateInterpolator());
                set.start();

            }
        });


    }

    public void setMoyenneHarmonique(double moyenneHarmonique) {
        defineMagicCircle(hmpc,hatv, (float) (moyenneHarmonique/101f),(int)moyenneHarmonique);    //101 pour corriger un probleme dans le cas de 100%
    }

    public void setClasse(double classe) {
        defineMagicCircle(cmpc,catv, (float) (classe/101f),(int)classe);
    }

    public void setNombre(double nombre) {
        defineMagicCircle(nmpc,natv, (float) (nombre/101f),(int)nombre);
    }
}
