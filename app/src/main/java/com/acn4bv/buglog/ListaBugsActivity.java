package com.acn4bv.buglog;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaBugsActivity extends AppCompatActivity {

    private LinearLayout containerBugs;
    private String filtroActual = "TODOS";

    private FirebaseFirestore db;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_bugs);

        containerBugs = findViewById(R.id.containerBugs);

        db = FirebaseFirestore.getInstance();

        // Cargar el rol del usuario
        UserRole.loadUserRole(() -> {
            isAdmin = UserRole.isAdmin();
            cargarBugs();
        });

        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        agregarBotonFlotanteFiltro();
    }

    private void agregarBotonFlotanteFiltro() {
        ViewGroup root = findViewById(android.R.id.content);
        Resources res   = getResources();

        int sizePx = res.getDimensionPixelSize(R.dimen.button_height);
        int padPx  = res.getDimensionPixelSize(R.dimen.padding_small);
        int mPx    = res.getDimensionPixelSize(R.dimen.margin_medium);

        ImageButton fab = new ImageButton(this);
        fab.setLayoutParams(new ViewGroup.LayoutParams(sizePx, sizePx));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#8B5CF6"));
        bg.setCornerRadius(sizePx / 2f);
        fab.setBackground(bg);
        fab.setPadding(padPx, padPx, padPx, padPx);

        fab.setImageResource(android.R.drawable.ic_menu_sort_by_size);
        fab.setColorFilter(Color.WHITE);

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        wrapper.setGravity(Gravity.BOTTOM | Gravity.END);

        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(sizePx, sizePx);
        lp.rightMargin  = mPx;
        lp.bottomMargin = mPx;
        fab.setLayoutParams(lp);

        fab.setOnClickListener(v -> mostrarBottomSheetFiltros());

        wrapper.addView(fab);
        root.addView(wrapper);
    }

    private void mostrarBottomSheetFiltros() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        layout.setBackgroundColor(Color.parseColor("#1F2937"));

        String[] filtros = {"Todos", "Baja", "Media", "Alta"};

        for (String filtro : filtros) {
            TextView opcion = new TextView(this);
            opcion.setText(filtro);
            opcion.setTextSize(16f);
            opcion.setTextColor(Color.WHITE);
            opcion.setPadding(32, 24, 32, 24);
            opcion.setTypeface(null, android.graphics.Typeface.BOLD);

            if (filtroActual.equalsIgnoreCase(filtro)) {
                GradientDrawable selectedBg = new GradientDrawable();
                selectedBg.setCornerRadius(20);
                selectedBg.setColor(Color.parseColor("#EC4899"));
                opcion.setBackground(selectedBg);
            }

            opcion.setOnClickListener(v -> {
                filtroActual = filtro.toUpperCase();
                cargarBugs();
                dialog.dismiss();
            });

            layout.addView(opcion);
        }

        dialog.setContentView(layout);
        dialog.show();
    }

    private void cargarBugs() {
        containerBugs.removeAllViews();

        db.collection("bugs")
                .get()
                .addOnSuccessListener(result -> {

                    List<Bug> todos = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : result) {

                        String id          = doc.getId();
                        String nombreJuego = doc.getString("nombreJuego");
                        String plataforma  = doc.getString("plataforma");
                        String tipo        = doc.getString("tipo");
                        String gravedad    = doc.getString("gravedad");
                        String descripcion = doc.getString("descripcion");
                        String imagenUrl   = doc.getString("imagenUrl");

                        Bug bug = new Bug(nombreJuego, plataforma, tipo, gravedad, descripcion, imagenUrl);
                        bug.setId(id);

                        todos.add(bug);
                    }

                    List<Bug> bugsFiltrados = filtrar(todos, filtroActual);

                    ordenarPorPrioridad(bugsFiltrados);

                    if (bugsFiltrados.isEmpty()) {
                        TextView empty = new TextView(this);
                        empty.setText(getString(R.string.no_bugs));
                        empty.setTextColor(getColor(R.color.text_secondary));
                        empty.setTextSize(16f);
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, 48, 0, 48);
                        containerBugs.addView(empty);
                        return;
                    }

                    Resources res = getResources();
                    int marginTop = res.getDimensionPixelSize(R.dimen.margin_medium);

                    for (Bug bug : bugsFiltrados) {
                        containerBugs.addView(crearCard(bug, marginTop));
                    }
                });
    }

    private List<Bug> filtrar(List<Bug> fuente, String filtro) {
        if ("TODOS".equals(filtro)) return fuente;

        List<Bug> out = new ArrayList<>();
        String target = filtro == null ? "" : filtro.toUpperCase();
        for (Bug bug : fuente) {
            String g = bug.getGravedad() == null ? "" : bug.getGravedad().toUpperCase();
            if (g.contains(target)) out.add(bug);
        }
        return out;
    }

    private CardView crearCard(Bug bug, int marginTop) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.topMargin = marginTop;
        cardView.setLayoutParams(cardParams);
        cardView.setCardBackgroundColor(getColor(R.color.card_background));
        cardView.setRadius(24f);
        cardView.setCardElevation(8f);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        if (bug.getImagenUrl() != null && !bug.getImagenUrl().trim().isEmpty()) {
            ImageView cover = new ImageView(this);
            int widthPx = (int) (120 * getResources().getDisplayMetrics().density);
            int heightPx = (int) (180 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(widthPx, heightPx);
            cover.setLayoutParams(imgLp);
            cover.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Glide.with(this)
                    .load(bug.getImagenUrl())
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .placeholder(android.R.color.darker_gray)
                    .into(cover);

            mainLayout.addView(cover);
        }

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        contentLayout.setLayoutParams(contentParams);
        contentLayout.setPadding(20, 20, 20, 20);

        TextView badge = crearBadgeGravedad(bug.getGravedad());
        if (badge != null) contentLayout.addView(badge);

        TextView titulo = new TextView(this);
        titulo.setText(bug.getNombreJuego() + " • " + bug.getPlataforma() + " • " + bug.getTipo());
        titulo.setTextSize(14f);
        titulo.setTextColor(getColor(R.color.text_primary));
        titulo.setTypeface(null, android.graphics.Typeface.BOLD);
        titulo.setPadding(0, 12, 0, 0);

        TextView cuerpo = new TextView(this);
        cuerpo.setText(bug.getDescripcion());
        cuerpo.setTextColor(getColor(R.color.text_secondary));
        cuerpo.setTextSize(13f);
        cuerpo.setPadding(0, 8, 0, 0);
        cuerpo.setMaxLines(3);
        cuerpo.setEllipsize(android.text.TextUtils.TruncateAt.END);

        contentLayout.addView(titulo);
        contentLayout.addView(cuerpo);

        // Botones admin - AHORA USA isAdmin en lugar de UserRole.isAdmin()
        if (isAdmin) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 16, 0, 0);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            row.setLayoutParams(rowParams);

            // Botón ELIMINAR (rojo, izquierda)
            Button btnBorrar = new Button(this);
            LinearLayout.LayoutParams btnBorrarParams = new LinearLayout.LayoutParams(
                    0,
                    (int) (48 * getResources().getDisplayMetrics().density),
                    1f
            );
            btnBorrarParams.rightMargin = (int) (8 * getResources().getDisplayMetrics().density);
            btnBorrar.setLayoutParams(btnBorrarParams);

            btnBorrar.setText("Eliminar");
            btnBorrar.setAllCaps(false);
            btnBorrar.setTextSize(14f);
            btnBorrar.setTextColor(Color.WHITE);
            btnBorrar.setTypeface(null, android.graphics.Typeface.BOLD);

            GradientDrawable bgBorrar = new GradientDrawable();
            bgBorrar.setCornerRadius(10 * getResources().getDisplayMetrics().density);
            bgBorrar.setColor(Color.parseColor("#EF4444"));
            btnBorrar.setBackground(bgBorrar);

            btnBorrar.setOnClickListener(v -> borrarBug(bug));

            // Botón EDITAR (gradiente rosa-púrpura, derecha)
            Button btnEditar = new Button(this);
            LinearLayout.LayoutParams btnEditarParams = new LinearLayout.LayoutParams(
                    0,
                    (int) (48 * getResources().getDisplayMetrics().density),
                    1f
            );
            btnEditarParams.leftMargin = (int) (8 * getResources().getDisplayMetrics().density);
            btnEditar.setLayoutParams(btnEditarParams);

            btnEditar.setText("Editar");
            btnEditar.setAllCaps(false);
            btnEditar.setTextSize(14f);
            btnEditar.setTextColor(Color.WHITE);
            btnEditar.setTypeface(null, android.graphics.Typeface.BOLD);

            GradientDrawable bgEditar = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{
                            Color.parseColor("#EC4899"),
                            Color.parseColor("#8B5CF6")
                    }
            );
            bgEditar.setCornerRadius(10 * getResources().getDisplayMetrics().density);
            btnEditar.setBackground(bgEditar);

            btnEditar.setOnClickListener(v -> editarBug(bug));

            row.addView(btnBorrar);
            row.addView(btnEditar);
            contentLayout.addView(row);
        }

        mainLayout.addView(contentLayout);
        cardView.addView(mainLayout);

        return cardView;
    }

    private void borrarBug(Bug bug) {
        db.collection("bugs")
                .document(bug.getId())
                .delete()
                .addOnSuccessListener(unused -> cargarBugs());
    }

    private void editarBug(Bug bug) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(R.layout.modal_editar_bug, null);
        dialog.setContentView(view);

        EditText etNombre      = view.findViewById(R.id.etEditNombre);
        Spinner  spPlataforma  = view.findViewById(R.id.spEditPlataforma);
        Spinner  spTipo        = view.findViewById(R.id.spEditTipo);
        Spinner  spGravedad    = view.findViewById(R.id.spEditGravedad);
        EditText etDescripcion = view.findViewById(R.id.etEditDescripcion);
        EditText etImagenUrl   = view.findViewById(R.id.etEditImagenUrl);

        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        etNombre.setText(bug.getNombreJuego());
        etDescripcion.setText(bug.getDescripcion());
        etImagenUrl.setText(bug.getImagenUrl());

        ArrayAdapter<CharSequence> adapterPlat = ArrayAdapter.createFromResource(
                this,
                R.array.plataformas,
                R.layout.spinner_item
        );
        adapterPlat.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spPlataforma.setAdapter(adapterPlat);

        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_bug,
                R.layout.spinner_item
        );
        adapterTipo.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spTipo.setAdapter(adapterTipo);

        String[] opcionesGravedad = {
                getString(R.string.gravedad_baja),
                getString(R.string.gravedad_media),
                getString(R.string.gravedad_alta)
        };
        ArrayAdapter<String> adapterGrav = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                opcionesGravedad
        );
        adapterGrav.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spGravedad.setAdapter(adapterGrav);

        int indexPlat = adapterPlat.getPosition(bug.getPlataforma());
        if (indexPlat >= 0) spPlataforma.setSelection(indexPlat);

        int indexTipo = adapterTipo.getPosition(bug.getTipo());
        if (indexTipo >= 0) spTipo.setSelection(indexTipo);

        int indexGrav = 0;
        String g = bug.getGravedad() == null ? "" : bug.getGravedad().toUpperCase();
        if (g.contains("MEDIA")) indexGrav = 1;
        if (g.contains("ALTA")) indexGrav = 2;
        spGravedad.setSelection(indexGrav);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {

            String nombre = etNombre.getText().toString().trim();
            String plataforma  = spPlataforma.getSelectedItem().toString();
            String tipo        = spTipo.getSelectedItem().toString();
            String gravedad    = spGravedad.getSelectedItem().toString();
            String descripcion = etDescripcion.getText().toString().trim();
            String imagenUrl   = etImagenUrl.getText().toString().trim();

            db.collection("bugs")
                    .document(bug.getId())
                    .update(
                            "nombreJuego", nombre,
                            "plataforma", plataforma,
                            "tipo", tipo,
                            "gravedad", gravedad,
                            "descripcion", descripcion,
                            "imagenUrl", imagenUrl
                    )
                    .addOnSuccessListener(unused -> {
                        cargarBugs();
                        dialog.dismiss();
                    });
        });

        dialog.show();
    }

    private TextView crearBadgeGravedad(String gravedad) {
        if (gravedad == null) return null;

        String g = gravedad.toUpperCase();
        Integer color = null;
        String texto = null;

        if (g.contains("BAJA")) {
            texto = "PRIORIDAD BAJA";
            color = Color.parseColor("#10B981");
        } else if (g.contains("MEDIA")) {
            texto = "PRIORIDAD MEDIA";
            color = Color.parseColor("#F59E0B");
        } else if (g.contains("ALTA")) {
            texto = "PRIORIDAD ALTA";
            color = Color.parseColor("#EF4444");
        }

        if (texto == null || color == null) return null;

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
        bg.setColor(color);

        badge.setText(texto);
        badge.setBackground(bg);
        return badge;
    }

    private void ordenarPorPrioridad(List<Bug> bugs) {
        bugs.sort((bug1, bug2) -> {
            int prioridad1 = obtenerValorPrioridad(bug1.getGravedad());
            int prioridad2 = obtenerValorPrioridad(bug2.getGravedad());
            return Integer.compare(prioridad2, prioridad1);
        });
    }

    private int obtenerValorPrioridad(String gravedad) {
        if (gravedad == null) return 0;

        String g = gravedad.toUpperCase();

        if (g.contains("ALTA")) return 3;
        if (g.contains("MEDIA")) return 2;
        if (g.contains("BAJA")) return 1;

        return 0;
    }
}