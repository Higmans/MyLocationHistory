package biz.lungo.mylocationhistory.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import biz.lungo.mylocationhistory.BuildConfig;
import biz.lungo.mylocationhistory.R;
import biz.lungo.mylocationhistory.background.LocationUpdateWorker;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 42;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    @BindView(R.id.ll_message_permission)
    View permissionWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        bottomNavigation.setOnNavigationItemSelectedListener(menuListener);
        checkPermissions();
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.menu_map);
        }
    }

    public void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                showPermissionWarning(false);
                bottomNavigation.setSelectedItemId(R.id.menu_map);
                startWorker();
            } else {
                tryRequestPermissions();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.btn_accept_permission)
    void tryRequestPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
            } else {
                showPermissionWarning(true);
                showPermissionErrorDialog();
            }
        }
    }

    private void startWorker() {
        Constraints constraints = new Constraints.Builder().build();
        PeriodicWorkRequest locationWork = new PeriodicWorkRequest.Builder(LocationUpdateWorker.class,
                15, TimeUnit.MINUTES)
                .addTag(BuildConfig.APPLICATION_ID)
                .setConstraints(constraints).build();
        final WorkManager workManager = WorkManager.getInstance();
        if (workManager != null)
            workManager.enqueueUniquePeriodicWork(BuildConfig.APPLICATION_ID, ExistingPeriodicWorkPolicy.KEEP, locationWork);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showPermissionErrorDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle(R.string.title_error)
                .setMessage(R.string.permissions_error)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.label_try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tryRequestPermissions();
                        dialogInterface.dismiss();
                    }
                });
        dialogBuilder.create().show();
    }

    private void showPermissionWarning(boolean show) {
        if (show) {
            permissionWarning.setVisibility(View.VISIBLE);
        } else  {
            permissionWarning.setVisibility(View.GONE);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener menuListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_map:
                    changeFragment(MapFragment.newInstance());
                    break;

                case R.id.menu_history:
                    changeFragment(HistoryFragment.newInstance());
                    break;
            }
            return true;
        }
    };

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow();
    }
}
