package biz.lungo.mylocationhistory.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import biz.lungo.mylocationhistory.R;
import biz.lungo.mylocationhistory.util.Constants;
import biz.lungo.mylocationhistory.viewModel.MapViewModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mViewModel;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        final FragmentActivity activity = getActivity();
        if (isAdded() && activity != null) {
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.stopLocationUpdate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.startLocationUpdate();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        final LiveData<LatLng> locationData = mViewModel.getLocationData();
        updateMarker(googleMap, locationData.getValue(), false);
        locationData.observeForever(new Observer<LatLng>() {
            @Override
            public void onChanged(@Nullable LatLng latLng) {
                if (latLng == null)
                    return;
                updateMarker(googleMap, locationData.getValue(), true);
            }
        });
    }

    private void updateMarker(GoogleMap googleMap, LatLng latLng, boolean withAnimation) {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.label_marker)));
        if (withAnimation) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM));
        }
    }
}