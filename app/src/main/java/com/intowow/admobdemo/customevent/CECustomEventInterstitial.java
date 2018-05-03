package com.intowow.admobdemo.customevent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import com.intowow.sdk.Ad;
import com.intowow.sdk.AdError;
import com.intowow.sdk.InterstitialAd;
import com.intowow.sdk.RequestInfo;

import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

/**
 * Custom event interstitial for Intowow SDK
 * Minimum support Intowow SDK 3.36
 */
public class CECustomEventInterstitial implements CustomEventInterstitial, InterstitialAd.InterstitialAdListener {

    private static final String TAG = CECustomEventInterstitial.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;

    private CustomEventInterstitialListener mInterstitialListener = null;

    private InterstitialAd mInterstitialAd = null;

    private boolean mAutoCloseWhenEngaged = false;

    @Override
    public void requestInterstitialAd(Context context,
                                      CustomEventInterstitialListener customEventInterstitialListener,
                                      String serverParameter,
                                      MediationAdRequest mediationAdRequest,
                                      Bundle customEventExtras) {
        // Error checking
        if (customEventInterstitialListener == null) {
            Log.w(TAG, "Failed to request interstitial ad, listener is null");
            return;
        }

        // The SDK requires activity context to initialize, so check that the context
        // provided by the app is an activity context before initializing.
        if (!(context instanceof Activity)) {
            // Context not an Activity context, log the reason for failure and fail the
            // initialization.
            Log.w(TAG, "Failed to request interstitial ad, context is null or not activity");
            customEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        final String placementId = serverParameter;
        if (TextUtils.isEmpty(placementId)) {
            Log.w(TAG, "Failed to request interstitial ad, placement id is null");
            customEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        Log.d(TAG, "Placement is: " + placementId);

        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
        }

        mInterstitialListener = customEventInterstitialListener;

        mInterstitialAd = new InterstitialAd(context);

        //	you can close the interstitial ad while user engaging the ad
        //
        mInterstitialAd.setAutoCloseWhenEngaged(mAutoCloseWhenEngaged);
        mInterstitialAd.setAdListener(this);
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setPlacement(placementId);
        requestInfo.setTimeout(DEFAULT_TIMEOUT_MILLIS);
        mInterstitialAd.loadAd(requestInfo);
    }

    @Override
    public void showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onDestroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    // Below @Override function for InterstitialAdListener
    @Override
    public void onError(Ad ad, AdError adError) {
        Log.d(TAG, "Failed to load interstitial ad, Error: " + (adError == null ? String.valueOf(AdError.CODE_NO_FILL_ERROR) : String.valueOf(adError.getErrorCode())));
        if (adError == null) {
            mInterstitialListener.onAdFailedToLoad(ERROR_CODE_NO_FILL);
            return;
        }

        switch (adError.getErrorCode()) {
            case AdError.CODE_NO_FILL_ERROR:
            case AdError.CODE_REQUEST_TIMEOUT_ERROR:
                mInterstitialListener.onAdFailedToLoad(ERROR_CODE_NO_FILL);
                break;

            case AdError.CODE_INTERNAL_ERROR:
                mInterstitialListener.onAdFailedToLoad(ERROR_CODE_INTERNAL_ERROR);
                break;

            case AdError.CODE_NETWORK_ERROR:
                mInterstitialListener.onAdFailedToLoad(ERROR_CODE_NETWORK_ERROR);
                break;

            default:
                mInterstitialListener.onAdFailedToLoad(ERROR_CODE_INTERNAL_ERROR);
                break;
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        Log.d(TAG,"Interstitial ad loaded success");
        mInterstitialListener.onAdLoaded();
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        Log.d(TAG,"Interstitial ad displayed");
        mInterstitialListener.onAdOpened();
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        Log.d(TAG,"Interstitial ad dismissed");
        mInterstitialListener.onAdClosed();
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.d(TAG,"Interstitial ad clicked");
        mInterstitialListener.onAdClicked();
    }

    @Override
    public void onAdImpression(Ad ad) {
        Log.d(TAG,"Interstitial ad impression");
    }

    @Override
    public void onAdMute(Ad ad) {
        Log.d(TAG,"Interstitial ad mute");
    }

    @Override
    public void onAdUnmute(Ad ad) {
        Log.d(TAG,"Interstitial ad unmute");
    }

    @Override
    public void onVideoStart(Ad ad) {
        Log.d(TAG,"Interstitial ad video start");
    }

    @Override
    public void onVideoEnd(Ad ad) {
        Log.d(TAG,"Interstitial ad video end");
    }

    @Override
    public void onVideoProgress(Ad ad, int i, int i1) {

    }
}
