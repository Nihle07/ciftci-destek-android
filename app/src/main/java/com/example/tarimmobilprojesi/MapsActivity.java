package com.example.tarimmobilprojesi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    FirebaseFirestore db;

    Spinner spinnerKategori, spinnerIlce;
    LinearLayout bilgiKart, layoutYakinNoktalar;

    TextView txtDetayAd, txtDetayKategori, txtDetayIlce,
            txtDetayTelefon, txtDetayAciklama, btnKapatBilgi;

    EditText edtHaritaArama;
    String aramaMetni = "";

    Button btnYolTarifi, btnWebSitesi;
    TarimNoktasi seciliNokta;

    String seciliKategori = "Tümü";
    String seciliIlce = "Tümü";

    ArrayList<TarimNoktasi> tarimNoktalari = new ArrayList<>();
    ArrayList<TarimNoktasi> aktifNoktalar = new ArrayList<>();
    ArrayList<Marker> markerListesi = new ArrayList<>();
    ArrayList<Circle> circleListesi = new ArrayList<>();

    HashSet<String> uyariVerilenNoktalar = new HashSet<>();

    LatLng kullaniciKonumu;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final int KONUM_IZIN_KODU = 1001;
    private static final float ALAN_YARICAPI_METRE = 100;

    public static class TarimNoktasi {
        String id, ad, kategori, ilce, telefon, aciklama, webSitesi;
        double latitude, longitude;

        public TarimNoktasi(String id, String ad, String kategori, String ilce,
                            String telefon, String aciklama, String webSitesi,
                            double latitude, double longitude) {
            this.id = id;
            this.ad = ad;
            this.kategori = kategori;
            this.ilce = ilce;
            this.telefon = telefon;
            this.aciklama = aciklama;
            this.webSitesi = webSitesi;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        edtHaritaArama = findViewById(R.id.edtHaritaArama);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        spinnerIlce = findViewById(R.id.spinnerIlce);

        bilgiKart = findViewById(R.id.bilgiKart);
        layoutYakinNoktalar = findViewById(R.id.layoutYakinNoktalar);

        txtDetayAd = findViewById(R.id.txtDetayAd);
        txtDetayKategori = findViewById(R.id.txtDetayKategori);
        txtDetayIlce = findViewById(R.id.txtDetayIlce);
        txtDetayTelefon = findViewById(R.id.txtDetayTelefon);
        txtDetayAciklama = findViewById(R.id.txtDetayAciklama);
        btnKapatBilgi = findViewById(R.id.btnKapatBilgi);

        btnYolTarifi = findViewById(R.id.btnYolTarifi);
        btnWebSitesi = findViewById(R.id.btnWebSitesi);

        edtHaritaArama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aramaMetni = s.toString().trim().toLowerCase(new Locale("tr", "TR"));
                bilgiKart.setVisibility(View.GONE);
                haritayiFiltrele();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnKapatBilgi.setOnClickListener(v -> bilgiKart.setVisibility(View.GONE));

        txtDetayTelefon.setOnClickListener(v -> {
            if (seciliNokta != null &&
                    seciliNokta.telefon != null &&
                    !seciliNokta.telefon.trim().isEmpty()) {

                telefonAc(seciliNokta.telefon);

            } else {
                Toast.makeText(this, "Bu nokta için telefon numarası eklenmemiş", Toast.LENGTH_SHORT).show();
            }
        });

        btnYolTarifi.setOnClickListener(v -> {
            if (seciliNokta != null) {
                yolTarifiAc(seciliNokta.latitude, seciliNokta.longitude);
            } else {
                Toast.makeText(this, "Önce bir tarım noktası seçin", Toast.LENGTH_SHORT).show();
            }
        });

        btnWebSitesi.setOnClickListener(v -> {
            if (seciliNokta != null &&
                    seciliNokta.webSitesi != null &&
                    !seciliNokta.webSitesi.trim().isEmpty()) {
                webSitesiAc(seciliNokta.webSitesi);
            } else {
                Toast.makeText(this, "Bu nokta için web sitesi eklenmemiş", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton btnCikisYap = findViewById(R.id.btnCikisYap);
        btnCikisYap.setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });

        TextView bekleme = new TextView(this);
        bekleme.setText("Konum ve noktalar yükleniyor...");
        bekleme.setPadding(16, 16, 16, 16);
        layoutYakinNoktalar.addView(bekleme);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Harita fragment bulunamadı", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sakarya = new LatLng(40.7569, 30.3781);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sakarya, 10));

        mMap.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();

            if (tag instanceof TarimNoktasi) {
                TarimNoktasi nokta = (TarimNoktasi) tag;
                bilgiKartiniGoster(nokta);
            }

            return true;
        });

        mMap.setOnMapClickListener(latLng -> bilgiKart.setVisibility(View.GONE));

        tarimNoktalariniGetir();
        konumIzniKontrolEt();
    }

    private void tarimNoktalariniGetir() {
        db.collection("tarim_noktalari")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tarimNoktalari.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Object latObj = document.get("latitude");
                        Object lngObj = document.get("longitude");

                        if (latObj == null || lngObj == null) continue;

                        try {
                            double latitude = Double.parseDouble(latObj.toString());
                            double longitude = Double.parseDouble(lngObj.toString());

                            tarimNoktalari.add(new TarimNoktasi(
                                    document.getId(),
                                    temizle(document.get("noktaAdi")),
                                    temizle(document.get("kategori")),
                                    temizle(document.get("ilce")),
                                    temizle(document.get("telefon")),
                                    temizle(document.get("aciklama")),
                                    temizle(document.get("webSitesi")),
                                    latitude,
                                    longitude
                            ));

                        } catch (Exception e) {
                            Toast.makeText(this, "Bir noktanın konumu okunamadı", Toast.LENGTH_SHORT).show();
                        }
                    }

                    filtreSpinnerlariniOlustur();
                    haritayiFiltrele();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Firebase hatası: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private String temizle(Object deger) {
        if (deger == null) return "";
        if (String.valueOf(deger).equals("null")) return "";
        return String.valueOf(deger);
    }

    private void filtreSpinnerlariniOlustur() {
        HashSet<String> kategoriSet = new HashSet<>();
        HashSet<String> ilceSet = new HashSet<>();

        kategoriSet.add("Kategori Seç");
        ilceSet.add("İlçe Seç");

        for (TarimNoktasi nokta : tarimNoktalari) {
            if (!nokta.kategori.trim().isEmpty()) kategoriSet.add(nokta.kategori);
            if (!nokta.ilce.trim().isEmpty()) ilceSet.add(nokta.ilce);
        }

        ArrayList<String> kategoriListesi = new ArrayList<>(kategoriSet);
        ArrayList<String> ilceListesi = new ArrayList<>(ilceSet);

        Collections.sort(kategoriListesi);
        Collections.sort(ilceListesi);

        kategoriListesi.remove("Kategori Seç");
        ilceListesi.remove("İlçe Seç");

        kategoriListesi.add(0, "Kategori Seç");
        ilceListesi.add(0, "İlçe Seç");

        ArrayAdapter<String> kategoriAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                kategoriListesi
        );
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategori.setAdapter(kategoriAdapter);

        ArrayAdapter<String> ilceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ilceListesi
        );
        ilceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIlce.setAdapter(ilceAdapter);

        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seciliKategori = position == 0 ? "Tümü" : parent.getItemAtPosition(position).toString();
                bilgiKart.setVisibility(View.GONE);
                haritayiFiltrele();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerIlce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seciliIlce = position == 0 ? "Tümü" : parent.getItemAtPosition(position).toString();
                bilgiKart.setVisibility(View.GONE);
                haritayiFiltrele();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void haritayiFiltrele() {
        if (mMap == null) return;

        for (Marker marker : markerListesi) marker.remove();
        for (Circle circle : circleListesi) circle.remove();

        markerListesi.clear();
        circleListesi.clear();
        aktifNoktalar.clear();

        for (TarimNoktasi nokta : tarimNoktalari) {
            boolean kategoriUygun =
                    seciliKategori.equals("Tümü") ||
                            nokta.kategori.equalsIgnoreCase(seciliKategori);

            boolean ilceUygun =
                    seciliIlce.equals("Tümü") ||
                            nokta.ilce.equalsIgnoreCase(seciliIlce);

            boolean aramaUygun = aramaMetni.isEmpty() || noktaAramayaUygunMu(nokta);

            if (kategoriUygun && ilceUygun && aramaUygun) {
                aktifNoktalar.add(nokta);

                LatLng konum = new LatLng(nokta.latitude, nokta.longitude);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(konum)
                        .title(nokta.ad));

                if (marker != null) {
                    marker.setTag(nokta);
                    markerListesi.add(marker);
                }

                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(konum)
                        .radius(ALAN_YARICAPI_METRE)
                        .strokeWidth(3)
                        .strokeColor(0x55154212)
                        .fillColor(0x22154212));

                circleListesi.add(circle);
            }
        }

        yakinNoktalariListele();
    }

    private boolean noktaAramayaUygunMu(TarimNoktasi nokta) {
        String aramaAlani =
                guvenliMetin(nokta.ad) + " " +
                        guvenliMetin(nokta.kategori) + " " +
                        guvenliMetin(nokta.ilce) + " " +
                        guvenliMetin(nokta.telefon) + " " +
                        guvenliMetin(nokta.aciklama) + " " +
                        guvenliMetin(nokta.webSitesi);

        return aramaAlani.toLowerCase(new Locale("tr", "TR")).contains(aramaMetni);
    }

    private String guvenliMetin(String metin) {
        if (metin == null) return "";
        return metin;
    }

    private void bilgiKartiniGoster(TarimNoktasi nokta) {
        seciliNokta = nokta;

        txtDetayAd.setText(nokta.ad);
        txtDetayKategori.setText("Kategori: " + nokta.kategori);
        txtDetayIlce.setText("İlçe: " + nokta.ilce);

        if (nokta.telefon == null || nokta.telefon.trim().isEmpty()) {
            txtDetayTelefon.setText("Telefon: -");
            txtDetayTelefon.setTextColor(android.graphics.Color.parseColor("#42493E"));
            txtDetayTelefon.setPaintFlags(
                    txtDetayTelefon.getPaintFlags() & (~android.graphics.Paint.UNDERLINE_TEXT_FLAG)
            );
        } else {
            txtDetayTelefon.setText("Telefon: " + nokta.telefon);
            txtDetayTelefon.setTextColor(android.graphics.Color.parseColor("#1565C0"));
            txtDetayTelefon.setPaintFlags(
                    txtDetayTelefon.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG
            );
        }

        txtDetayAciklama.setText("Açıklama: " + nokta.aciklama);

        if (nokta.webSitesi == null || nokta.webSitesi.trim().isEmpty()) {
            btnWebSitesi.setVisibility(View.GONE);
        } else {
            btnWebSitesi.setVisibility(View.VISIBLE);
        }

        bilgiKart.setVisibility(View.VISIBLE);
    }

    private void telefonAc(String telefon) {
        String temizTelefon = telefon.replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "");

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + temizTelefon));

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Telefon uygulaması açılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void yolTarifiAc(double latitude, double longitude) {
        Uri gmmIntentUri = Uri.parse(
                "google.navigation:q=" + latitude + "," + longitude + "&mode=d"
        );

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri webUri = Uri.parse(
                    "https://www.google.com/maps/dir/?api=1&destination="
                            + latitude + "," + longitude
            );

            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }

    private void webSitesiAc(String webSitesi) {
        String url = webSitesi.trim();

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Web sitesi açılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void konumIzniKontrolEt() {
        boolean fineGranted = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        if (fineGranted || coarseGranted) {
            konumTakibiniBaslat();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    KONUM_IZIN_KODU
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == KONUM_IZIN_KODU) {
            boolean granted = false;

            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }

            if (granted) {
                konumTakibiniBaslat();
            } else {
                Toast.makeText(this, "Konum izni verilmedi.", Toast.LENGTH_LONG).show();

                new AlertDialog.Builder(this)
                        .setTitle("Konum İzni Gerekli")
                        .setMessage("Yakındaki tarım noktalarını sıralamak için konum izni gereklidir.")
                        .setPositiveButton("Tamam", null)
                        .show();
            }
        }
    }

    private void konumTakibiniBaslat() {
        boolean fineGranted = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        if (!fineGranted && !coarseGranted) return;

        try {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            kullaniciKonumu = new LatLng(location.getLatitude(), location.getLongitude());
                            yakinNoktalariListele();
                            alanKontrolEt(location);
                        }
                    });

            int priority = fineGranted
                    ? Priority.PRIORITY_HIGH_ACCURACY
                    : Priority.PRIORITY_BALANCED_POWER_ACCURACY;

            LocationRequest locationRequest =
                    new LocationRequest.Builder(priority, 5000)
                            .setMinUpdateDistanceMeters(5)
                            .build();

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        kullaniciKonumu = new LatLng(location.getLatitude(), location.getLongitude());
                        yakinNoktalariListele();
                        alanKontrolEt(location);
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    getMainLooper()
            );

        } catch (SecurityException e) {
            Toast.makeText(this, "Konum takibi başlatılamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void alanKontrolEt(Location location) {
        for (TarimNoktasi nokta : tarimNoktalari) {
            float[] sonuc = new float[1];

            Location.distanceBetween(
                    location.getLatitude(),
                    location.getLongitude(),
                    nokta.latitude,
                    nokta.longitude,
                    sonuc
            );

            float mesafe = sonuc[0];

            if (mesafe <= ALAN_YARICAPI_METRE &&
                    !uyariVerilenNoktalar.contains(nokta.id)) {

                uyariVerilenNoktalar.add(nokta.id);

                new AlertDialog.Builder(this)
                        .setTitle("Tarım Noktasına Girdiniz")
                        .setMessage(nokta.ad + " alanına hoş geldiniz.")
                        .setPositiveButton("Tamam", null)
                        .show();
            }

            if (mesafe > ALAN_YARICAPI_METRE + 30) {
                uyariVerilenNoktalar.remove(nokta.id);
            }
        }
    }

    private double mesafeHesapla(double lat1, double lon1, double lat2, double lon2) {
        float[] sonuc = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, sonuc);
        return sonuc[0] / 1000.0;
    }

    private void yakinNoktalariListele() {
        if (layoutYakinNoktalar == null) return;

        layoutYakinNoktalar.removeAllViews();

        if (kullaniciKonumu == null) {
            TextView uyari = new TextView(this);
            uyari.setText("Konum bekleniyor. Lütfen konum iznini açın.");
            uyari.setPadding(16, 16, 16, 16);
            layoutYakinNoktalar.addView(uyari);
            return;
        }

        if (aktifNoktalar.isEmpty()) {
            TextView uyari = new TextView(this);
            uyari.setText("Seçili filtreye veya aramaya uygun nokta bulunamadı.");
            uyari.setPadding(16, 16, 16, 16);
            layoutYakinNoktalar.addView(uyari);
            return;
        }

        ArrayList<TarimNoktasi> siraliListe = new ArrayList<>(aktifNoktalar);

        Collections.sort(siraliListe, (n1, n2) -> {
            double mesafe1 = mesafeHesapla(
                    kullaniciKonumu.latitude,
                    kullaniciKonumu.longitude,
                    n1.latitude,
                    n1.longitude
            );

            double mesafe2 = mesafeHesapla(
                    kullaniciKonumu.latitude,
                    kullaniciKonumu.longitude,
                    n2.latitude,
                    n2.longitude
            );

            return Double.compare(mesafe1, mesafe2);
        });

        int sira = 1;

        for (TarimNoktasi nokta : siraliListe) {
            double mesafe = mesafeHesapla(
                    kullaniciKonumu.latitude,
                    kullaniciKonumu.longitude,
                    nokta.latitude,
                    nokta.longitude
            );

            TextView kutu = new TextView(this);
            kutu.setText(
                    sira + ". " + nokta.ad + "\n" +
                            "Kategori: " + nokta.kategori + "\n" +
                            "İlçe: " + nokta.ilce + "\n" +
                            "Mesafe: " + String.format("%.2f", mesafe) + " km"
            );

            kutu.setTextSize(15);
            kutu.setPadding(18, 14, 18, 14);
            kutu.setTextColor(android.graphics.Color.parseColor("#191C1A"));
            kutu.setBackgroundResource(R.drawable.bg_input);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 12);
            kutu.setLayoutParams(params);

            kutu.setOnClickListener(v -> {
                LatLng konum = new LatLng(nokta.latitude, nokta.longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(konum, 16));
                bilgiKartiniGoster(nokta);
            });

            layoutYakinNoktalar.addView(kutu);
            sira++;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}