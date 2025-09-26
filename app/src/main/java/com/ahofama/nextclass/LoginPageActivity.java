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

import com.ahofama.nextclass.database.DBHelper;

public class LoginPageActivity extends AppCompatActivity{
    TextView signUpText;
    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvSignUp;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This links the Java class with your login_page.xml
        setContentView(R.layout.login_page);


        // Initialize views
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.sign_up);

        dbHelper = new DBHelper(this);

        // Login button
        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (dbHelper.checkUser(user, pass)) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        signUpText = findViewById(R.id.sign_up);
        // Navigate to RegisterActivity
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPageActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
