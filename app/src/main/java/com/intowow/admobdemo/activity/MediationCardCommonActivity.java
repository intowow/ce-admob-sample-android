package com.intowow.admobdemo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.intowow.admobdemo.R;


public class MediationCardCommonActivity extends Activity {

    private static final String TAG = MediationCardCommonActivity.class.getSimpleName();

    private static final String AD_UNIT_ID = "ca-app-pub-4766247142778820/6671046640";

    private AdView mAdView = null;

    // UI
    private Button mLoadAdBtn = null;
    private RelativeLayout mAdViewLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_mediation_common);

        setupUI();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdViewLayout.removeAllViews();
            mAdView.setAdListener(null);
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void setupUI() {
        mAdViewLayout = (RelativeLayout) findViewById(R.id.adView);

        mLoadAdBtn = (Button) findViewById(R.id.loadAd);
        mLoadAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loadAd();
            }
        });
    }

    private void loadAd() {
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
        mAdView = new AdView(this);
        mAdView.setAdUnitId(AD_UNIT_ID);
        // You can customize your ad size
        mAdView.setAdSize(new AdSize(300, 169));
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                mAdViewLayout.removeAllViews();
                mAdViewLayout.addView(mAdView);
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "Card ad onAdClicked");
            }

            @Override
            public void onAdImpression() {
                Log.d(TAG, "Card ad onAdImpression");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "Card ad left application");
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "Card ad open ");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "Card ad closed ");
            }
        });
        mAdView.loadAd(new AdRequest.Builder().build());
    }

}
