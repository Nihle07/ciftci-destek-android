# Firebase, Google Maps ve Android kurulumu

Bu proje kullanıcının mevcut hesabına bağlanacak şekilde yeniden yapılandırılmalıdır. Kaynak Java sınıfları değiştirilmemiştir. Paket içindeki Gradle/Manifest değişikliği yalnızca Maps anahtarının yerel ayardan okunmasını sağlar.

## 1. Firebase bağlantısı

1. Kendi Firebase Console projenizi oluşturun veya test projenizi seçin.
2. Android uygulaması eklerken paket adını `com.example.tarimmobilprojesi` girin. Paket adı app/build.gradle içindeki applicationId ile eşleşmelidir.
3. İndirilen `google-services.json` dosyasını `app/google-services.json` konumuna koyun. Bu dosyanın içeriğini elle uydurmayın; `.gitignore` paylaşım dışında tutar.
4. Authentication içinde Email/Password giriş sağlayıcısını etkinleştirin.
5. Cloud Firestore veritabanını oluşturun. Bölge ve veri saklama tercihlerini kendi projenize göre seçin.
6. Aşağıdaki şemaya uygun test verisi kullanın.

Özgün firestore.rules dosyası arşiv amacıyla korunmuştur. `users/{uid}` için `allow read, write` kullanıcının kendi `role` alanını da değiştirmesine izin verir. Bu nedenle bu kuralların güvenli yönetici yetkilendirmesi sağladığı varsayılmamalıdır. Gerçek kullanıcı verisiyle yayın yapmadan önce profil yazma izinleri, admin rolünü yalnızca yetkili sunucu/Console yönetecek biçimde düzenlenmelidir. Bu paketleme görevinde kural mantığı değiştirilmemiştir.

Özgün prototipi denemek için yalnızca kendi test projenizi ve test hesaplarınızı kullanın; paket bu bulut kaynaklarında otomatik değişiklik yapmaz. Kuralları yayınlama kararı ve işlemi Firebase Console'da size aittir.

## 2. Google Maps bağlantısı

1. Kendi Google Cloud projenizde Maps SDK for Android'i etkinleştirin; Google'ın istediği hesap/faturalandırma yapılandırmasını tamamlayın.
2. Android API anahtarı oluşturun. Uygulama kısıtını paket adı ve imzalama SHA-1 değeriyle, API kısıtını gerekli Maps SDK ile ayarlayın.
3. Android Studio'nun oluşturduğu yerel `local.properties` dosyasına aşağıdaki satırı ekleyin. Var olan `sdk.dir` satırını silmeyin.

```properties
MAPS_API_KEY=KENDI_MAPS_ANDROID_ANAHTARINIZ
```

4. Örnek satır kökteki local.properties.example içinde de bulunur. Gerçek anahtarı README veya Manifest'e yazmayın. `local.properties` Git dışında tutulur.
5. Gradle Sync yapın. Manifest'teki `${MAPS_API_KEY}` değeri app/build.gradle tarafından bu dosyadan alınır.

Bu yöntem anahtarın Git deposuna eklenmesini önler; APK içindeki istemci anahtarını gizli bir sunucu sırrına dönüştürmez. Android uygulama/API kısıtları yine gereklidir.

SHA-1 için Windows PowerShell:

```powershell
.\gradlew.bat :app:signingReport
```

macOS/Linux:

```bash
./gradlew :app:signingReport
```

## Veri şeması

`users/{uid}`:

| Alan | Tür | Açıklama |
|---|---|---|
| adSoyad | string | Kayıt ekranında girilir |
| email | string | Firebase Authentication e-postası |
| role | string | Kayıt ekranı `user` yazar; admin rolü test için yetkili Console erişimiyle verilir |

`tarim_noktalari/{otomatik_id}`:

| Alan | Tür | Açıklama |
|---|---|---|
| noktaAdi | string | Nokta adı |
| kategori | string | Filtrede kullanılan kategori |
| ilce | string | Filtrede kullanılan ilçe |
| telefon | string | Telefon çeviriciye iletilir |
| aciklama | string | Detay metni |
| webSitesi | string | İsteğe bağlı bağlantı |
| latitude | number/double | Enlem; string olmamalı |
| longitude | number/double | Boylam; string olmamalı |

Gerçek veri yedeği sağlanmadı. Noktalar Admin Panel → Yeni Nokta Ekle ile oluşturulabilir. Test için kendinize ait/temsili bilgiler kullanın; arayüzdeki yer örnekleri doğrulanmış tarım noktası veri seti değildir.

## Sürüm bilgisi

Kaynaktaki sürümler korundu: AGP 9.2.1, Gradle Wrapper 9.4.1, JDK daemon 21, Java kaynak uyumluluğu 11, Firebase BoM 34.13.0, Google Services plugin 4.4.4, Maps 18.2.0 ve Location 21.3.0. Tam sürümler app/build.gradle, gradle/libs.versions.toml ve gradle/wrapper/gradle-wrapper.properties içindedir. Bu listeden bir sürümün yerel ortamda başarıyla çözümlendiği sonucu çıkarılmamalıdır; derleme çalıştırılmadı.

## Sorun giderme

| Belirti | Kontrol |
|---|---|
| google-services.json bulunamadı | Firebase'den indirdiğiniz dosya app/ altında mı? |
| No matching client found | Firebase'deki Android paket adı applicationId ile eşleşiyor mu? |
| Harita döşemeleri gelmiyor | Maps SDK etkin mi, anahtar tanımlı mı, SHA-1/paket adı doğru mu? |
| Firestore permission denied | Kullanıcı oturumu, ilgili users belgesi ve sunucudaki kurallar doğru mu? |
| Admin paneline girilemiyor | Test kullanıcısının users/{uid}.role değeri admin mi? |
| Yakındaki noktalar görünmüyor | Konum izni, konum sağlayıcısı ve kayıtların sayısal koordinatları doğru mu? |
| Gradle/SDK uyuşmazlığı | Daemon JDK 21 ve kaynakta belirtilen SDK/AGP desteği kurulu mu? |

## Resmi başvuru kaynakları

- [Firebase Android kurulumu](https://firebase.google.com/docs/android/setup)
- [Google Services eklentisi ve config dosyası](https://firebase.google.com/docs/android/google-services-plugin-and-file)
- [Google Maps Android başlangıç rehberi](https://developers.google.com/maps/documentation/android-sdk/start)

Bu bağlantılar hizmet kurulumu içindir; projede başarıyla tamamlanmış bulut kurulumu veya cihaz testi kanıtı değildir.
