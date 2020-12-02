package adudecalledleo.cyber2021finalalpha;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import java.util.Date;

public class LocationActivity extends AppCompatActivity {
    private static final int LOCATION_PERMS_REQUEST_CODE = 1;

    private TextView tvLatitude, tvLongitude, tvTimestamp;
    private Button btnTrack;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        btnTrack = findViewById(R.id.btnTrack);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuAuth) {
            Intent si = new Intent(this, AuthActivity.class);
            startActivity(si);
        } else if (id == R.id.menuPhoto) {
            Intent si = new Intent(this, PhotoActivity.class);
            startActivity(si);
        } else if (id != R.id.menuLocation) {
            Toast.makeText(this, "Unknown item \"" + item.getTitle() + "\" (ID: " + id + ")",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public void onClick_btnTrack(View view) {
        if (locationManager == null) {
            // check for prerequisites
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION)) {
                Toast.makeText(this, "Your device does not support location.", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_PERMS_REQUEST_CODE);
                return;
            }
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        if (locationListener == null) {
            if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
                Toast.makeText(this,
                        "Please enable location.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setCostAllowed(false);
            String locationProvider = locationManager.getBestProvider(criteria, true);
            if (locationProvider == null) {
                Toast.makeText(this,
                        "Couldn't find location provider.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    runOnUiThread(() -> {
                        tvLatitude.setText("Latitude: " + location.getLatitude());
                        tvLongitude.setText("Longitude: " + location.getLongitude());
                        tvTimestamp.setText("Timestamp: " + new Date(location.getTime()));
                    });
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

                @Override
                public void onProviderEnabled(String provider) { }

                @Override
                public void onProviderDisabled(String provider) {
                    if (locationProvider.equals(provider)) {
                        runOnUiThread(() -> {
                            Toast.makeText(LocationActivity.this,
                                    "Location provider was disabled.",
                                    Toast.LENGTH_LONG).show();
                            onClick_btnTrack(btnTrack);
                        });
                    }
                }
            };
            locationManager.requestLocationUpdates(locationProvider,
                    2000,
                    0.1f,
                    locationListener);
            btnTrack.setText("Stop Tracking");
        } else {
            locationManager.removeUpdates(locationListener);
            locationListener = null;
            btnTrack.setText("Start Tracking");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationListener != null)
            locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMS_REQUEST_CODE)
            return;
        if (grantResults.length < 2
                || grantResults[0] != PackageManager.PERMISSION_GRANTED
                || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            runOnUiThread(() -> Toast.makeText(this,
                    "This application does not have permission to access your location.",
                    Toast.LENGTH_LONG).show());
        } else {
            runOnUiThread(() -> onClick_btnTrack(btnTrack));
        }
    }
}