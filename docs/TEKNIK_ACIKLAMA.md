# Kaynak incelemesi ve paketleme notu

## Mevcut proje

Dış ZIP, MobilProjeÖdev2 klasöründe TarimMobilProjesi.zip içeriyordu. İç arşivde Android Studio projesi ve derleme çıktıları bulunuyor. Java sınıfları, XML arayüzleri, Manifest, Gradle tanımları, sürüm kataloğu ve Firestore kuralları incelendi. Arayüzdeki ad ÇiftçiDestek olarak kullanıldı.

Kaynak giriş noktası MainActivity'dir. Kullanıcı akışı UserLoginActivity/RegisterActivity üzerinden MapsActivity'ye; yönetici akışı AdminLoginActivity üzerinden AdminPanelActivity'ye gider. AddPlaceActivity ve UpdatePlaceActivity Firestore kayıtlarını yönetir. First/Second fragment sınıfları kaynaktaki şablon dosyaları olarak korundu.

## Verilen PDF formatıyla eşleşme

| PDF'deki bölüm | Paketteki karşılık |
|---|---|
| Proje adı, kısa özet ve görsel | README başlangıcı + assets/proje-ozeti.svg |
| Problem/yöntem/donanım/makale özeti | README Özet tablosu; makale bulunmadığı açıkça belirtilir |
| Sistem mimarisi | README Mermaid şeması ve açıklaması |
| Açıklamalı seçilmiş çıktılar | Kaynakta bulunan işlevler ve beklenen çıktılar tablosu |
| Kurulum ve kullanım | README + KURULUM.md; gerçek bağımlılıklar ve sürümler |
| Sonuçların yeniden gösterimi | README kontrol tablosu + GOSTERIM_KONTROLU.md |
| Klasör yapısı | README Proje yapısı tablosu |
| Atıf/lisans/iletişim | README son bölümü; kaynaktaki eksikler uydurulmaz |

Sayısal deney/başarı oranı eklenmedi. Açıklayıcı SVG cihaz ekran görüntüsü olarak etiketlenmedi. Gerçek ekran görüntüsü ve varsa ders raporu sonradan eklenebilir.

## Korunanlar ve değişiklikler

- app/src içindeki tüm Java dosyaları ve arayüz XML'leri byte düzeyinde korundu.
- app/src/main/AndroidManifest.xml içindeki doğrudan Maps anahtarı `${MAPS_API_KEY}` yer tutucusuna çevrildi.
- app/build.gradle içine local.properties okuma ve manifestPlaceholders tanımı eklendi. Java iş mantığına müdahale edilmedi; sürümler değiştirilmedi.
- Gradle Wrapper jar ve çalıştırıcıları korundu. gradlew çalıştırma izni paket içinde ayarlandı.
- app/google-services.json pakete alınmadı; kullanıcı kendi Firebase dosyasını ekler. Bu dosya Firebase istemci yapılandırmasıdır; çıkarılması tek başına veritabanı yetkilendirmesi sağlamaz.
- .gradle, .idea, build, APK ve yerel SDK ayarları çıkarıldı; .gitignore yenilendi. Kaynak ZIP'te derlenmiş bir debug APK bulunması bu paketin burada derlenip test edildiği anlamına gelmez.
- README, kurulum/gösterim/GitHub rehberleri, bu teknik açıklama, kaynak hash kayıtları, local.properties.example ve açıklayıcı SVG eklendi.

Dosya bazında koruma/değişiklik kayıtları SOURCE_FILES.json içindedir. Yeni model, deney, özellik veya başarı iddiası eklenmedi.

## Bilinen sinirlar

1. Özgün firestore.rules, kullanıcıya kendi users belgesinde bütün alanları yazma izni veriyor. role alanı da buna dahil olduğundan uygulamadaki admin rol kontrolü güvenli sunucu yetkilendirmesi olarak kabul edilemez. Kural aslı korundu; gerçek kullanım öncesinde yetkilendirme ayrıca düzeltilmelidir.
2. UserLoginActivity ve AdminLoginActivity, "beni hatırla" akışında şifreleri SharedPreferences içinde saklıyor. Gerçek hesaplarla dağıtım öncesinde bu davranış gözden geçirilmelidir; uygulama mantığı bu görevde değiştirilmedi.
3. Konum takibi ve 100 m uyarısı MapsActivity içindeki konum güncellemelerine dayanıyor. Bu özellik arka planda sürekli çalışan geofencing olarak doğrulanmadı.
4. Firebase veri tabanı dökümü ve hesaplar verilmedi. Uygulama cihazda test edilmedi; ekran ve beklenen davranış açıklamaları kaynak kod incelemesine dayanıyor.
5. Kaynakta JDK daemon 21 tanımlı, mevcut hazırlık ortamında Java 17 var ve Android SDK/cihaz ortamı hazır değil. Tam Gradle Sync/assembleDebug çalıştırılmadı.
6. Kaynak testleri şablon toplama/paket adı testleri; Firebase, arama, konum veya CRUD için işlevsel test kanıtı sağlamaz.

## Paket üzerinde yapılan kontroller

Java kaynaklarının hash eşitliği, XML dosyalarının sözdizimi, README bağlantılarının hedefleri, Manifest ekranlarının sınıf karşılıkları, Gradle Wrapper dosyaları ve gerçek Maps anahtarının pakette bulunmaması kontrol edildi. Çalışma zamanı başarısı veya güvenlik denetiminin tamamlandığı iddia edilmez.
