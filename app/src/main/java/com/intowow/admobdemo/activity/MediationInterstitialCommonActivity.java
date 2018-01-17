package com.intowow.admobdemo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.intowow.admobdemo.R;

public class MediationInterstitialCommonActivity extends Activity {

    private static final String TAG = MediationInterstitialCommonActivity.class.getSimpleName();

    private static final String AD_UNIT_ID = "ca-app-pub-4766247142778820/9412596065";

    private InterstitialAd mCustomEventInterstitial = null;

    //UI
    private Button mLoadAdBtn = null;
    private Button mShowAdBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_mediation_common);

        setupUI();
    }

    private void setupUI() {
        mLoadAdBtn = (Button) findViewById(R.id.loadAd);
        mLoadAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loadAd();
            }
        });

        mShowAdBtn = (Button) findViewById(R.id.showAd);
        mShowAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mCustomEventInterstitial != null && mCustomEventInterstitial.isLoaded()) {
                    mCustomEventInterstitial.show();
                }
            }
        });
        mShowAdBtn.setVisibility(View.VISIBLE);
        mShowAdBtn.setEnabled(false);
    }

    private void loadAd() {
        // Sample custom event interstitial.
        mCustomEventInterstitial = new InterstitialAd(this);
        mCustomEventInterstitial.setAdUnitId(AD_UNIT_ID);
        mCustomEventInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(MediationInterstitialCommonActivity.this,
                        "Error loading custom event interstitial, code " + errorCode,
                        Toast.LENGTH_SHORT).show();
                mShowAdBtn.setEnabled(false);
            }

            @Override
            public void onAdLoaded() {
                Toast.makeText(MediationInterstitialCommonActivity.this,
                        "Interstitial ad loaded",
                        Toast.LENGTH_SHORT).show();
                mShowAdBtn.setEnabled(true);
            }

            @Override
            public void onAdOpened() {
                mShowAdBtn.setEnabled(false);
            }

            @Override
            public void onAdClosed() {
                mCustomEventInterstitial.loadAd(new AdRequest.Builder().build());
            }
        });
        mCustomEventInterstitial.loadAd(new AdRequest.Builder().build());
    }

}
