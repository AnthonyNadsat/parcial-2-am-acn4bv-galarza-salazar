package com.acn4bv.buglog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText etNombreJuego, etDescripcion, etImagenUrl;
    private Spinner spPlataforma, spTipoBug;
    private RadioGroup rgGravedad;
    private Button btnReportar, btnVerLista, btnLogout, btnGestionUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!UserRole.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        etNombreJuego = findViewById(R.id.etNombreJuego);
        etDescripcion = findViewById(R.id.etDescripcion);
        etImagenUrl   = findViewById(R.id.etImagenUrl);
        spPlataforma  = findViewById(R.id.spPlataforma);
        spTipoBug     = findViewById(R.id.spTipoBug);
        rgGravedad    = findViewById(R.id.rgGravedad);
        btnReportar   = findViewById(R.id.btnReportar);
        btnVerLista   = findViewById(R.id.btnVerLista);
        btnLogout     = findViewById(R.id.btnLogout);
        btnGestionUsuarios = findViewById(R.id.btnGestionUsuarios);

        btnLogout.setOnClickListener(v -> logout());

        // Crear documento para usuarios base si no existe
        crearDocumentoUsuarioBaseSiNoExiste();

        // Verificar rol de admin
        verificarRolAdmin();

        ArrayAdapter<CharSequence> adapterPlataforma = ArrayAdapter.createFromResource(
                this,
                R.array.plataformas,
                R.layout.spinner_item
        );
        adapterPlataforma.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spPlataforma.setAdapter(adapterPlataforma);

        ArrayAdapter<CharSequence> adapterTipoBug = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_bug,
                R.layout.spinner_item
        );
        adapterTipoBug.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spTipoBug.setAdapter(adapterTipoBug);

        btnReportar.setOnClickListener(v -> reportar());

        btnVerLista.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ListaBugsActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        verificarRolAdmin();
    }

    private void crearDocumentoUsuarioBaseSiNoExiste() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String email = currentUser.getEmail();
        if (email == null) return;

        // Solo para usuarios base (admin@buglog.com y tester@buglog.com)
        if (!email.equals("admin@buglog.com") && !email.equals("tester@buglog.com")) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        // Crear documento si no existe
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", email);

                        if (email.equals("admin@buglog.com")) {
                            userData.put("displayName", "Administrador Principal");
                            userData.put("role", "admin");
                        } else {
                            userData.put("displayName", "Tester Principal");
                            userData.put("role", "tester");
                        }

                        userData.put("createdAt", System.currentTimeMillis());

                        db.collection("users")
                                .document(currentUser.getUid())
                                .set(userData)
                                .addOnSuccessListener(unused -> {
                                    Log.d(TAG, " Usuario creado exitosamente");
                                    // Recargar el rol después de crear el documento
                                    verificarRolAdmin();
                                })
                                .addOnFailureListener(e ->
                                        Log.e(TAG, " Error al crear usuario", e)
                                );
                    } else {
                        Log.d(TAG, " El usuario ya existe");
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, " Error al verificar usuario", e)
                );
    }

    private void verificarRolAdmin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, " No hay usuario logueado");
            btnGestionUsuarios.setVisibility(View.GONE);
            return;
        }

        String email = currentUser.getEmail();
        Log.d(TAG, " Verificando rol para: " + email);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Log.d(TAG, " Rol del usuario encontrado: " + role);

                        if ("admin".equalsIgnoreCase(role)) {
                            Log.d(TAG, " Usuario es ADMIN - Mostrando botón");
                            btnGestionUsuarios.setVisibility(View.VISIBLE);
                            btnGestionUsuarios.setOnClickListener(v ->
                                    startActivity(new Intent(MainActivity.this, GestionUsuariosActivity.class))
                            );
                        } else {
                            Log.d(TAG, " Usuario es TESTER - Ocultando botón");
                            btnGestionUsuarios.setVisibility(View.GONE);
                        }
                    } else {
                        Log.w(TAG, "️ No se encontró el usuario en Firestore");

                        // Fallback: verificar por email
                        if (email != null && email.equals("admin@buglog.com")) {
                            Log.d(TAG, " Fallback activado - Usuario admin por email");
                            btnGestionUsuarios.setVisibility(View.VISIBLE);
                            btnGestionUsuarios.setOnClickListener(v ->
                                    startActivity(new Intent(MainActivity.this, GestionUsuariosActivity.class))
                            );
                        } else {
                            btnGestionUsuarios.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, " Error al obtener rol del usuario", e);

                    // Fallback: verificar por email
                    if (email != null && email.equals("admin@buglog.com")) {
                        Log.d(TAG, " Fallback activado por error - Usuario admin por email");
                        btnGestionUsuarios.setVisibility(View.VISIBLE);
                        btnGestionUsuarios.setOnClickListener(v ->
                                startActivity(new Intent(MainActivity.this, GestionUsuariosActivity.class))
                        );
                    } else {
                        btnGestionUsuarios.setVisibility(View.GONE);
                    }
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        UserRole.clear();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void reportar() {
        String nombre = etNombreJuego.getText().toString().trim();
        String desc   = etDescripcion.getText().toString().trim();
        String imagenUrl = etImagenUrl.getText().toString().trim();

        int posPlat = spPlataforma.getSelectedItemPosition();
        int posTipo = spTipoBug.getSelectedItemPosition();

        int checked = rgGravedad.getCheckedRadioButtonId();
        String gravedad = null;
        if (checked == R.id.rbBaja)  gravedad = getString(R.string.gravedad_baja);
        if (checked == R.id.rbMedia) gravedad = getString(R.string.gravedad_media);
        if (checked == R.id.rbAlta)  gravedad = getString(R.string.gravedad_alta);

        if (TextUtils.isEmpty(nombre)) { toast(R.string.error_nombre_vacio); return; }
        if (posPlat == 0)              { toast(R.string.error_plataforma);   return; }
        if (posTipo == 0)              { toast(R.string.error_tipo_bug);     return; }
        if (gravedad == null)          { toast(R.string.error_gravedad);     return; }
        if (TextUtils.isEmpty(desc))   { toast(R.string.error_descripcion);  return; }

        String plataforma = spPlataforma.getSelectedItem().toString();
        String tipo       = spTipoBug.getSelectedItem().toString();

        Bug nuevo = new Bug(nombre, plataforma, tipo, gravedad, desc);

        if (!imagenUrl.isEmpty()) {
            nuevo.setImagenUrl(imagenUrl);
        }

        FirestoreRepository.agregarBug(nuevo);

        toast(R.string.bug_reportado);

        etNombreJuego.setText("");
        etDescripcion.setText("");
        etImagenUrl.setText("");
        spPlataforma.setSelection(0);
        spTipoBug.setSelection(0);
        rgGravedad.clearCheck();
    }

    private void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}