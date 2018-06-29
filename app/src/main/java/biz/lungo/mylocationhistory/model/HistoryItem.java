package biz.lungo.mylocationhistory.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HistoryItem extends RealmObject {

    @PrimaryKey
    public long timestamp;
    public float accuracy;
    public String provider;
    public Date dateUpdated;
    public double lat;
    public double lng;

    public HistoryItem() {}

    public HistoryItem(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        this.dateUpdated = new Date(location.getTime());
        this.timestamp = location.getTime();
        this.accuracy = location.getAccuracy();
        this.provider = location.getProvider();
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }
}
