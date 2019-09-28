package nl.hypothermic.windesmemes.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import nl.hypothermic.windesmemes.android.auth.AuthenticationManager;
import nl.hypothermic.windesmemes.android.ui.EditTextVerifier;

public class LoginActivity extends AppCompatActivity {

    private static void lockFields(boolean lock, EditText... fields) {
        for (EditText field : fields) {
            field.setEnabled(!lock);
        }
    }

    private TextView resultTextView;

    private EditText usernameField, passwordField;
    private volatile EditText[] inputFields;

    private Button interactableSubmit, interactablePolicyWM, interactablePolicyHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO butterknife of canary 11 view binding
        resultTextView       = findViewById(R.id.login_result_text);
        usernameField        = findViewById(R.id.login_field_username);
        passwordField        = findViewById(R.id.login_field_password);
        interactableSubmit   = findViewById(R.id.login_interactable_submit);
        interactablePolicyWM = findViewById(R.id.login_interactable_policy_wm);
        interactablePolicyHT = findViewById(R.id.login_interactable_policy_ht);

        inputFields = new EditText[] {
                usernameField, passwordField
        };
        lockFields(false, inputFields);

        interactableSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EditText field : inputFields) {
                    // TODO set correct min length depending on field
                    if (!EditTextVerifier.verify(LoginActivity.this, field, 4)) {
                        return;
                    }
                }
                lockFields(true, inputFields);
                AuthenticationManager.acquire(LoginActivity.this.getApplicationContext()).userAuthenticate(new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        lockFields(false, inputFields);
                        if (aBoolean != null) {
                            if (aBoolean) {
                                LogWrapper.info(this, "Login successful");
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                resultTextView.setText("Login not successful"); // TODO show error message and @string
                                LogWrapper.error(this, "TODO handle login error"); // TODO
                            }
                        } else {
                            LogWrapper.error(this, "TODO handle error 4"); // TODO
                        }
                    }
                }, usernameField.getText().toString(), passwordField.getText().toString());
            }
        });
    }
}
