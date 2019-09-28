package nl.hypothermic.windesmemes.android.ui;

import android.content.Context;
import android.widget.EditText;

import androidx.annotation.NonNull;

import nl.hypothermic.windesmemes.android.R;

public class EditTextVerifier {

    public static boolean verify(@NonNull Context activityContext, @NonNull EditText editText, @NonNull int minLength) {
        boolean success = editText.getText() != null && editText.getText().toString().length() > minLength;
        if (!success) {
            editText.setError(activityContext.getString(R.string.login_error_invalid_input));
        }
        return success;
    }

    private EditTextVerifier() {

    }
}
