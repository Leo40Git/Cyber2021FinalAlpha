package adudecalledleo.cyber2021finalalpha;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private EditText etPhone;
    private LinearLayout layEnterCode;
    private EditText etVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        verificationId = null;
        resendToken = null;
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String verificationId,
                    @NonNull PhoneAuthProvider.ForceResendingToken resendToken) {
                AuthActivity.this.verificationId = verificationId;
                AuthActivity.this.resendToken = resendToken;
                runOnUiThread(() -> {
                    etVerifyCode.setText("");
                    layEnterCode.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onVerificationCompleted(
                    @NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneCred(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(AuthActivity.this,
                        "Failed to verify phone number: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };

        setContentView(R.layout.activity_auth);
        etPhone = findViewById(R.id.etPhone);
        layEnterCode = findViewById(R.id.layEnterCode);
        etVerifyCode = findViewById(R.id.etVerifyCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuPhoto) {
            Intent si = new Intent(this, PhotoActivity.class);
            startActivity(si);
        } else if (id == R.id.menuLocation) {
            Intent si = new Intent(this, LocationActivity.class);
            startActivity(si);
        } else if (id != R.id.menuAuth) {
            Toast.makeText(this, "Unknown item \"" + item.getTitle() + "\" (ID: " + id + ")",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public void onClick_btnAuth(View btnAuth) {
        PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(etPhone.getText().toString(),
                60L, TimeUnit.SECONDS, this, callbacks);
    }

    public void obClick_btnVerify(View view) {
        if (verificationId == null)
            return;
        signInWithPhoneCred(
                PhoneAuthProvider.getCredential(verificationId, etVerifyCode.getText().toString()));
    }

    public void onClick_btnResend(View view) {
        if (resendToken == null)
            return;
        PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(etPhone.getText().toString(),
                60L, TimeUnit.SECONDS, this, callbacks, resendToken);
    }

    private void signInWithPhoneCred(PhoneAuthCredential phoneCred) {
        firebaseAuth.signInWithCredential(phoneCred)
                .addOnCompleteListener(task -> {
                    verificationId = null;
                    resendToken = null;
                    if (task.isSuccessful() && task.getResult() != null) {
                        runOnUiThread(() -> {
                            etVerifyCode.setText("");
                            layEnterCode.setVisibility(View.GONE);
                        });
                        String username = "<unnamed>";
                        if (task.getResult() != null && task.getResult().getUser() != null)
                            username = task.getResult().getUser().getDisplayName();
                        Toast.makeText(AuthActivity.this, "Successfully signed in as: " + username,
                                Toast.LENGTH_LONG).show();
                    } else {
                        String message = "unknown error";
                        if (task.getException() != null) {
                            Exception e = task.getException();
                            Log.e("AuthActivity", "Firebase sign in failed with exception", e);
                            message = e.getMessage();
                        } else
                            Log.e("AuthActivity", "Firebase sign in failed without exception");
                        Toast.makeText(AuthActivity.this, "Failed to sign in: " + message,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}