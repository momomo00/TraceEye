package momomo00.traceeye;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
    implements MyPermissionManager.WhenGrantedListener
    , LocationUpdater.LocationUpdateListener
{

    private MyPermissionManager mMyPermissionManager;
    private LocationUpdater mLocationUpdater;
    private MyDatabaseHelper mMyDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMain();
    }

    private void initMain() {
        initLocationUpdater();
        initMyPermissionManager();

        mMyDatabaseHelper = new MyDatabaseHelper(this);

        Button showDatabaseButton = (Button)findViewById(R.id.button2);
        showDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyDatabaseHelper.showData();
            }
        });
    }

    private void initLocationUpdater() {
        mLocationUpdater = new LocationUpdater(this);
        mLocationUpdater.setLocationUpdateListener(this);
    }

    private void initMyPermissionManager() {
        mMyPermissionManager = new MyPermissionManager(this);
        mMyPermissionManager.setWhenGrantedListener(this);
        mMyPermissionManager.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationUpdater.stopLocationUpdate();
    }

    @Override
    protected void onStop() {
        mLocationUpdater.stopGoogleApiClient();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mMyPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void whenGranted() {
        requestUserName();
        mLocationUpdater.startLocationUpdate();
    }

    private void requestUserName() {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setTitle("Input user name")
            .setView(editText)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    savePreference(editText.getText().toString());
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    savePreference("Any");
                }
            })
            .show();
    }

    private void savePreference(String userName) {
        SharedPreferences preferences = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserName", userName);
        editor.apply();
    }

    @Override
    public void onLocationUpdate(Location location, Date date) {
        SharedPreferences preferences = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        String userName = preferences.getString("UserName", "Empty");
        mMyDatabaseHelper.insertData(userName, location, date);
    }
}
