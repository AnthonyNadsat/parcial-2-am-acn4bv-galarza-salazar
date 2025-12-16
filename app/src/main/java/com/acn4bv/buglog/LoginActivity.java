package com.acn4bv.buglog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etUser, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            cargarRolYAbrirMain();
            return;
        }

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String user = etUser.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(user)) {
            etUser.setError("Ingrese usuario");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Ingrese contraseña");
            return;
        }

        // Completa admin o tester en un email
        String email = user.contains("@") ? user : user + "@buglog.com";

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(r -> {
                    cargarRolYAbrirMain();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_LONG).show();
                });
    }

    private void cargarRolYAbrirMain() {
        UserRole.loadUserRole(() -> {
            progressBar.setVisibility(View.GONE);
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}