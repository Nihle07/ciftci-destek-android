package com.example.tarimmobilprojesi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.Iterator;

public class UserLoginActivity extends AppCompatActivity {

    EditText edtUserEmail, edtUserSifre;
    Button btnUserGiris, btnKayitSayfasinaGit;
    TextView tabKayit, txtSifremiUnuttum;
    CheckBox chkBeniHatirla;
    LinearLayout layoutKayitliHesaplar;

    FirebaseAuth auth;
    FirebaseFirestore db;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser mevcutKullanici = auth.getCurrentUser();
        if (mevcutKullanici != null) {
            kullaniciRolKontrolEt(mevcutKullanici.getUid(), true);
            return;
        }

        setContentView(R.layout.activity_user_login);

        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtUserSifre = findViewById(R.id.edtUserSifre);
        btnUserGiris = findViewById(R.id.btnUserGiris);
        btnKayitSayfasinaGit = findViewById(R.id.btnKayitSayfasinaGit);
        tabKayit = findViewById(R.id.tabKayit);

        chkBeniHatirla = findViewById(R.id.chkBeniHatirla);
        txtSifremiUnuttum = findViewById(R.id.txtSifremiUnuttum);
        layoutKayitliHesaplar = findViewById(R.id.layoutKayitliHesaplar);

        preferences = getSharedPreferences("kayitli_hesaplar", MODE_PRIVATE);

        edtUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kayitliHesaplariGoster(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        tabKayit.setOnClickListener(v -> {
            Intent intent = new Intent(UserLoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnKayitSayfasinaGit.setOnClickListener(v -> {
            Intent intent = new Intent(UserLoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        txtSifremiUnuttum.setOnClickListener(v -> {
            String email = edtUserEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Lütfen e-posta adresinizi girin", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Şifre sıfırlama maili gönderildi", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        btnUserGiris.setOnClickListener(v -> {
            String email = edtUserEmail.getText().toString().trim();
            String sifre = edtUserSifre.getText().toString().trim();

            if (email.isEmpty() || sifre.isEmpty()) {
                Toast.makeText(this, "Email ve şifre boş olamaz", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, sifre)
                    .addOnSuccessListener(authResult -> {
                        if (chkBeniHatirla.isChecked()) {
                            hesabiKaydet(email, sifre);
                        }

                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            kullaniciRolKontrolEt(user.getUid(), false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Giriş başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void hesabiKaydet(String email, String sifre) {
        try {
            String mevcutKayitlar = preferences.getString("hesaplar", "{}");
            JSONObject jsonObject = new JSONObject(mevcutKayitlar);

            jsonObject.put(email, sifre);

            preferences.edit()
                    .putString("hesaplar", jsonObject.toString())
                    .apply();

        } catch (Exception e) {
            Toast.makeText(this, "Hesap kaydedilemedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void kayitliHesaplariGoster(String arananMail) {
        layoutKayitliHesaplar.removeAllViews();

        if (arananMail.isEmpty()) {
            layoutKayitliHesaplar.setVisibility(View.GONE);
            return;
        }

        try {
            String mevcutKayitlar = preferences.getString("hesaplar", "{}");
            JSONObject jsonObject = new JSONObject(mevcutKayitlar);

            Iterator<String> keys = jsonObject.keys();
            boolean sonucVar = false;

            while (keys.hasNext()) {
                String kayitliEmail = keys.next();

                if (kayitliEmail.toLowerCase().startsWith(arananMail.toLowerCase())) {
                    String kayitliSifre = jsonObject.getString(kayitliEmail);

                    TextView hesapView = new TextView(this);
                    hesapView.setText(kayitliEmail + "\nŞifre: ••••••••");
                    hesapView.setTextSize(15);
                    hesapView.setTextColor(Color.parseColor("#191C1A"));
                    hesapView.setPadding(18, 14, 18, 14);
                    hesapView.setBackgroundResource(R.drawable.bg_input);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 0, 10);
                    hesapView.setLayoutParams(params);

                    hesapView.setOnClickListener(v -> {
                        edtUserEmail.setText(kayitliEmail);
                        edtUserSifre.setText(kayitliSifre);
                        chkBeniHatirla.setChecked(true);
                        layoutKayitliHesaplar.setVisibility(View.GONE);
                    });

                    layoutKayitliHesaplar.addView(hesapView);
                    sonucVar = true;
                }
            }

            layoutKayitliHesaplar.setVisibility(sonucVar ? View.VISIBLE : View.GONE);

        } catch (Exception e) {
            layoutKayitliHesaplar.setVisibility(View.GONE);
        }
    }

    private void kullaniciRolKontrolEt(String uid, boolean otomatikGiris) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        if ("user".equals(role)) {
                            Toast.makeText(this, otomatikGiris ? "Kullanıcı oturumu açık" : "Giriş başarılı", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserLoginActivity.this, MapsActivity.class);
                            startActivity(intent);
                            finish();

                        } else if ("admin".equals(role)) {
                            Toast.makeText(this, "Admin oturumu açık. Kullanıcı girişi için önce admin çıkışı yapın.", Toast.LENGTH_LONG).show();
                            auth.signOut();
                            recreate();

                        } else {
                            Toast.makeText(this, "Geçersiz kullanıcı rolü", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            recreate();
                        }

                    } else {
                        Toast.makeText(this, "Kullanıcı bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                        recreate();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Rol kontrolü başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    auth.signOut();
                });
    }
}