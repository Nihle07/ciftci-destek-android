package com.example.tarimmobilprojesi;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
public class RegisterActivity extends AppCompatActivity {
    EditText edtAdSoyad, edtEmail, edtSifre;
    Button btnKayitOl;
    TextView tabGiris;
    FirebaseAuth auth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtAdSoyad = findViewById(R.id.edtAdSoyad);
        edtEmail = findViewById(R.id.edtEmail);
        edtSifre = findViewById(R.id.edtSifre);
        btnKayitOl = findViewById(R.id.btnKayitOl);
        tabGiris = findViewById(R.id.tabGiris);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tabGiris.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, UserLoginActivity.class);
            startActivity(intent);
            finish();
        });
        btnKayitOl.setOnClickListener(v -> {
            String adSoyad = edtAdSoyad.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String sifre = edtSifre.getText().toString().trim();
            if (adSoyad.isEmpty()) {
                edtAdSoyad.setError("Ad Soyad boş bırakılamaz");
                edtAdSoyad.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                edtEmail.setError("E-posta adresi boş bırakılamaz");
                edtEmail.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Geçersiz e-posta formatı");
                edtEmail.requestFocus();
                return;
            }

            if (sifre.isEmpty()) {
                edtSifre.setError("Şifre boş bırakılamaz");
                edtSifre.requestFocus();
                return;
            }

            if (sifre.length() < 6) {
                edtSifre.setError("Şifre en az 6 karakter olmalı");
                edtSifre.requestFocus();
                return;
            }
            auth.createUserWithEmailAndPassword(email, sifre)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("adSoyad", adSoyad);
                            userMap.put("email", email);
                            userMap.put("role", "user");
                            db.collection("users")
                                    .document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Kayıt başarılı", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, UserLoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Firestore kayıt hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        String hataMesaji = e.getMessage();
                        if (hataMesaji != null &&
                                hataMesaji.contains("The email address is already in use")) {
                            Toast.makeText(this,
                                    "Bu email ile zaten kayıt olunmuş",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this,
                                    "Kayıt başarısız: " + hataMesaji,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}