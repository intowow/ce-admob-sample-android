package com.intowow.admobdemo.customevent;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.intowow.sdk.Ad;
import com.intowow.sdk.AdError;
import com.intowow.sdk.AdListener;
import com.intowow.sdk.DisplayAd;

import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

/**
 * Custom event card for Intowow SDK
 * Minimum support Intowow SDK 3.30.1
 */
public class CECustomEventCard implements CustomEventBanner, AdListener {

    protected static final String TAG = CECustomEventCard.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT_MILLIS = 10000;

    private CustomEventBannerListener mBannerListener = null;

    private DisplayAd mDisplayAd = null;
    private Context mContext;
    private AdSize mAdSize;

    @Override
    public void requestBannerAd(Context context,
                                CustomEventBannerListener customEventBannerListener,
                                String serverParameter,
                                AdSize adSize,
                                MediationAdRequest mediationAdRequest,
                                Bundle customEventExtras) {

        // Error checking
        if (customEventBannerListener == null) {
            Log.w(TAG, "Failed to request card ad, listener is null");
            return;
        }

        if(context == null) {
            Log.w(TAG, "Failed to request card ad, context is null");
            customEventBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        final String placementId = serverParameter;
        if (TextUtils.isEmpty(placementId)) {
            Log.w(TAG, "Failed to request card ad, placement id is null");
            customEventBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        Log.d(TAG, "Placement is: " + placementId);

        if(mDisplayAd != null) {
            mDisplayAd.destroy();
        }

        mContext = context;
        mAdSize = adSize;
        mBannerListener = customEventBannerListener;

        mDisplayAd = new DisplayAd(context, placementId, null);
        mDisplayAd.setAdListener(this);
        mDisplayAd.loadAd(DEFAULT_TIMEOUT_MILLIS);
    }

    @Override
    public void onDestroy() {
        if (mDisplayAd != null) {
            mDisplayAd.destroy();
            mDisplayAd = null;
        }

        mContext = null;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Log.d(TAG, "Failed to load card ad, Error: " + (adError == null ? String.valueOf(AdError.CODE_NO_FILL_ERROR) : String.valueOf(adError.getErrorCode())));
        if (adError == null) {
            mBannerListener.onAdFailedToLoad(ERROR_CODE_INTERNAL_ERROR);
            return;
        }

        switch (adError.getErrorCode()) {
            case AdError.CODE_NO_FILL_ERROR:
            case AdError.CODE_REQUEST_TIMEOUT_ERROR:
                mBannerListener.onAdFailedToLoad(ERROR_CODE_NO_FILL);
                break;

            case AdError.CODE_INTERNAL_ERROR:
                mBannerListener.onAdFailedToLoad(ERROR_CODE_INTERNAL_ERROR);
                break;

            case AdError.CODE_NETWORK_ERROR:
                mBannerListener.onAdFailedToLoad(ERROR_CODE_NETWORK_ERROR);
                break;

            default:
                mBannerListener.onAdFailedToLoad(ERROR_CODE_INTERNAL_ERROR);
                break;
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (ad != mDisplayAd) {
            Log.d(TAG, "Card ad loaded failed");
            mBannerListener.onAdFailedToLoad(ERROR_CODE_INTERNAL_ERROR);
            return;
        }

        View adView = getAdView(mContext, mAdSize, mDisplayAd);
        if (adView != null) {
            Log.d(TAG, "Card ad loaded success");
            mBannerListener.onAdLoaded(adView);
        } else {
            Log.d(TAG, "Card ad loaded failed");
            mBannerListener.onAdFailedToLoad(ERROR_CODE_INTERNAL_ERROR);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.d(TAG, "Card ad clicked");
        mBannerListener.onAdClicked();
    }

    @Override
    public void onAdImpression(Ad ad) {
        Log.d(TAG, "Card ad impression");
    }

    @Override
    public void onAdMute(Ad ad) {
        Log.d(TAG, "Card ad mute");
    }

    @Override
    public void onAdUnmute(Ad ad) {
        Log.d(TAG, "Card ad unmute");
    }

    @Override
    public void onVideoStart(Ad ad) {
        Log.d(TAG, "Card ad video start");
    }

    @Override
    public void onVideoEnd(Ad ad) {
        Log.d(TAG, "Card ad video end");
    }

    @Override
    public void onVideoProgress(Ad ad, int i, int i1) {

    }

    private View getAdView(final Context context,
                           final AdSize adSize,
                           final DisplayAd displayAd) {
        int screenWidth;
        int screenHeight;
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);

        if (dm.widthPixels > dm.heightPixels) {
            screenHeight = dm.widthPixels;
            screenWidth = dm.heightPixels;
        } else {
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
        }

        int requiredWidthInPixels = adSize.getWidthInPixels(mContext);
        int requiredHeightInPixels = adSize.getHeightInPixels(mContext);

        if (requiredWidthInPixels > screenWidth) {
            requiredWidthInPixels = screenWidth;
        }
        if (requiredHeightInPixels > screenHeight) {
            requiredHeightInPixels = screenHeight;
        }

        int maxAdWidth = requiredWidthInPixels < screenWidth ? requiredWidthInPixels : screenWidth;
        int maxAdHeight = requiredHeightInPixels < screenHeight ? requiredHeightInPixels : screenHeight;
        int displayAdWidth = displayAd.getSize().width();
        int displayAdHeight = displayAd.getSize().height();
        int newAdWidth;
        int newAdHeight;

        if (displayAdWidth > maxAdWidth || displayAdHeight > maxAdHeight) {
            float widthRatio = (float)displayAdWidth / maxAdWidth;
            float heightRatio = (float)displayAdHeight / maxAdHeight;
            if (widthRatio > heightRatio) {
                newAdWidth = maxAdWidth;
                newAdHeight = (int) (displayAdHeight / widthRatio);
            } else {
                newAdWidth = (int) (displayAdWidth / heightRatio);
                newAdHeight = maxAdHeight;
            }
        } else {
            float widthRatio = (float)maxAdWidth / displayAdWidth;
            float heightRatio = (float)maxAdHeight / displayAdHeight;
            if (widthRatio > heightRatio) {
                newAdWidth = (int) (displayAdWidth * heightRatio);
                newAdHeight = maxAdHeight;
            } else {
                newAdWidth = maxAdWidth;
                newAdHeight = (int) (displayAdHeight * widthRatio);
            }
        }

        displayAd.resize(newAdWidth);

        View adView = displayAd.getView();
        ViewGroup.LayoutParams adViewLayoutParams = adView.getLayoutParams();
        RelativeLayout.LayoutParams adViewRelativeLayoutParams = (RelativeLayout.LayoutParams) adViewLayoutParams;
        adViewRelativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        adView.setLayoutParams(adViewRelativeLayoutParams);

        RelativeLayout wrapperAdView = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(maxAdWidth, maxAdHeight);
        wrapperAdView.setLayoutParams(params);
        wrapperAdView.setBackgroundColor(Color.BLACK);
        wrapperAdView.addView(adView);

        RelativeLayout customAdView = new RelativeLayout(mContext);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customAdView.setLayoutParams(params);
        customAdView.addView(wrapperAdView);

        return customAdView;
    }
}
