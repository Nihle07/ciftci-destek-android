package com.example.tarimmobilprojesi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPlaceActivity extends AppCompatActivity {

    EditText edtNoktaAdi, edtKategori, edtIlce,
            edtTelefon, edtAciklama, edtWebSitesi,
            edtLatitude, edtLongitude;

    Button btnKaydet;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_place);

        db = FirebaseFirestore.getInstance();

        edtNoktaAdi = findViewById(R.id.edtNoktaAdi);
        edtKategori = findViewById(R.id.edtKategori);
        edtIlce = findViewById(R.id.edtIlce);
        edtTelefon = findViewById(R.id.edtTelefon);
        edtAciklama = findViewById(R.id.edtAciklama);
        edtWebSitesi = findViewById(R.id.edtWebSitesi);
        edtLatitude = findViewById(R.id.edtLatitude);
        edtLongitude = findViewById(R.id.edtLongitude);
        btnKaydet = findViewById(R.id.btnKaydet);


        btnKaydet.setOnClickListener(v -> {

            String noktaAdi = edtNoktaAdi.getText().toString().trim();
            String kategori = edtKategori.getText().toString().trim();
            String ilce = edtIlce.getText().toString().trim();
            String telefon = edtTelefon.getText().toString().trim();
            String aciklama = edtAciklama.getText().toString().trim();
            String webSitesi = edtWebSitesi.getText().toString().trim();
            String latitudeStr = edtLatitude.getText().toString().trim();
            String longitudeStr = edtLongitude.getText().toString().trim();


            if (noktaAdi.isEmpty()) {
                edtNoktaAdi.setError("Nokta adı boş bırakılamaz");
                edtNoktaAdi.requestFocus();
                return;
            }

            if (kategori.isEmpty()) {
                edtKategori.setError("Kategori boş bırakılamaz");
                edtKategori.requestFocus();
                return;
            }

            if (ilce.isEmpty()) {
                edtIlce.setError("İlçe boş bırakılamaz");
                edtIlce.requestFocus();
                return;
            }

            if (telefon.isEmpty()) {
                edtTelefon.setError("Telefon boş bırakılamaz");
                edtTelefon.requestFocus();
                return;
            }

            if (aciklama.isEmpty()) {
                edtAciklama.setError("Açıklama boş bırakılamaz");
                edtAciklama.requestFocus();
                return;
            }

            if (latitudeStr.isEmpty()) {
                edtLatitude.setError("Enlem boş bırakılamaz");
                edtLatitude.requestFocus();
                return;
            }

            if (longitudeStr.isEmpty()) {
                edtLongitude.setError("Boylam boş bırakılamaz");
                edtLongitude.requestFocus();
                return;
            }

            double latitude;
            double longitude;

            try {
                latitude = Double.parseDouble(latitudeStr);

                if (latitude < -90 || latitude > 90) {
                    edtLatitude.setError("Enlem -90 ile 90 arasında olmalıdır");
                    edtLatitude.requestFocus();
                    return;
                }

            } catch (NumberFormatException e) {
                edtLatitude.setError("Geçersiz enlem formatı");
                edtLatitude.requestFocus();
                return;
            }

            try {
                longitude = Double.parseDouble(longitudeStr);

                if (longitude < -180 || longitude > 180) {
                    edtLongitude.setError("Boylam -180 ile 180 arasında olmalıdır");
                    edtLongitude.requestFocus();
                    return;
                }

            } catch (NumberFormatException e) {
                edtLongitude.setError("Geçersiz boylam formatı");
                edtLongitude.requestFocus();
                return;
            }

            Map<String, Object> nokta = new HashMap<>();
            nokta.put("noktaAdi", noktaAdi);
            nokta.put("kategori", kategori);
            nokta.put("ilce", ilce);
            nokta.put("telefon", telefon);
            nokta.put("aciklama", aciklama);
            nokta.put("webSitesi", webSitesi);
            nokta.put("latitude", latitude);
            nokta.put("longitude", longitude);


            db.collection("tarim_noktalari")
                    .add(nokta)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(
                                AddPlaceActivity.this,
                                "Tarım noktası başarıyla kaydedildi",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                AddPlaceActivity.this,
                                "Kayıt hatası: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });
    }
}