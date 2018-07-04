# ce-admob-sample-android

## Mediate CrystalExpress Ads through AdMob

The full integration guide: https://intowow.gitbooks.io/crystalexpress-documentation-v3-x/content/mediation-guidelines/admob/android.html

The CrystalExpress AdMob Custom Event allows AdMob publishers to add CrystalExpress as a Custom Ad Network within the AdMob platform.

CrystalExpress provides four ad formats for AdMob mediation. The relationship between AdMob ad unit and ad format in CrystalExpress is as following:

| AdMob ad unit | AD format from CrystalExpress |
| --- | --- |
| Banner | Card AD |
| Rewarded Video | Rewarded Video AD |
| Interstitial | Splash AD |
| Native Advanced | Native AD |

Before adding CrystalExpress as Custom network, you have to integrate AdMob SDK by following the instructions on the [AdMob website](https://developers.google.com/admob/android/quick-start).


** NOTICE: This porject does not contain CrystalExpress SDK. Please contact your Intowow account manager. We will provide the appropriate version of SDK and Crystal ID to fit your needs.**

The custom event is under folder 'app/src/main/java/com/intowow/admobdemo/customevent/' and 'mediationadapter/src/main/java/com/intowow/admob/rewardedvideo/'


## CHANGELOG

#### Version 7 (2018-07-05)

#### Bug fixes
* The AdMob custom event sample app will crash when playing card ad.
* The AdMob custom event demo app will change to non-test mode when load rewarded video.


#### Version 6 (2018-05-11)

#### Features
* Refine AdMob custom events.


#### Version 5 (2018-04-19)

#### Features
* AS-768: Add interface to enable onRewardedComplete callback above AdMob ver.12.0.0


#### Version 4 (2018-01-05)

#### Features
* Implement AdMob Interstitial Custom Event.


#### Version 3 (2017-09-28)

#### Features
* Implement AdMob Native Advanced Custom Event


#### Version 2 (2017-09-04)

#### Features
* Implement AdMob adapter for CE rewarded video.


#### Version 1 (2017-08-23)

#### Features
* Implement AdMob Custom Events for CE Card.