package com.intowow.admobdemo.common;

import android.app.Activity;
import android.util.DisplayMetrics;

import java.math.BigDecimal;


public class LayoutManager {
    static public enum LayoutRatio {
        RATIO_178,    ///< Screen aspect ratio near 1.78
        RATIO_167,    ///< Screen aspect ratio near 1.67
        RATIO_16,     ///< Screen aspect ratio near 1.6
        RATIO_15,     ///< Screen aspect ratio near 1.5
    }

    static private final int DEFAULT_SCREEN_WIDTH = 720;
    static private final int DEFAULT_SCREEN_HEIGHT = 1280;
    static private final float DEFAULT_LOGICAL_DENSITY = 1.0f;
    static private final double DEFAULT_SCALING_RATIO = 1.0;
    static private final int REF_SCREEN_WIDTH = 720;

    static private final LayoutRatio DEFAULT_LAYOUT_RATIO = LayoutRatio.RATIO_178;

    /// Member fields
    private LayoutRatio mRatio = DEFAULT_LAYOUT_RATIO;
    private float mLogicalDensity = DEFAULT_LOGICAL_DENSITY;
    public int mScreenWidth = DEFAULT_SCREEN_WIDTH;
    public int mScreenHeight = DEFAULT_SCREEN_HEIGHT;
    private double mScalingRatio = DEFAULT_SCALING_RATIO;
    private int[] mLayoutMetrics = null;

    // Singleton
    static private LayoutManager mInstance = null;

    static public synchronized LayoutManager getInstance(final Activity activity) {
        if (mInstance == null) {
            mInstance = new LayoutManager();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            mInstance.init(dm.density, dm.widthPixels, dm.heightPixels);
        }

        return mInstance;
    }

    private LayoutManager() {
        mLayoutMetrics = new int[LayoutID.values().length];
    }

    // Layout ID
    static public enum LayoutID {
        // Common metrics
        SCREEN_WIDTH,
        SCREEN_HEIGHT,

        MENU_TOP_MARGIN, //100
        MENU_BUTTOM_MARGIN, //50
    }

    /**
     * Provide the default metrics defined by visual designer
     */
    private void reset() {
        /* Common metrics */
        sm(LayoutID.SCREEN_WIDTH, 720);
        sm(LayoutID.SCREEN_HEIGHT, 1280);
        sm(LayoutID.MENU_TOP_MARGIN, 120);
        sm(LayoutID.MENU_BUTTOM_MARGIN, 50);
    }

    private void updateLayout() {
        sm(LayoutID.SCREEN_WIDTH, mScreenWidth);
        sm(LayoutID.SCREEN_HEIGHT, mScreenHeight);
        as(LayoutID.MENU_TOP_MARGIN);
        as(LayoutID.MENU_BUTTOM_MARGIN);
    }

    public void init(float density, int width, int height) {
        mLogicalDensity = density;
        mScreenWidth = width;
        mScreenHeight = height;
        if (width > height) {
            // UI not rotate the orientation correctly at this moment
            mScreenHeight = width;
            mScreenWidth = height;
        }
        mScalingRatio = mScreenWidth / (double) REF_SCREEN_WIDTH;

        double screenRatio = (double) mScreenHeight / (double) mScreenWidth;

        screenRatio = new BigDecimal(screenRatio)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

        if (screenRatio >= 1.78f) {
            mRatio = LayoutRatio.RATIO_178;
        } else if (screenRatio >= 1.67f) {
            mRatio = LayoutRatio.RATIO_167;
        } else if (screenRatio >= 1.6f) {
            mRatio = LayoutRatio.RATIO_16;
        } else {
            mRatio = LayoutRatio.RATIO_15;
        }

        reset();
        updateLayout();
    }

    public int getMetric(LayoutID id) {
        return gm(id);
    }

    public int applyScaling(int value) {
        return (int) Math.floor(value * mScalingRatio);
    }

    // Abbreviation of getMetric
    private int gm(LayoutID id) {
        return mLayoutMetrics[id.ordinal()];
    }

    // Abbreviation of setMetric
    private void sm(LayoutID id, int value) {
        mLayoutMetrics[id.ordinal()] = value;
    }

    // Abbreviation of applyScaling
    private void as(LayoutID id) {
        int oldValue = mLayoutMetrics[id.ordinal()];
        int newValue = (int) Math.floor(oldValue * mScalingRatio);
        mLayoutMetrics[id.ordinal()] = newValue;
    }

    public LayoutRatio getRatio() {
        return mRatio;
    }

}
