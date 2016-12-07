package momomo00.traceeye;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

/**
 * Created by momomo00 on 16/11/29.
 */

public class LocationUpdater implements
      GoogleApiClient.ConnectionCallbacks
    , GoogleApiClient.OnConnectionFailedListener
    , LocationListener
{
    private final static int UPDATE_INTERVAL_IN_MILLISECONDS = 20000;
    private final static int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private LocationUpdateListener mLocationUpdateListener;

    public LocationUpdater(Context context) {
        MyDefine.showLog("LocationUpdater", "LocationUpdater(Context)");
        mContext = context;
    }

    public void startLocationUpdate() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MyDefine.showLog("LocationUpdater", "onConnected(Bundle)");
        if(!MyPermissionManager.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            return;
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient
                , mLocationRequest
                , this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        MyDefine.showLog("LocationUpdater", "onConnectionSuspended(int: " + String.valueOf(i) + ")");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyDefine.showLog("LocationUpdater", "onConnectionFailed(ConnectionResult)");

    }

    public void stopLocationUpdate() {
        if(!mGoogleApiClient.isConnected()) {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void stopGoogleApiClient() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        execOnLocationUpdate(location, new Date());
    }

    public interface LocationUpdateListener {
        void onLocationUpdate(Location location, Date date);
    }

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        mLocationUpdateListener = listener;
    }

    public void execOnLocationUpdate(Location location, Date date) {
        if (mLocationUpdateListener == null) {
            return;
        }
        mLocationUpdateListener.onLocationUpdate(location, date);
    }
}
