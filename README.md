Android Client SDK Setup
========================

Kount's SDK for Android helps integrate Kount's fraud fighting solution into
your Android app.

## Requirements

-   [Kount integration](http://www.kount.com/fraud-detection-software)
-   [Android Studio and Android
    SDK](http://developer.android.com/sdk/index.html)
-   Target Android SDK &gt;= 10

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
  DataCollector.getInstance().setEnvironment(DataCollector.ENVIRONMENT_TEST);
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

Early in the checkout process you'll want to start the data collection
with a unique session ID tied to the transaction, and collect once per unique session ID. For example, in the
onCreate method of an activity early in your transaction workflow, add
the following:

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

Open the `CheckoutExample` folder in Android Studio to see a simple example of using the SDK.


## Migrating to version 3.x 

The interface and workflow of the SDK has changed between version 2 and
version 3. The old version would have you create an instance of the Data
Collector, configure it, make a call to collect, and then implement the
listener methods if you wished to receive feedback regarding the
collection.

With the new version, the Data Collector is implemented as a singleton
and is configured when your main activity is created. The collect call
is now a method on the Data Collector singleton and has an optional
completion handler interface argument you can implement if you wish to
get additional information on the collection. Here are the steps to
upgrade to version 3.x:

-   Remove the old library from your project and replace it with the
    new library.
-   Remove the old initialization code and replace it with the new
    initialization code methods on the DataCollector singleton in your
    main activity.
-   Remove the old call to collect and corresponding listener methods
    and replace it with the collect method on the DataCollector
    singleton, and optionally implement the completion
    handler interface.
