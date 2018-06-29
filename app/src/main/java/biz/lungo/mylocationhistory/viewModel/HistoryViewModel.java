package biz.lungo.mylocationhistory.viewModel;

import android.arch.lifecycle.ViewModel;

import biz.lungo.mylocationhistory.model.HistoryItem;
import biz.lungo.mylocationhistory.util.DBHelper;
import io.realm.RealmResults;

public class HistoryViewModel extends ViewModel {
    private RealmResults<HistoryItem> mHistoryData;

    public RealmResults<HistoryItem> getHistoryData() {
        initHistoryData();
        return mHistoryData;
    }

    private void initHistoryData() {
        mHistoryData = DBHelper.getSortedHistoryData();
    }
}
