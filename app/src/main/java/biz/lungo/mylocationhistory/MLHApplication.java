package biz.lungo.mylocationhistory;

import android.app.Application;

import biz.lungo.mylocationhistory.util.Constants;
import biz.lungo.mylocationhistory.util.DBHelper;

public class MLHApplication extends Application {
    private static MLHApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        DBHelper.configureDb(sInstance.getApplicationContext(), Constants.DB_NAME);
    }

    public static MLHApplication getInstance() {
        return sInstance;
    }
}