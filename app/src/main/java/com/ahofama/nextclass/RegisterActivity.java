package com.ahofama.nextclass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ahofama.nextclass.database.DBHelper;

public class RegisterActivity extends AppCompatActivity {
    EditText etNewUser, etNewEmail, etNewPass;
    Button btnRegister;
    DBHelper dbHelper;
    TextView signInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This links the Java class with your login_page.xml
        setContentView(R.layout.register_activity);

        // Initialize views
        etNewUser = findViewById(R.id.et_new_username);
        etNewEmail = findViewById(R.id.et_new_email);
        etNewPass = findViewById(R.id.et_new_password);
        btnRegister = findViewById(R.id.btn_register);

        dbHelper = new DBHelper(this);

        btnRegister.setOnClickListener(v -> {
            String user = etNewUser.getText().toString().trim();
            String email = etNewEmail.getText().toString().trim();
            String pass = etNewPass.getText().toString().trim();

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email already exists
            if (dbHelper.checkEmailExists(email)) {
                Toast.makeText(this, "Email already registered!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert into database
            if (dbHelper.insertUser(user, email, pass)) {
                Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to LoginPageActivity
            } else {
                Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show();
            }
        });

        signInText = findViewById(R.id.sign_in);
        // Navigate to RegisterActivity
        signInText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginPageActivity.class);
            startActivity(intent);
        });

    }
}
