# eMotoApp #

## User Interface ##

## App Logic ##


### 1. eMotoBackground Service  ###

  * keeping track of the eMotoCell device and upload to server
  * update authentication token in background
  * handle bluetooth communications (to be Implemented)

```
#!xml
<service
android:name="me.chayut.eMotoLogic.eMotoService"
android:label="eMotoService">
</service>
```

```
#!java
//The filter's action is BROADCAST_ACTION
IntentFilter statusIntentFilter = new IntentFilter( eMotoService.BROADCAST_ACTION);

//Sets the filter's category to DEFAULT
statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

//Instantiates a new DownloadStateReceiver
mServiceResponseReceiver = new ServiceResponseReceiver();

//Registers the DownloadStateReceiver and its intent filters
LocalBroadcastManager.getInstance(this).registerReceiver( mServiceResponseReceiver,statusIntentFilter);
```


### 2. Ads Management Classes ###

### 3. Supporting Class ###

### 4. Dependentcies ###
