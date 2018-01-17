package com.intowow.admobdemo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAdView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.intowow.admobdemo.R;
import com.intowow.admobdemo.customevent.CECustomEventNativeAdvanced;

import static com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_TOP_RIGHT;

public class MediationNativeCommonActivity extends Activity {

    private static final String TAG = MediationNativeCommonActivity.class.getSimpleName();

    private static final String AD_UNIT_ID = "Your-ad-unit-id";

    // UI
    private Button mLoadAdBtn = null;
    private RelativeLayout mAdViewLayout = null;

    private NativeAd mNativeAd = null;
    private NativeAdView mNativeAdView = null;

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
        destroyAd();
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

    private void destroyAd() {
        if (mNativeAd instanceof NativeAppInstallAd) {
            if (((NativeAppInstallAd) mNativeAd).getVideoController().hasVideoContent()) {
                ((NativeAppInstallAdView) mNativeAdView).setMediaView(null);
            }
            ((NativeAppInstallAd) mNativeAd).destroy();
        } else if (mNativeAd instanceof NativeContentAd) {
            if (((NativeContentAd) mNativeAd).getVideoController().hasVideoContent()) {
                ((NativeContentAdView) mNativeAdView).setMediaView(null);
            }
            ((NativeContentAd) mNativeAd).destroy();
        }
        mNativeAd = null;
        if (mAdViewLayout != null) {
            mAdViewLayout.removeAllViews();
        }
        if (mNativeAdView != null) {
            mNativeAdView.destroy();
            mNativeAdView = null;
        }
    }

    private void loadAd() {
        // Destroy previous Ad
        destroyAd();

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

        NativeAppInstallAdView appInstallAdView = (NativeAppInstallAdView) getLayoutInflater()
                .inflate(R.layout.item_admob_native_app_install, null);

        // [Notice]
        // This is for intowow native custom event using.
        // Set media view width & height to custom event.
        // Let native custom event to resize ad view to fit media view layout.
        MediaView mediaView = (MediaView) appInstallAdView.findViewById(R.id.native_main_media_layout);
        Bundle extras = new CECustomEventNativeAdvanced.CustomEventExtrasBundleBuilder()
                .setMediaViewWidth(mediaView.getLayoutParams().width)
                .setMediaViewHeight(mediaView.getLayoutParams().height)
                .build();

        AdRequest nativeAdRequest = new AdRequest.Builder()
                .addCustomEventExtrasBundle(CECustomEventNativeAdvanced.class, extras).build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).setAdChoicesPlacement(ADCHOICES_TOP_RIGHT).build();

        // The request is content ad
        AdLoader adLoader = new AdLoader.Builder(this, AD_UNIT_ID).forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
            @Override
            public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                Log.i(TAG, "onContentAdLoaded");
                mNativeAd = nativeContentAd;
                populateAdView();
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.i(TAG, "Custom event native ad failed with code: " + errorCode);
                Toast.makeText(MediationNativeCommonActivity.this,
                        "Custom event native ad failed with code: " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }
        }).withNativeAdOptions(adOptions).build();

        adLoader.loadAd(nativeAdRequest);
    }

    private void populateAdView() {
        if (mNativeAd instanceof NativeContentAd) {
            NativeContentAd contentAd = (NativeContentAd) mNativeAd;
            NativeContentAdView contentAdView = (NativeContentAdView) getLayoutInflater()
                    .inflate(R.layout.item_admob_native_content, null);
            mNativeAdView = contentAdView;
            contentAdView.setNativeAd(contentAd);
            contentAdView.setHeadlineView(contentAdView.findViewById(R.id.native_title));
            contentAdView.setBodyView(contentAdView.findViewById(R.id.native_text));
            contentAdView.setCallToActionView(contentAdView.findViewById(R.id.native_cta));
            contentAdView.setLogoView(contentAdView.findViewById(R.id.native_icon_image));
            contentAdView.setAdvertiserView(contentAdView.findViewById(R.id.native_privacy_information_icon_image));
            contentAdView.setMediaView((MediaView) contentAdView.findViewById(R.id.native_main_media_layout));

            // Some assets are guaranteed to be in every NativeContentAd.
            // [Notice]
            // Headline and body will have default value “Sponsor” and “Description” when ad don’t have these fields,
            // feel free to customized these value
            CharSequence headline = contentAd.getHeadline();
            if (headline != null && headline.equals("Sponsor")) {
                headline = "";
            }
            ((TextView) contentAdView.getHeadlineView()).setText(headline);

            CharSequence body = contentAd.getBody();
            if (body != null && body.equals("Description")) {
                body = "";
            }
            ((TextView) contentAdView.getBodyView()).setText(body);
            ((TextView) contentAdView.getCallToActionView()).setText(contentAd.getCallToAction());
            ((TextView) contentAdView.getAdvertiserView()).setText(contentAd.getAdvertiser());

            // Some aren't guaranteed, however, and should be checked.
            NativeAd.Image logoImage = contentAd.getLogo();

            if (logoImage == null) {
                contentAdView.getLogoView().setVisibility(View.INVISIBLE);
            } else {
                ((ImageView) contentAdView.getLogoView())
                        .setImageDrawable(logoImage.getDrawable());
                contentAdView.getLogoView().setVisibility(View.VISIBLE);
            }
        }

        if (mAdViewLayout != null && mNativeAdView != null) {
            mAdViewLayout.removeAllViews();
            mAdViewLayout.addView(mNativeAdView);
        }
    }

}
