package adudecalledleo.cyber2021finalalpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class LocationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
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
}