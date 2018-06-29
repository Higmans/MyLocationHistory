package biz.lungo.mylocationhistory.background;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.work.Worker;
import biz.lungo.mylocationhistory.MLHApplication;
import biz.lungo.mylocationhistory.model.HistoryItem;
import biz.lungo.mylocationhistory.util.DBHelper;

public class LocationUpdateWorker extends Worker implements OnSuccessListener<Location> {

    @NonNull
    @Override
    public Result doWork() {
        Result result;
        FusedLocationProviderClient mLocationProviderClient = LocationServices.getFusedLocationProviderClient(MLHApplication.getInstance());
        if (ActivityCompat.checkSelfPermission(MLHApplication.getInstance(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationProviderClient.getLastLocation().addOnSuccessListener(this);
            result = Result.SUCCESS;
        } else {
            result = Result.FAILURE;
        }
        return result;
    }

    @Override
    public void onSuccess(Location location) {
        HistoryItem historyItem = new HistoryItem(location);
        DBHelper.storeHistoryItem(historyItem);
    }
}