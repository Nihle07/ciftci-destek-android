package com.example.tarimmobilprojesi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;

public class AdminLoginActivity extends AppCompatActivity {

    AutoCompleteTextView edtKullanici;
    EditText edtSifre;
    CheckBox chkBeniHatirla;
    Button btnGiris;

    FirebaseAuth auth;
    FirebaseFirestore db;

    SharedPreferences prefs;

    private static final String PREF_NAME = "AdminLoginPrefs";
    private static final String KEY_ADMIN_EMAILS = "adminEmails";
    private static final String KEY_SIFRE_PREFIX = "sifre_";
    private static final String KEY_BENI_HATIRLA = "beniHatirla";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser mevcutKullanici = auth.getCurrentUser();

        if (mevcutKullanici != null) {
            adminRolKontrolEt(mevcutKullanici.getUid(), true);
            return;
        }

        setContentView(R.layout.activity_admin_login2);

        edtKullanici = findViewById(R.id.edtKullanici);
        edtSifre = findViewById(R.id.edtSifre);
        chkBeniHatirla = findViewById(R.id.chkBeniHatirla);
        btnGiris = findViewById(R.id.btnGiris);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        adminEmailOnerileriniYukle();

        edtKullanici.setOnItemClickListener((parent, view, position, id) -> {
            String secilenEmail = parent.getItemAtPosition(position).toString();
            String kayitliSifre = prefs.getString(KEY_SIFRE_PREFIX + secilenEmail, "");

            edtKullanici.setText(secilenEmail);
            edtSifre.setText(kayitliSifre);
            chkBeniHatirla.setChecked(true);
        });

        btnGiris.setOnClickListener(v -> {
            String email = edtKullanici.getText().toString().trim();
            String sifre = edtSifre.getText().toString().trim();

            if (email.isEmpty() || sifre.isEmpty()) {
                Toast.makeText(this, "Email ve şifre boş bırakılamaz", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtKullanici.setError("Geçersiz e-posta formatı");
                edtKullanici.requestFocus();
                return;
            }

            auth.signInWithEmailAndPassword(email, sifre)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null) {
                            adminBilgileriniKaydet(email, sifre);
                            adminRolKontrolEt(user.getUid(), false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Giriş başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void adminEmailOnerileriniYukle() {
        Set<String> adminEmails = prefs.getStringSet(KEY_ADMIN_EMAILS, new HashSet<>());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                adminEmails.toArray(new String[0])
        );

        edtKullanici.setAdapter(adapter);
        edtKullanici.setThreshold(1);
    }

    private void adminBilgileriniKaydet(String email, String sifre) {
        SharedPreferences.Editor editor = prefs.edit();

        if (chkBeniHatirla.isChecked()) {
            Set<String> eskiSet = prefs.getStringSet(KEY_ADMIN_EMAILS, new HashSet<>());
            Set<String> yeniSet = new HashSet<>(eskiSet);

            yeniSet.add(email);

            editor.putStringSet(KEY_ADMIN_EMAILS, yeniSet);
            editor.putString(KEY_SIFRE_PREFIX + email, sifre);
            editor.putBoolean(KEY_BENI_HATIRLA, true);
        } else {
            editor.remove(KEY_SIFRE_PREFIX + email);
        }

        editor.apply();
    }

    private void adminRolKontrolEt(String uid, boolean otomatikGiris) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        if ("admin".equals(role)) {
                            if (otomatikGiris) {
                                Toast.makeText(this, "Admin oturumu açık, panele yönlendiriliyor", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Admin girişi başarılı", Toast.LENGTH_SHORT).show();
                            }

                            Intent intent = new Intent(AdminLoginActivity.this, AdminPanelActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this, "Bu kullanıcı admin değil", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            recreate();
                        }

                    } else {
                        Toast.makeText(this, "Kullanıcı rol bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
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