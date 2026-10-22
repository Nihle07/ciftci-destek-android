package com.example.tarimmobilprojesi;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;import android.widget.ImageButton;import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
public class AdminPanelActivity extends AppCompatActivity {

    LinearLayout containerNoktalar;
    FirebaseFirestore db;

    Button btnYeniNokta, btnHaritayiGor;
    ImageButton btnCikisYap;

    EditText edtAdminArama;
    String adminAramaMetni = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        edtAdminArama = findViewById(R.id.edtAdminArama);

        edtAdminArama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adminAramaMetni = s.toString().trim().toLowerCase();
                verileriGetir();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnHaritayiGor = findViewById(R.id.btnHaritayiGor);
        btnCikisYap = findViewById(R.id.btnCikisYap);
        btnYeniNokta = findViewById(R.id.btnYeniNokta);
        containerNoktalar = findViewById(R.id.containerNoktalar);

        db = FirebaseFirestore.getInstance();

        btnHaritayiGor.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        btnCikisYap.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(AdminPanelActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnYeniNokta.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, AddPlaceActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        verileriGetir();
    }

    private void verileriGetir() {
        containerNoktalar.removeAllViews();

        db.collection("tarim_noktalari")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        TextView bosText = new TextView(this);
                        bosText.setText("Henüz kayıtlı destek noktası bulunmuyor.");
                        bosText.setTextSize(16);
                        bosText.setTextColor(Color.parseColor("#42493E"));
                        bosText.setPadding(0, 20, 0, 20);
                        containerNoktalar.addView(bosText);
                        return;
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        String id = document.getId();
                        String noktaAdi = document.getString("noktaAdi");
                        String kategori = document.getString("kategori");
                        String ilce = document.getString("ilce");
                        String telefon = document.getString("telefon");
                        String webSitesi = document.getString("webSitesi");

                        String aciklama = document.getString("aciklama");


                        String aramaAlani =
                                (noktaAdi != null ? noktaAdi : "") + " " +
                                        (kategori != null ? kategori : "") + " " +
                                        (ilce != null ? ilce : "") + " " +
                                        (telefon != null ? telefon : "") + " " +
                                        (aciklama != null ? aciklama : "") + " " +
                                        (webSitesi != null ? webSitesi : "");

                        if (!adminAramaMetni.isEmpty() &&
                                !aramaAlani.toLowerCase().contains(adminAramaMetni)) {
                            continue;
                        }

                        LinearLayout card = new LinearLayout(this);
                        card.setOrientation(LinearLayout.VERTICAL);
                        card.setPadding(22, 22, 22, 22);

                        GradientDrawable cardBg = new GradientDrawable();
                        cardBg.setColor(Color.WHITE);
                        cardBg.setCornerRadius(24);
                        cardBg.setStroke(1, Color.parseColor("#E1E3DE"));
                        card.setBackground(cardBg);

                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        cardParams.setMargins(0, 0, 0, 18);
                        card.setLayoutParams(cardParams);
                        card.setElevation(4);

                        TextView txtAd = new TextView(this);
                        txtAd.setText(noktaAdi != null ? noktaAdi : "İsimsiz Nokta");
                        txtAd.setTextSize(20);
                        txtAd.setTypeface(null, Typeface.BOLD);
                        txtAd.setTextColor(Color.parseColor("#191C1A"));

                        TextView txtBilgi = new TextView(this);
                        txtBilgi.setText(
                                "Kategori: " + (kategori != null ? kategori : "-") +
                                        "\nİlçe: " + (ilce != null ? ilce : "-") +
                                        "\nTelefon: " + (telefon != null ? telefon : "-") +
                                        "\nWeb Sitesi: " + (webSitesi != null && !webSitesi.trim().isEmpty() ? webSitesi : "-")
                        );
                        txtBilgi.setTextSize(16);
                        txtBilgi.setTextColor(Color.parseColor("#42493E"));
                        txtBilgi.setPadding(0, 12, 0, 18);

                        LinearLayout buttonLayout = new LinearLayout(this);
                        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

                        Button btnGuncelle = new Button(this);
                        btnGuncelle.setText("Güncelle");
                        btnGuncelle.setTextColor(Color.WHITE);
                        btnGuncelle.setTextSize(14);
                        btnGuncelle.setBackgroundResource(R.drawable.bg_primary_button);

                        Button btnSil = new Button(this);
                        btnSil.setText("Sil");
                        btnSil.setTextColor(Color.parseColor("#154212"));
                        btnSil.setTextSize(14);
                        btnSil.setBackgroundResource(R.drawable.bg_outline_button);

                        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                                0,
                                100,
                                1
                        );

                        LinearLayout.LayoutParams btnParams2 = new LinearLayout.LayoutParams(
                                0,
                                100,
                                1
                        );
                        btnParams2.setMargins(8, 0, 0, 0);

                        buttonLayout.addView(btnGuncelle, btnParams);
                        buttonLayout.addView(btnSil, btnParams2);

                        card.addView(txtAd);
                        card.addView(txtBilgi);
                        card.addView(buttonLayout);

                        containerNoktalar.addView(card);

                        btnSil.setOnClickListener(v -> {
                            new AlertDialog.Builder(this)
                                    .setTitle("Silme İşlemi")
                                    .setMessage("Bu kaydı silmek istediğinize emin misiniz?")
                                    .setPositiveButton("Evet", (dialog, which) -> {
                                        db.collection("tarim_noktalari")
                                                .document(id)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(this, "Kayıt silindi", Toast.LENGTH_SHORT).show();
                                                    verileriGetir();
                                                });
                                    })
                                    .setNegativeButton("Hayır", null)
                                    .show();
                        });

                        btnGuncelle.setOnClickListener(v -> {
                            Intent intent = new Intent(AdminPanelActivity.this, UpdatePlaceActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Veriler alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}