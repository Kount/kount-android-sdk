# Android SDK Release Notes

## 4.3.2
* Patched vulnerability CWE-926
* Minimum Version on Android API: 26 (Oreo)

## 4.3.1
* QA Environment hidden
* Updated QA environment URL
* Merchant ID changed to String
* Added Changelog
* Enhanced Unit Tests
* Minimum Version on Android API: 21 (Lollipop)
* Regex updated for client_id and session_id
* Autogeneration of session_id when the field is null or empty


## 4.3.0
* Added the new feature of Completion Handler. This feature can help you perform any action of your choice just after the DDC Data Collection completes. The needed actions can be performed in the completion block.

## 4.2.4
* Fixed a memory leak bug which could occur if you turn off the Analytics flag and have many Activities.
* Fixed maven issue with release.

## 4.2.3
* Fix for gradle implementations so that the library installs properly into a project.

## 4.2.2
* Fixes a race condition where the collection handlers say they have completed prior to transmissions of the collection data to servers.
* Any users of 4.2.X should upgrade to this to resolve the possible race condition.

## 4.2.1
* This release contains bug fixes related to timing issue when Collections were not started if the User did not switch Activities in an application. Additional documentation for multiple platforms and hybrids available at support.kount.com.

## 4.1.3
* Fixes an issue where the transmission of data collected was being delayed in some use cases. It will now transmit data as soon as the work is completed. This impacted customers using 4.1.2 and older who enabled the Analytics features and either run an Single Page Application(SPA) or don't transition to a second view prior to requesting the data from Kount.

## 4.1.2
* Fixes NullPointer issue when connecting/disconnecting charging cable.

## 4.1.1
* Updated SDK to collect additional data points.

## 4.1.0

* Added new UI element collection capabilities for analytics
* Minimum Version on Android API: 20 (JellyBean)

## 4.0.0
Enhancements to the Android SDK for Kount customers including:
* City Level location information
* Utilizes Play Store libraries if available.
* Enhanced timing metrics
* Android10 updates
* Security and Bug fixes

NOTE: If your application is not running on a device with the recommended OS version(Android10), you may not get all the features in this release.  Features are automatically enabled by the OS version of the device.

Kount's Android SDK is compatible with:
* Minumum Version of Android API: API 19
* Recommended Target Version of Android OS: 29 (Android10)
* Kount Recommends clients to include Google Play Store libraries if client app does not already.
* Tested on the following Android OS Versions:
    * 4.4.2 (KitKat)
    * 5.1 (Lolipop)
    * 6.0 (Marshmallow)
    * 7.1 (Nougat)
    * 8.1 (Oreo)
    * 9 (Pie)
    * 10 (Android10)

## 3.3
* Added support for Instant Apps.
  Note: Due to Android restrictions; Instant App Collection may be degraded compared to a full, installed Android App.
* Improved LocationCollector process
* Improved reporting of errors and timeouts

## 3.2
* Significantly reduced collection time
* Bugfixes for LocationCollector

## 3.1
* Updated SDK in preparation for future enhancements. No coding interface changes implemented, completely compatible with established 3.0 integrations.

## 3.0
* Updated SDK with more intuitive configuration and collection
* Improved collection capabilities
