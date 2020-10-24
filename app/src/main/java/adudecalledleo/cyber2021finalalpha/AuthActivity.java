package adudecalledleo.cyber2021finalalpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private EditText etPhone;
    private LinearLayout layEnterCode;
    private EditText etVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

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
                30L, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneCred(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(AuthActivity.this, "Failed to verify phone number: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void obClick_btnVerify(View view) {
        signInWithPhoneCred(PhoneAuthProvider.getCredential(verificationId, etVerifyCode.getText().toString()));
    }

    private void signInWithPhoneCred(PhoneAuthCredential phoneCred) {
        firebaseAuth.signInWithCredential(phoneCred)
                .addOnCompleteListener(task -> {
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
                      if (task.getException() != null)
                          message = task.getException().getMessage();
                      Toast.makeText(AuthActivity.this, "Failed to sign in: " + message,
                              Toast.LENGTH_LONG).show();
                  }
                });
    }
}