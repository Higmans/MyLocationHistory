package biz.lungo.mylocationhistory.viewModel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import biz.lungo.mylocationhistory.MLHApplication;
import biz.lungo.mylocationhistory.util.DBHelper;

public class MapViewModel extends ViewModel implements GoogleApiClient.ConnectionCallbacks,
                                                       GoogleApiClient.OnConnectionFailedListener, OnSuccessListener<Location> {
    private static final long INTERVAL = 10_000;
    private static final long FASTEST_INTERVAL = 5_000;
    private MutableLiveData<LatLng> mLocationData;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mLocationProviderClient;

    public LiveData<LatLng> getLocationData() {
        initLocationData();
        requestLiveLocation();
        return mLocationData;
    }

    private void initLocationData() {
        if (mLocationData == null) {
            mLocationData = new MutableLiveData<>();
            mLocationData.setValue(DBHelper.getLastStoredLocation().getLatLng());
        }
    }

    private void requestLiveLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MLHApplication.getInstance())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    public void stopLocationUpdate() {
        if (mLocationProviderClient != null) {
            mLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdate() {
        if (mLocationProviderClient == null) {
            mLocationProviderClient = LocationServices.getFusedLocationProviderClient(MLHApplication.getInstance());
        }
        mLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        mLocationProviderClient.getLastLocation().addOnSuccessListener(this);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            final Location lastLocation = locationResult.getLastLocation();
            mLocationData.setValue(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        }
    };

    @Override
    public void onSuccess(Location location) {
        initLocationData();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
