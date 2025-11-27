package com.acn4bv.buglog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText etNombreJuego, etDescripcion;
    private Spinner spPlataforma, spTipoBug;
    private RadioGroup rgGravedad;
    private TextView btnLogout;
    private View btnReportar, btnVerLista;

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
        spPlataforma  = findViewById(R.id.spPlataforma);
        spTipoBug     = findViewById(R.id.spTipoBug);
        rgGravedad    = findViewById(R.id.rgGravedad);
        btnReportar   = findViewById(R.id.btnReportar);
        btnVerLista   = findViewById(R.id.btnVerLista);

        btnLogout = findViewById(R.id.btnLogout);   // <-- TextView ahora
        btnLogout.setOnClickListener(v -> logout());

        // Spinner Plataforma
        ArrayAdapter<CharSequence> adapterPlataforma = ArrayAdapter.createFromResource(
                this,
                R.array.plataformas,
                R.layout.spinner_item
        );
        adapterPlataforma.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spPlataforma.setAdapter(adapterPlataforma);

        // Spinner Tipo
        ArrayAdapter<CharSequence> adapterTipoBug = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_bug,
                R.layout.spinner_item
        );
        adapterTipoBug.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spTipoBug.setAdapter(adapterTipoBug);

        // Botón reportar
        btnReportar.setOnClickListener(v -> reportar());

        // Botón ver lista
        btnVerLista.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ListaBugsActivity.class))
        );
    }

    // Logout
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

        int posPlat = spPlataforma.getSelectedItemPosition();
        int posTipo = spTipoBug.getSelectedItemPosition();

        int checked = rgGravedad.getCheckedRadioButtonId();
        String gravedad = null;

        if (checked == R.id.rbBaja)  gravedad = getString(R.string.gravedad_baja);
        if (checked == R.id.rbMedia) gravedad = getString(R.string.gravedad_media);
        if (checked == R.id.rbAlta)  gravedad = getString(R.string.gravedad_alta);

        // Validaciones
        if (TextUtils.isEmpty(nombre)) { toast(R.string.error_nombre_vacio); return; }
        if (posPlat == 0)              { toast(R.string.error_plataforma);   return; }
        if (posTipo == 0)              { toast(R.string.error_tipo_bug);     return; }
        if (gravedad == null)          { toast(R.string.error_gravedad);     return; }
        if (TextUtils.isEmpty(desc))   { toast(R.string.error_descripcion);  return; }

        // Crear y guardar Bug
        Bug nuevo = new Bug(
                nombre,
                spPlataforma.getSelectedItem().toString(),
                spTipoBug.getSelectedItem().toString(),
                gravedad,
                desc
        );

        FirestoreRepository.agregarBug(nuevo);

        toast(R.string.bug_reportado);

        // Limpieza
        etNombreJuego.setText("");
        etDescripcion.setText("");
        spPlataforma.setSelection(0);
        spTipoBug.setSelection(0);
        rgGravedad.clearCheck();
    }

    private void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}