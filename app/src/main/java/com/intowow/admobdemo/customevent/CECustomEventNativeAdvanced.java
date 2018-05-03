package com.intowow.admobdemo.customevent;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.mediation.NativeContentAdMapper;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventNative;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;
import com.intowow.sdk.Ad;
import com.intowow.sdk.AdError;
import com.intowow.sdk.AdListener;
import com.intowow.sdk.NativeAd;
import com.intowow.sdk.RequestInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Custom event native advanced for Intowow SDK
 * Minimum support Intowow SDK 3.33
 */
public class CECustomEventNativeAdvanced implements CustomEventNative {

    private static final String TAG = CECustomEventNativeAdvanced.class.getSimpleName();

    private static final int DRAWABLE_FUTURE_TIMEOUT_SECONDS = 10;

    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;

    private CustomEventNativeListener mCustomEventNativeListener;
    private NativeAd mNativeAd;
    private Context mContext;
    private NativeAd.MediaView mMediaView;
    private Bundle mExtras;

    @Override
    public void requestNativeAd(Context context,
                                CustomEventNativeListener customEventNativeListener,
                                String serverParameter,
                                NativeMediationAdRequest nativeMediationAdRequest,
                                Bundle extras) {
        // Error checking
        if (customEventNativeListener == null) {
            Log.w(TAG, "Failed to request native advanced ad, listener is null");
            return;
        }

        if (context == null) {
            Log.w(TAG, "Failed to request native advanced ad, context is null");
            customEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        // Verify that the request is content ad.
        if (!nativeMediationAdRequest.isContentAdRequested()) {
            Log.w(TAG, "Failed to request native advanced ad. content ad should be "
                    + "requested");
            customEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        String placementId = serverParameter;
        if (TextUtils.isEmpty(placementId)) {
            Log.w(TAG, "Failed to request native advanced ad, placement id is null");
            customEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        Log.d(TAG, "Placement is: " + placementId);

        mExtras = extras;
        mContext = context;
        mCustomEventNativeListener = customEventNativeListener;

        mNativeAd = new NativeAd(context);
        mNativeAd.setAdListener(new NativeListener(mNativeAd, nativeMediationAdRequest));
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setPlacement(placementId);
        requestInfo.setTimeout(DEFAULT_TIMEOUT_MILLIS);
        mNativeAd.loadAd(requestInfo);
    }

    @Override
    public void onDestroy() {
        if (mMediaView != null) {
            mMediaView.destroy();
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    private class NativeListener implements AdListener {
        private NativeAd mNativeAd;
        private NativeMediationAdRequest mMediationAdRequest;
        /**
         * Flag to determine whether or not an impression callback from Intowow SDK has already been
         * sent to the Google Mobile Ads SDK.
         */
        private boolean mIsImpressionRecorded;

        private NativeListener(NativeAd nativeAd, NativeMediationAdRequest mediationAdRequest) {
            mNativeAd = nativeAd;
            mMediationAdRequest = mediationAdRequest;
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            Log.d(TAG, "Failed to load native advanced ad, Error: " + (adError == null ? String.valueOf(AdError.CODE_NO_FILL_ERROR) : String.valueOf(adError.getErrorCode())));
            if (adError == null) {
                mCustomEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                return;
            }

            switch(adError.getErrorCode()) {
                case AdError.CODE_INTERNAL_ERROR:
                    mCustomEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
                    break;

                case AdError.CODE_NETWORK_ERROR:
                    mCustomEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
                    break;

                case AdError.CODE_INVALID_PLACEMENT_ERROR:
                    mCustomEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
                    break;

                case AdError.CODE_NO_FILL_ERROR:
                case AdError.CODE_REQUEST_TIMEOUT_ERROR:
                default:
                    mCustomEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                    break;
            }
        }

        @Override
        public void onAdLoaded(Ad ad) {
            if (ad != mNativeAd) {
                Log.w(TAG, "Ad loaded is not a native ad.");
                mCustomEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
                return;
            }

            try {
                int screenWidth;
                DisplayMetrics dm = new DisplayMetrics();
                ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                if (dm.widthPixels > dm.heightPixels) {
                    screenWidth = dm.heightPixels;
                } else {
                    screenWidth = dm.widthPixels;
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                Map<String, Object> adProps = new HashMap<String, Object>();
                mMediaView = new NativeAd.MediaView(mContext, adProps);
                mMediaView.setLayoutParams(params);
                mMediaView.setNativeAd(mNativeAd);

                NativeAdOptions options = mMediationAdRequest.getNativeAdOptions();
                // We always convert the ad into content ad.
                final ContentAdMapper mapper = new ContentAdMapper(mContext, mNativeAd, mMediaView, options, mExtras);
                mapper.mapNativeAd(new NativeAdMapperListener() {
                    @Override
                    public void onMappingSuccess() {
                        Log.d(TAG,"Native advanced ad loaded success");
                        mCustomEventNativeListener.onAdLoaded(mapper);
                    }

                    @Override
                    public void onMappingFailed() {
                        Log.d(TAG,"Native advanced ad load failed");
                        mCustomEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                    }
                });

            } catch (Exception e) {
                Log.e("error", e.getStackTrace().toString());
            }

        }

        @Override
        public void onAdClicked(Ad ad) {
            Log.d(TAG,"Native advanced ad clicked");
            mCustomEventNativeListener.onAdClicked();
            mCustomEventNativeListener.onAdOpened();
        }

        @Override
        public void onAdImpression(Ad ad) {
            if (mIsImpressionRecorded) {
                Log.d(TAG, "Received onAdImpression callback for a native whose impression"
                        + " is already recorded. Ignoring the duplicate callback.");
                return;
            }
            Log.d(TAG,"Native advanced ad impression");
            mCustomEventNativeListener.onAdImpression();
            mIsImpressionRecorded = true;
        }

        @Override
        public void onAdMute(Ad ad) {
            Log.d(TAG,"Native advanced ad mute");
        }

        @Override
        public void onAdUnmute(Ad ad) {
            Log.d(TAG,"Native advanced ad unmute");
        }

        @Override
        public void onVideoStart(Ad ad) {
            Log.d(TAG,"Native advanced ad video start");
        }

        @Override
        public void onVideoEnd(Ad ad) {
            Log.d(TAG,"Native advanced ad video end");
        }

        @Override
        public void onVideoProgress(Ad ad, int totalDuration, int currentPosition) {

        }
    }

    class ContentAdMapper extends NativeContentAdMapper {

        /**
         * The Intowow native ad to be mapped.
         */
        private NativeAd mNativeAd = null;

        private NativeAd.MediaView mMediaView = null;

        /**
         * Google Mobile Ads native ad options.
         */
        private NativeAdOptions mNativeAdOptions = null;

        private Context mContext = null;

        private int mCallToActionId = -1;

        private boolean mCallToActionRegistered = false;

        private Bundle mExtras = null;

        /**
         * Default constructor for {@link ContentAdMapper}.
         *
         * @param nativeAd  The Intowow native ad to be mapped.
         * @param adOptions {@link NativeAdOptions} containing the preferences to be used when
         *                  mapping the native ad.
         */
        public ContentAdMapper(Context context, NativeAd nativeAd, NativeAd.MediaView mediaView, NativeAdOptions adOptions, Bundle extras) {
            ContentAdMapper.this.mContext = context;
            ContentAdMapper.this.mNativeAd = nativeAd;
            ContentAdMapper.this.mMediaView = mediaView;
            ContentAdMapper.this.mNativeAdOptions = adOptions;
            ContentAdMapper.this.mExtras = extras;
        }

        /**
         * This method will map the Intowow {@link #mNativeAd} to this mapper and send a success
         * callback if the mapping was successful or a failure callback if the mapping was
         * unsuccessful.
         *
         * @param mapperListener used to send success/failure callbacks when mapping is done.
         */
        public void mapNativeAd(NativeAdMapperListener mapperListener) {
            // Map all required assets (headline, body, icon and call to action).
            // The default value for Headline, Body and Call_to_action field
            String adTitle = mNativeAd.getAdTitle();
            setHeadline(adTitle != null ? adTitle : "Sponsor");
            String adBody = mNativeAd.getAdBody();
            setBody(adBody != null ? adBody : "Description");
            NativeAd.Image adIcon = mNativeAd.getAdIcon();
            if (adIcon != null) {
                setLogo(new CENativeAdImage(Uri.parse(adIcon.getUrl())));
            }
            String callToAction = mNativeAd.getAdCallToAction();
            setCallToAction(callToAction != null ? callToAction : "Learn More");
            setMediaView(resizeMediaView(mNativeAd, mMediaView, mExtras));

            // Respect the publisher's setting whether to return a drawable or not.
            boolean urlsOnly = false;
            if (mNativeAdOptions != null) {
                urlsOnly = mNativeAdOptions.shouldReturnUrlsForImageAssets();
            }

            if (urlsOnly) {
                mapperListener.onMappingSuccess();
            } else {
                new DownloadDrawablesAsync(mapperListener).execute(ContentAdMapper.this);
            }
        }

        private View resizeMediaView(final NativeAd nativeAd, final NativeAd.MediaView mediaView, final Bundle extras) {
            RelativeLayout adView = new RelativeLayout(mContext);
            int targetAdViewWidth = RelativeLayout.LayoutParams.MATCH_PARENT;
            int targetAdViewHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;
            if (extras != null) {
                if (extras.containsKey(CustomEventExtrasBundleBuilder.KEY_MEDIA_VIEW_WIDTH)) {
                    targetAdViewWidth = extras.getInt(CustomEventExtrasBundleBuilder.KEY_MEDIA_VIEW_WIDTH);
                }
                if (extras.containsKey(CustomEventExtrasBundleBuilder.KEY_MEDIA_VIEW_HEIGHT)) {
                    targetAdViewHeight = extras.getInt(CustomEventExtrasBundleBuilder.KEY_MEDIA_VIEW_HEIGHT);
                }
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(targetAdViewWidth, targetAdViewHeight);
            adView.setLayoutParams(params);
            adView.setBackgroundColor(Color.BLACK);

            int maxAdWidth;
            int maxAdHeight;
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
            if (dm.widthPixels > dm.heightPixels) {
                maxAdHeight = dm.widthPixels;
                maxAdWidth = dm.heightPixels;
            } else {
                maxAdWidth = dm.widthPixels;
                maxAdHeight = dm.heightPixels;
            }

            int adWidth = nativeAd.getSize().width();
            int adHeight = nativeAd.getSize().height();

            if (targetAdViewWidth < 0 && targetAdViewHeight > 0) {
                maxAdHeight = targetAdViewHeight;
            } else if (targetAdViewWidth > 0 && targetAdViewHeight < 0) {
                maxAdWidth = targetAdViewWidth;
            } else {
                maxAdWidth = targetAdViewWidth;
                maxAdHeight = targetAdViewHeight;
            }

            if (maxAdWidth > 0 || maxAdHeight > 0) {
                if (adWidth > maxAdWidth || adHeight > maxAdHeight) {
                    float widthRatio = (float) adWidth / maxAdWidth;
                    float heightRatio = (float) adHeight / maxAdHeight;
                    if (widthRatio > heightRatio) {
                        mediaView.getLayoutParams().width = maxAdWidth;
                        mediaView.getLayoutParams().height = (int) (adHeight / widthRatio);
                    } else {
                        mediaView.getLayoutParams().width = (int) (adWidth / heightRatio);
                        mediaView.getLayoutParams().height = maxAdHeight;
                    }
                } else {
                    float widthRatio = (float) maxAdWidth / adWidth;
                    float heightRatio = (float) maxAdHeight / adHeight;
                    if (widthRatio > heightRatio) {
                        mediaView.getLayoutParams().width = (int) (adWidth * heightRatio);
                        mediaView.getLayoutParams().height = maxAdHeight;
                    } else {
                        mediaView.getLayoutParams().width = maxAdWidth;
                        mediaView.getLayoutParams().height = (int) (adHeight * widthRatio);
                    }
                }
            }

            adView.addView(mediaView);

            return adView;
        }

        @Override
        public void trackView(View view) {
            super.trackView(view);
            // Intowow does its own impression tracking.
            setOverrideImpressionRecording(true);

            setOverrideClickHandling(false);

            // Find call to action button view
            if (view instanceof ViewGroup) {
                mCallToActionId = getButtonViewId((ViewGroup) view);
            }

        }

        private int getButtonViewId(ViewGroup viewGroup) {

            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View overlayView = viewGroup.getChildAt(i);
                if (overlayView instanceof Button) {
                    return overlayView.getId();
                } else if (overlayView instanceof ViewGroup) {
                    int id = getButtonViewId((ViewGroup) overlayView);
                    if (id > 0) {
                        return id;
                    }
                }
            }

            return -1;
        }

        @Override
        public void untrackView(View view) {
            super.untrackView(view);
            // Called when the native ad view no longer needs tracking. Remove any previously
            // added trackers.
            mNativeAd.unregisterView();
        }

        @Override
        public void handleClick(View view) {
            if (view.getId() == mCallToActionId && mCallToActionRegistered == false) {
                mCallToActionRegistered = true;
                mNativeAd.registerViewForInteraction(view);
                view.performClick();
            }
        }
    }

    /**
     * The {@link NativeAdMapperListener} interface is used to notify the success/failure
     * events after trying to map the native ad.
     */
    private interface NativeAdMapperListener {

        /**
         * This method will be called once the native ad mapping is successfully.
         */
        void onMappingSuccess();

        /**
         * This method will be called if the native ad mapping failed.
         */
        void onMappingFailed();
    }

    private class CENativeAdImage extends
            com.google.android.gms.ads.formats.NativeAd.Image {

        /**
         * A drawable for the Image.
         */
        private Drawable mDrawable;

        /**
         * An Uri from which the image can be obtained.
         */
        private Uri mUri;

        /**
         * Default constructor for {@link CENativeAdImage}, requires an {@link Uri}.
         *
         * @param uri required to initialize.
         */
        public CENativeAdImage(Uri uri) {
            this.mUri = uri;
        }

        /**
         * @param drawable set to {@link #mDrawable}.
         */
        protected void setDrawable(Drawable drawable) {
            this.mDrawable = drawable;
        }

        @Override
        public Drawable getDrawable() {
            return mDrawable;
        }

        @Override
        public Uri getUri() {
            return mUri;
        }

        @Override
        public double getScale() {
            // Default scale is 1.
            return 1;
        }
    }

    /**
     * The {@link DownloadDrawablesAsync} class is used to download the drawables and send the
     * necessary callbacks.
     */
    private static class DownloadDrawablesAsync extends AsyncTask<Object, Void, Boolean> {

        /**
         * The image drawable listener used to send the success/fail callbacks once the task
         * completed the download process.
         */
        private NativeAdMapperListener mDrawableListener;

        public DownloadDrawablesAsync(NativeAdMapperListener listener) {
            this.mDrawableListener = listener;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                ContentAdMapper mapper = (ContentAdMapper) params[0];
                ExecutorService executorService = Executors.newCachedThreadPool();

                // Create all the Futures before calling get on them, so that the images can be
                // downloaded in parallel. This HashMap is used to set each drawable to its image
                // once the Future finishes downloading.
                HashMap<CENativeAdImage, Future<Drawable>> futuresMap = new HashMap<>();

                List<com.google.android.gms.ads.formats.NativeAd.Image> images = mapper.getImages();
                if (images != null) {
                    for (int i = 0; i < images.size(); i++) {
                        CENativeAdImage image =
                                (CENativeAdImage) images.get(i);
                        Future<Drawable> drawableFuture = getDrawableFuture(image.getUri(), executorService);
                        futuresMap.put(image, drawableFuture);
                    }
                }

                CENativeAdImage iconImage = (CENativeAdImage) mapper.getLogo();
                if (iconImage != null) {
                    Future<Drawable> drawableFuture = getDrawableFuture(iconImage.getUri(), executorService);
                    futuresMap.put(iconImage, drawableFuture);
                }

                for (Map.Entry<CENativeAdImage, Future<Drawable>> pair : futuresMap.entrySet()) {
                    Drawable drawable;
                    try {
                        drawable =
                                pair.getValue().get(DRAWABLE_FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                        Log.w(TAG, "Exception occurred while waiting for future to return. "
                                + "Returning null as drawable : " + exception);
                        return false;
                    }
                    pair.getKey().setDrawable(drawable);
                }
            } catch (Throwable t) {
                Log.e(TAG, t.toString());
                return false;
            }

            return true;
        }

        /**
         * This method will use the given {@link ExecutorService} to create a
         * {@link Future<Drawable>} which will download the image drawable. The Future will start
         * downloading as soon as the {@link ExecutorService} has a thread available.
         *
         * @param uri             the {@link Uri} from which to get the Image.
         * @param executorService needed to create the {@link Future<Drawable>}.
         * @return a {@link Future<Drawable>} which will start downloading the image from the
         * given Uri.
         */
        private Future<Drawable> getDrawableFuture(final Uri uri, ExecutorService executorService) {
            // The call() will be executed as the threads in executorService's thread pool become
            // available.
            return executorService.submit(new Callable<Drawable>() {
                @Override
                public Drawable call() throws Exception {
                    Bitmap bitmap = BitmapFactory.decodeFile(uri.toString());

                    // Defaulting to a scale of 1.
                    bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
                    return new BitmapDrawable(Resources.getSystem(), bitmap);
                }
            });
        }

        @Override
        protected void onPostExecute(Boolean isDownloadSuccessful) {
            super.onPostExecute(isDownloadSuccessful);
            if (isDownloadSuccessful) {
                // Image download successful, send on success callback.
                mDrawableListener.onMappingSuccess();
            } else {
                mDrawableListener.onMappingFailed();
            }
        }
    }

    /**
     * The {@link CustomEventExtrasBundleBuilder} class is used to create a custom event extras bundle
     * that can be passed to the adapter as extra data to be used in making requests.
     */
    public static final class CustomEventExtrasBundleBuilder {

        // Keys to add and obtain the extra parameters from the bundle.
        public static final String KEY_MEDIA_VIEW_WIDTH = "media_view_width";
        public static final String KEY_MEDIA_VIEW_HEIGHT = "media_view_height";

        /**
         * For media view width
         */
        private int mMediaViewWidth;

        /**
         * For media view height.
         */
        private int mMediaViewHeight;

        public CustomEventExtrasBundleBuilder setMediaViewWidth(int width) {
            this.mMediaViewWidth = width;
            return CustomEventExtrasBundleBuilder.this;
        }

        public CustomEventExtrasBundleBuilder setMediaViewHeight(int height) {
            this.mMediaViewHeight = height;
            return CustomEventExtrasBundleBuilder.this;
        }

        public Bundle build() {
            Bundle extras = new Bundle();
            extras.putInt(KEY_MEDIA_VIEW_WIDTH, mMediaViewWidth);
            extras.putInt(KEY_MEDIA_VIEW_HEIGHT, mMediaViewHeight);
            return extras;
        }
    }
}
