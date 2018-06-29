package biz.lungo.mylocationhistory.view.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import biz.lungo.mylocationhistory.MLHApplication;
import biz.lungo.mylocationhistory.R;
import biz.lungo.mylocationhistory.model.HistoryItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class HistoryAdapter extends RealmRecyclerViewAdapter<HistoryItem, HistoryAdapter.ViewHolder> {

    public HistoryAdapter(@Nullable OrderedRealmCollection<HistoryItem> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_latlng)
        TextView tvLatLng;

        @BindView(R.id.tv_time)
        TextView tvTime;

        @BindView(R.id.tv_accuracy)
        TextView tvAccuracy;

        @BindView(R.id.tv_provider)
        TextView tvProvider;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(HistoryItem item) {
            final Resources resources = MLHApplication.getInstance().getResources();
            PrettyTime prettyTime = new PrettyTime();
            String latLng = resources.getString(R.string.lat_lng_format,
                    item.getLatLng().latitude,
                    item.getLatLng().longitude);
            String time = resources.getString(R.string.time_format, prettyTime.format((item.dateUpdated)));
            String accuracy = resources.getString(R.string.accuracy_format, item.accuracy);
            String provider = resources.getString(R.string.provider_format, item.provider);

            tvLatLng.setText(latLng);
            tvTime.setText(time);
            tvAccuracy.setText(accuracy);
            tvProvider.setText(provider);
        }
    }
}
