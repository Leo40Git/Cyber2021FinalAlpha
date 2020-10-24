package adudecalledleo.cyber2021finalalpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PhotoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
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
        } else if (id == R.id.menuLocation) {
            Intent si = new Intent(this, LocationActivity.class);
            startActivity(si);
        } else if (id != R.id.menuPhoto) {
            Toast.makeText(this, "Unknown item \"" + item.getTitle() + "\" (ID: " + id + ")",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }
}