package adudecalledleo.cyber2021finalalpha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class PhotoActivity extends AppCompatActivity {
    private static final int OPEN_FILE_REQUEST_CODE = 1;

    private StorageReference storageRef;
    private StorageReference imageRef;

    private ImageView imageView;
    private TextView tvInstruct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storageRef = FirebaseStorage.getInstance().getReference();
        imageRef = storageRef.child("image.jpeg");

        setContentView(R.layout.activity_photo);
        imageView = findViewById(R.id.imageView);
        tvInstruct = findViewById(R.id.tvInstruct);
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

    public void onClick_btnUpload(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || requestCode != OPEN_FILE_REQUEST_CODE || resultCode != RESULT_OK)
            return;
        Uri imageUri = data.getData();
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        runOnUiThread(() ->
                                Toast.makeText(this,
                                        "Successfully uploaded image! Try downloading it!",
                                        Toast.LENGTH_LONG).show()))
                .addOnFailureListener(e -> {
                    Log.e("PhotoActivity", "Failed to upload image", e);
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Failed to upload image: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
                });
    }

    public void onClick_btnDownload(View view) {
        File destFile;
        try {
            destFile = File.createTempFile("image", "jpeg");
        } catch (IOException e) {
            Log.e("PhotoActivity", "Couldn't create temp file", e);
            Toast.makeText(this, "Couldn't create temp file: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        imageRef.getFile(destFile)
                .addOnSuccessListener(taskSnapshot -> {
                    final Bitmap bitmap = BitmapFactory.decodeFile(destFile.getPath());
                    if (bitmap == null) {
                        Log.e("PhotoActivity", "Failed to decode downloaded image");
                        runOnUiThread(() ->
                                Toast.makeText(this,
                                        "Failed to decode downloaded image",
                                        Toast.LENGTH_LONG).show());
                    } else {
                        runOnUiThread(() -> {
                            tvInstruct.setVisibility(View.GONE);
                            imageView.setImageBitmap(bitmap);
                            Toast.makeText(this,
                                    "Successfully downloaded image!",
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PhotoActivity", "Failed to download image", e);
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Failed to download image: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
                });
    }
}