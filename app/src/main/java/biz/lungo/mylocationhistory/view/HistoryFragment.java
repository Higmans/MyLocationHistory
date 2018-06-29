package biz.lungo.mylocationhistory.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import biz.lungo.mylocationhistory.R;
import biz.lungo.mylocationhistory.view.adapter.HistoryAdapter;
import biz.lungo.mylocationhistory.viewModel.HistoryViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;

public class HistoryFragment extends Fragment {

    @BindView(R.id.rv_history)
    RecyclerView rvHistory;

    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.history_fragment, container, false);
        ButterKnife.bind(this, view);
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HistoryViewModel mViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        HistoryAdapter adapter = new HistoryAdapter(mViewModel.getHistoryData(), true);
        rvHistory.setAdapter(adapter);
        Realm.getDefaultInstance().addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(@NonNull Realm realm) {
                checkIfListEmpty();
            }
        });
        checkIfListEmpty();
    }

    private void checkIfListEmpty() {
        if (rvHistory.getAdapter().getItemCount() == 0) {
            tvEmptyView.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmptyView.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
        }
    }
}