package com.ahofama.nextclass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    TextView signInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This links the Java class with your login_page.xml
        setContentView(R.layout.register_activity);

        signInText = findViewById(R.id.sign_in);
        // Navigate to RegisterActivity
        signInText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginPageActivity.class);
            startActivity(intent);
        });

    }
}
