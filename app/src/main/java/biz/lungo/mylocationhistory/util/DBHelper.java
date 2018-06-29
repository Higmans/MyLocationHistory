package biz.lungo.mylocationhistory.util;

import android.content.Context;

import biz.lungo.mylocationhistory.model.HistoryItem;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class DBHelper {

    public static void configureDb(Context context, String name) {
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(name)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    public static RealmResults<HistoryItem> getSortedHistoryData() {
        return Realm.getDefaultInstance().where(HistoryItem.class).sort("dateUpdated", Sort.DESCENDING).findAll();
    }

    public static void storeHistoryItem(HistoryItem item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
    }

    public static HistoryItem getLastStoredLocation() {
        final HistoryItem historyItem = getSortedHistoryData().first();
        return historyItem == null ? new HistoryItem() : historyItem;
    }
}
