package com.ahofama.nextclass;

import static androidx.databinding.DataBindingUtil.setContentView;
import android.os.Bundle;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPageActivity extends AppCompatActivity{
    TextView signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This links the Java class with your login_page.xml
        setContentView(R.layout.login_page);



        signUpText = findViewById(R.id.sign_up);
        // Navigate to RegisterActivity
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPageActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
