package com.acn4bv.buglog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GestionUsuariosActivity extends AppCompatActivity {

    private LinearLayout containerUsuarios;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!UserRole.isAdmin()) {
            Toast.makeText(this, "Acceso denegado: solo administradores", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_gestion_usuarios);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        containerUsuarios = findViewById(R.id.containerUsuarios);
        Button btnVolver = findViewById(R.id.btnVolverGestionUsuarios);
        Button btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario);

        btnVolver.setOnClickListener(v -> finish());
        btnAgregarUsuario.setOnClickListener(v -> mostrarDialogoCrearUsuario());

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        containerUsuarios.removeAllViews();

        db.collection("users")
                .get()
                .addOnSuccessListener(result -> {
                    List<User> usuarios = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : result) {
                        User user = doc.toObject(User.class);
                        user.setId(doc.getId());
                        usuarios.add(user);
                    }

                    if (usuarios.isEmpty()) {
                        TextView empty = new TextView(this);
                        empty.setText("No hay usuarios registrados");
                        empty.setTextColor(getColor(R.color.text_secondary));
                        empty.setTextSize(16f);
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, 48, 0, 48);
                        containerUsuarios.addView(empty);
                        return;
                    }

                    for (User user : usuarios) {
                        containerUsuarios.addView(crearCardUsuario(user));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
                );
    }

    private CardView crearCardUsuario(User user) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.topMargin = (int) (16 * getResources().getDisplayMetrics().density);
        cardView.setLayoutParams(cardParams);
        cardView.setCardBackgroundColor(getColor(R.color.card_background));
        cardView.setRadius(16f);
        cardView.setCardElevation(8f);

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        contentLayout.setPadding(24, 24, 24, 24);

        // Badge de rol
        TextView badge = crearBadgeRol(user.getRole());
        contentLayout.addView(badge);

        // Nombre y email
        TextView tvNombre = new TextView(this);
        tvNombre.setText(user.getDisplayName());
        tvNombre.setTextSize(18f);
        tvNombre.setTextColor(getColor(R.color.text_primary));
        tvNombre.setTypeface(null, android.graphics.Typeface.BOLD);
        tvNombre.setPadding(0, 12, 0, 0);
        contentLayout.addView(tvNombre);

        TextView tvEmail = new TextView(this);
        tvEmail.setText(user.getEmail());
        tvEmail.setTextColor(getColor(R.color.text_secondary));
        tvEmail.setTextSize(14f);
        tvEmail.setPadding(0, 4, 0, 0);
        contentLayout.addView(tvEmail);

        // Fecha de creación
        TextView tvFecha = new TextView(this);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvFecha.setText("Creado: " + sdf.format(new Date(user.getCreatedAt())));
        tvFecha.setTextColor(getColor(R.color.text_secondary));
        tvFecha.setTextSize(12f);
        tvFecha.setPadding(0, 8, 0, 0);
        contentLayout.addView(tvFecha);

        // Botones de acción
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 16, 0, 0);

        Button btnEditar = crearBotonEditar();
        btnEditar.setOnClickListener(v -> mostrarDialogoEditarUsuario(user));

        Button btnEliminar = crearBotonEliminar();
        btnEliminar.setOnClickListener(v -> confirmarEliminarUsuario(user));

        row.addView(btnEliminar);
        row.addView(btnEditar);
        contentLayout.addView(row);

        cardView.addView(contentLayout);
        return cardView;
    }

    private TextView crearBadgeRol(String role) {
        TextView badge = new TextView(this);
        badge.setPadding(16, 6, 16, 6);
        badge.setTextSize(10);
        badge.setTextColor(Color.WHITE);
        badge.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        badge.setLayoutParams(params);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(12);

        if ("admin".equalsIgnoreCase(role)) {
            badge.setText("ADMINISTRADOR");
            bg.setColor(Color.parseColor("#8B5CF6"));
        } else {
            badge.setText("TESTER");
            bg.setColor(Color.parseColor("#10B981"));
        }

        badge.setBackground(bg);
        return badge;
    }

    private Button crearBotonEditar() {
        Button btn = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                (int) (48 * getResources().getDisplayMetrics().density),
                1f
        );
        params.leftMargin = (int) (8 * getResources().getDisplayMetrics().density);
        btn.setLayoutParams(params);
        btn.setText("Editar");
        btn.setAllCaps(false);
        btn.setTextSize(14f);
        btn.setTextColor(Color.WHITE);
        btn.setTypeface(null, android.graphics.Typeface.BOLD);

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.parseColor("#EC4899"),
                        Color.parseColor("#8B5CF6")
                }
        );
        bg.setCornerRadius(10 * getResources().getDisplayMetrics().density);
        btn.setBackground(bg);
        return btn;
    }

    private Button crearBotonEliminar() {
        Button btn = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                (int) (48 * getResources().getDisplayMetrics().density),
                1f
        );
        params.rightMargin = (int) (8 * getResources().getDisplayMetrics().density);
        btn.setLayoutParams(params);
        btn.setText("Eliminar");
        btn.setAllCaps(false);
        btn.setTextSize(14f);
        btn.setTextColor(Color.WHITE);
        btn.setTypeface(null, android.graphics.Typeface.BOLD);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(10 * getResources().getDisplayMetrics().density);
        bg.setColor(Color.parseColor("#EF4444"));
        btn.setBackground(bg);
        return btn;
    }

    private void mostrarDialogoCrearUsuario() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.modal_crear_usuario, null);
        dialog.setContentView(view);

        EditText etNombre = view.findViewById(R.id.etCrearNombre);
        EditText etEmail = view.findViewById(R.id.etCrearEmail);
        EditText etPassword = view.findViewById(R.id.etCrearPassword);
        RadioGroup rgRol = view.findViewById(R.id.rgCrearRol);
        Button btnCrear = view.findViewById(R.id.btnCrearUsuario);
        Button btnCancelar = view.findViewById(R.id.btnCancelarCrear);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnCrear.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            int selectedId = rgRol.getCheckedRadioButtonId();
            String rol = selectedId == R.id.rbCrearAdmin ? "admin" : "tester";

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            crearUsuario(nombre, email, password, rol, dialog);
        });

        dialog.show();
    }

    private void crearUsuario(String nombre, String email, String password, String rol, BottomSheetDialog dialog) {
        // NUEVO: Primero verificar si el email ya existe en Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Error: El email ya está en uso. Si fue eliminado anteriormente, debe usar un email diferente.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Si no existe en Firestore, proceder a crear en Auth
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                String uid = authResult.getUser().getUid();

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", email);
                                userData.put("displayName", nombre);
                                userData.put("role", rol);
                                userData.put("createdAt", System.currentTimeMillis());

                                db.collection("users")
                                        .document(uid)
                                        .set(userData)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                                            cargarUsuarios();
                                            dialog.dismiss();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Error al guardar datos del usuario", Toast.LENGTH_SHORT).show()
                                        );
                            })
                            .addOnFailureListener(e -> {
                                String mensaje = "Error al crear usuario";
                                if (e.getMessage() != null && e.getMessage().contains("email address is already in use")) {
                                    mensaje = "El email ya está registrado en el sistema. Use un email diferente.";
                                }
                                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al verificar email", Toast.LENGTH_SHORT).show()
                );
    }

    private void mostrarDialogoEditarUsuario(User user) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.modal_editar_usuario, null);
        dialog.setContentView(view);

        EditText etNombre = view.findViewById(R.id.etEditarNombre);
        RadioGroup rgRol = view.findViewById(R.id.rgEditarRol);
        Button btnGuardar = view.findViewById(R.id.btnGuardarUsuario);
        Button btnCancelar = view.findViewById(R.id.btnCancelarEditar);

        etNombre.setText(user.getDisplayName());

        if ("admin".equalsIgnoreCase(user.getRole())) {
            ((RadioButton) view.findViewById(R.id.rbEditarAdmin)).setChecked(true);
        } else {
            ((RadioButton) view.findViewById(R.id.rbEditarTester)).setChecked(true);
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            int selectedId = rgRol.getCheckedRadioButtonId();
            String rol = selectedId == R.id.rbEditarAdmin ? "admin" : "tester";

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users")
                    .document(user.getId())
                    .update("displayName", nombre, "role", rol)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                        cargarUsuarios();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
                    );
        });

        dialog.show();
    }

    private void confirmarEliminarUsuario(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de que deseas eliminar a " + user.getDisplayName() + "?\n\nNOTA: El email no podrá ser reutilizado.")
                .setPositiveButton("Eliminar", (dialogInterface, i) -> eliminarUsuario(user))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(User user) {
        db.collection("users")
                .document(user.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Usuario eliminado de Firestore", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
                );
    }
}