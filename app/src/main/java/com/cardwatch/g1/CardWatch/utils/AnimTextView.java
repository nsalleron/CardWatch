package com.cardwatch.g1.CardWatch.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import cn.dreamtobe.percentsmoothhandler.ISmoothTarget;
import cn.dreamtobe.percentsmoothhandler.SmoothHandler;

/**
 * Réalisé par nicolassalleron le 10/05/2016.  <br/>
 * Permet d'animer le texte.  <br/>
 */

public class AnimTextView extends TextView implements ISmoothTarget {

    private SmoothHandler smoothHandler;
    private int progress;
    private int max = 100;

    public AnimTextView(Context context) {
        super(context);
    }
    public AnimTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public AnimTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnimTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //Set
    public void setProgress(final int progress) {
        if (smoothHandler != null) {
            smoothHandler.commitPercent(progress / (float) getMax());
        }

        this.progress = progress;
        setText(String.valueOf(progress));
    }
    public void setMax(int max) {
        this.max = max;
    }
    @Override
    public void setPercent(float percent) {
        setProgress((int) Math.ceil(percent * getMax()));
    }
    @Override
    public void setSmoothPercent(float percent) {
        if (smoothHandler == null) {
            smoothHandler = new SmoothHandler(new WeakReference<ISmoothTarget>(this));
        }

        smoothHandler.loopSmooth(percent);
    }

    //Get
    public int getProgress() {
        return this.progress;
    }
    public int getMax() {
        return max;
    }
    @Override
    public float getPercent() {
        return getProgress() / (float) getMax();
    }




}

