Android Client SDK Setup
========================

Kount's SDK for Android helps integrate Kount's fraud fighting solution into
your Android app.

## Requirements

-   [Kount integration](http://www.kount.com/fraud-detection-software)
-   [Android Studio and Android
    SDK](http://developer.android.com/sdk/index.html)
-   Target Android SDK &gt;= 19(Android 4.4) however, 29(Android10) is recommended for all features to be enabled.
-   Instant App support dependency. You must include the Instant App library at compile time; even if you are not building an Instant App (see Instant App Dependencies below for more information)

NOTE: Due to the restrictions imposed on Instant Apps by Android, Instant App collection may be degraded compared to a full Android App. The current Android Mobile SDK does not support anything below (or earlier than) v4.4.2.

## Installation

Download the [Kount Android
SDK](https://github.com/Kount/kount-android-sdk) and unzip the file in a
folder separate from your project

-   Copy the JAR file in the KountDataCollector folder into the **libs** folder of your app

## Initialization

To set up the data collector, you'll need to set the context, the
merchant ID, configuration for location collection, and the Kount
environment. While testing your integration you'll want to use the
*Test* environment, switching to *Production* when your app is ready to
release.

### Setup

In the main activity of your app, add the following initialization:

``` 
import com.kount.api.DataCollector;
          
@Override
protected void onCreate(Bundle savedInstanceState) {
  ...
  DataCollector.getInstance().setContext(this);
  DataCollector.getInstance().setMerchantID(<MERCHANT_ID>);
  DataCollector.getInstance().setLocationCollectorConfig(DataCollector.LocationConfig.COLLECT);
  // For Test Environment
  DataCollector.getInstance().setEnvironment(DataCollector.ENVIRONMENT_TEST);
  // For Production Environment
  // DataCollector.getInstance().setEnvironment(DataCollector.ENVIRONMENT_PRODUCTION);
  ...
}
```

### Location Permissions

For location collection support you'll need to add these permissions to
your **AndroidManifest.xml** file:

``` 
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

For Android API &gt;= 23, you'll also need to add permission requesting
code to your main activity, similar to the following:

``` 
...
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
  if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, <REQUEST_ID>);
  } else {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, <REQUEST_ID>);
  }
}
...
```

## Collection

Early in the checkout process, start the data collection with a unique 
session ID tied to the transaction, and collect once per unique session
ID.

**NOTE:** Only run collection during the checkout process (and the call
should be made early in the collection process). Calling collection
outside of the checkout process may result in a high proportion of 
collection to RIS calls. This may be misinterpreted by our services as a
DDOS attack. While this is rare, we highlight this to emphasize the
importance that collection only be placed at the beginning of the
checkout process.

Below is an example adding the controller to the onCreate method:

``` 
import com.kount.api.DataCollector;
            
@Override
protected void onCreate(Bundle savedInstanceState) {
  ...
  DataCollector.getInstance().collectForSession(sessionID, new DataCollector.CompletionHandler() {
   /* Add handler code here if desired. The handler is optional. */
    @Override
    public void completed(String sessionID) {
    }
    @Override
    public void failed(String sessionID, final DataCollector.Error error) {
    }
  });
  ...
}
```

## Example

Open the `CheckoutExample` folder in Android Studio to see a simple 
example of using the SDK.


## Instant App dependencies

If your application is not also an Instant App, you will need to include the Instant App library during compile time to your project. This can be done in Android Studio by adding the following line in your module's gradle file under the dependency section:

```
dependencies {
    compile 'com.google.android.instantapps:instantapps:1.1.0'
...
}
```
