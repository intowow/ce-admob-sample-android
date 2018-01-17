package com.intowow.admobdemo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.intowow.admobdemo.R;

public class MediationRewardedVideoCommonActivity extends Activity implements RewardedVideoAdListener {

    private static final String TAG = MediationRewardedVideoCommonActivity.class.getSimpleName();

    private static final String AD_UNIT_ID = "ca-app-pub-4766247142778820/5380760094";

    private RewardedVideoAd mRewardedVideoAd = null;

    // UI
    private Button mLoadAdBtn = null;
    private Button mShowAdBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_mediation_common);

        setupUI();

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
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
        mShowAdBtn.setVisibility(View.VISIBLE);
        mShowAdBtn.setEnabled(false);
        mShowAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showAd();
            }
        });
    }

    private void loadAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
        }
    }

    private void showAd() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        } else {
            Toast.makeText(this, "Dude, your ad's not loaded yet.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        mShowAdBtn.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
        mShowAdBtn.setEnabled(false);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(this, "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad, error code: " + errorCode, Toast.LENGTH_SHORT).show();
    }
}
