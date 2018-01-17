package com.intowow.admob.rewardedvideo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdListener;
import com.intowow.sdk.Ad;
import com.intowow.sdk.AdError;
import com.intowow.sdk.I2WAPI;
import com.intowow.sdk.RewardedVideoAd;

/**
 * Mediation adapter rewarded video for Intowow SDK
 * minimum support Intowow SDK 3.33
 */
public class CEMediationAdapterRewardedVideo implements MediationRewardedVideoAdAdapter {
    protected static final String TAG = CEMediationAdapterRewardedVideo.class.getSimpleName();

    private Context mContext;
    protected RewardedVideoAd mRewardedVideoAd = null;
    protected boolean mInit = false;
    protected boolean mHasRewardedVideoAd = false;
    private boolean mAutoCloseWhenEngaged = false;
    private String mPlacementId = "";
    private MediationRewardedVideoAdListener mListener = null;

    @Override
    public void initialize(Context context,
                           MediationAdRequest mediationAdRequest,
                           String unUsed,
                           MediationRewardedVideoAdListener listener,
                           Bundle serverParameters,
                           Bundle mediationExtras) {

        if (listener == null) {
            Log.w(TAG, "The SDK requires mediation rewarded video ad listener to initialize");
            return;
        }

        // The SDK requires activity context to initialize, so check that the context
        // provided by the app is an activity context before initializing.
        if (!(context instanceof Activity)) {
            // Context not an Activity context, log the reason for failure and fail the
            // initialization.
            Log.w(TAG, "The SDK requires an Activity context to initialize");
            listener.onInitializationFailed(
                    CEMediationAdapterRewardedVideo.this, AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        /**
         * Get the Ad Unit ID for the SDK from serverParameters bundle using the pre
         * configured keys.
         *
         * For custom events, there is a single parameter that can be accessed via
         *
         * String serverParameter = serverParameters.getString(
         *         MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
         */
        mPlacementId = serverParameters.getString(MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
        if (TextUtils.isEmpty(mPlacementId)) {
            Log.w(TAG, "The SDK requires placement id to initialize");
            listener.onAdFailedToLoad(this, AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        mContext = context;
        mListener = listener;

        I2WAPI.init(mContext, false, false);
        mInit = true;
    }

    @Override
    public void loadAd(MediationAdRequest mediationAdRequest, Bundle bundle, Bundle bundle1) {
        //	request the rewarded video ad
        //
        mRewardedVideoAd = new RewardedVideoAd(mContext, mPlacementId);

        //	you can close the rewarded video ad while user engaging the ad
        //
        mRewardedVideoAd.setAutoCloseWhenEngaged(mAutoCloseWhenEngaged);

        mRewardedVideoAd.setAdListener(new RewardedVideoAd.RewardedVideoAdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                Log.d(TAG, "Failed to load rewarded video ad, Error: " + (error == null ? String.valueOf(AdError.CODE_NO_FILL_ERROR) : String.valueOf(error.getErrorCode())));
                releaseRewardedVideoAd();
                if (error == null) {
                    mListener.onAdFailedToLoad(CEMediationAdapterRewardedVideo.this, AdRequest.ERROR_CODE_NO_FILL);
                    return;
                }

                switch (error.getErrorCode()) {
                    case AdError.CODE_INTERNAL_ERROR:
                        mListener.onAdFailedToLoad(CEMediationAdapterRewardedVideo.this, AdRequest.ERROR_CODE_INTERNAL_ERROR);
                        break;

                    case AdError.CODE_NETWORK_ERROR:
                        mListener.onAdFailedToLoad(CEMediationAdapterRewardedVideo.this, AdRequest.ERROR_CODE_NETWORK_ERROR);
                        break;

                    case AdError.CODE_INVALID_PLACEMENT_ERROR:
                        mListener.onAdFailedToLoad(CEMediationAdapterRewardedVideo.this, AdRequest.ERROR_CODE_INVALID_REQUEST);
                        break;

                    case AdError.CODE_NO_FILL_ERROR:
                    case AdError.CODE_REQUEST_TIMEOUT_ERROR:
                    default:
                        mListener.onAdFailedToLoad(CEMediationAdapterRewardedVideo.this, AdRequest.ERROR_CODE_NO_FILL);
                        break;
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "Rewarded video ad loaded success");
                mHasRewardedVideoAd = true;
                mListener.onAdLoaded(CEMediationAdapterRewardedVideo.this);
            }

            @Override
            public void onRewardedVideoDisplayed(Ad ad) {
                Log.d(TAG, "Rewarded video ad displayed");
                mListener.onAdOpened(CEMediationAdapterRewardedVideo.this);
            }

            @Override
            public void onRewardedVideoDismissed(Ad ad) {
                Log.d(TAG, "Rewarded video ad dismissed");
                releaseRewardedVideoAd();
                mListener.onAdClosed(CEMediationAdapterRewardedVideo.this);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(TAG, "Rewarded video ad clicked");
                mListener.onAdClicked(CEMediationAdapterRewardedVideo.this);
            }

            @Override
            public void onAdImpression(Ad ad) {
                Log.d(TAG, "Rewarded video ad impression");
            }

            @Override
            public void onAdMute(Ad ad) {
                Log.d(TAG, "Rewarded video ad mute");
            }

            @Override
            public void onAdUnmute(Ad ad) {
                Log.d(TAG, "Rewarded video ad unmute");
            }

            @Override
            public void onVideoEnd(Ad arg0) {
                Log.d(TAG, "Rewarded video ad video end");
            }

            @Override
            public void onVideoProgress(Ad arg0, int totalDuration, int currentPosition) {
            }

            @Override
            public void onRewarded(Ad ad) {
                Log.d(TAG, "Rewarded video ad rewarded");
                mListener.onRewarded(CEMediationAdapterRewardedVideo.this, new RewardItem() {
                    @Override
                    public String getType() {
                        return "Reward";
                    }

                    @Override
                    public int getAmount() {
                        return 50;
                    }
                });
            }

            @Override
            public void onVideoStart(Ad arg0) {
                Log.d(TAG, "Rewarded video ad video start");
                mListener.onVideoStarted(CEMediationAdapterRewardedVideo.this);
            }

        });

        mRewardedVideoAd.loadAd(10000);
    }

    @Override
    public void showVideo() {
        //	show rewarded video ad here
        //
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public boolean isInitialized() {
        return mInit;
    }

    @Override
    public void onDestroy() {
        releaseRewardedVideoAd();
        mContext = null;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    protected boolean hasRequestedRewardedVideoAd() {
        return mHasRewardedVideoAd;
    }

    protected void releaseRewardedVideoAd() {
        if(mRewardedVideoAd != null) {
            mRewardedVideoAd.destroy();
            mRewardedVideoAd = null;
        }

        mHasRewardedVideoAd = false;
    }
}
