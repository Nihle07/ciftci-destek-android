# GitHub'a adım adım yükleme

1. Hazırlanan ciftci-destek-github.zip dosyasını indir → sağ tık → Tümünü ayıkla.
2. Ayıklanan klasörü aç. İçinde doğrudan README.md, app, gradle, docs gibi dosyalar görmelisin.
3. GitHub sağ üst + → New repository. Ad önerisi: ciftci-destek-android.
4. Description: Firebase ve Google Maps tabanlı tarım destek noktaları Android uygulaması.
5. İstediğin görünürlüğü seç; otomatik README/gitignore/lisans oluşturmayı açma, pakette ilgili belgeler hazır. Create repository.
6. Quick setup altındaki uploading an existing file bağlantısına tıkla. Mevcut depoda Add file → Upload files.
7. Ayıklanan klasörün İÇİNDE Ctrl+A ile bütün dosya ve klasörleri seçip birlikte sürükle. ZIP'i veya dış klasörün kendisini yükleme.
8. Commit mesajı: Add original Android project and documentation. Commit changes.
9. Ana sayfada README açıklaması ve proje görseli görünmeli. README.md ana dizinde, app/ ve gradle/ yanında olmalı.

ZIP dosyasının içinde ek bir proje dış klasörü bulunmaz; bu, yanlışlıkla ikinci bir alt klasör oluşturmamanı kolaylaştırır.

Bulut ayarlarını bu temiz paketi yükledikten sonra bilgisayarında yap: kendi app/google-services.json dosyanı ve local.properties içine Maps anahtarını ekle. Bu iki yerel dosyayı tarayıcıdan elle seçip GitHub'a yükleme; tarayıcıdaki elle yükleme .gitignore'u uygulamaz.

gradlew dosyasının çalıştırma izni web yüklemesinde korunmayabilir; macOS/Linux için README'deki chmod +x gradlew komutunu kullan.

Var olan başka bir projeye ekleme. Yanlışlıkla dış klasör yüklediysen önce yedeğini al, yalnızca o dış klasörü kaldırıp içindekileri depo köküne yeniden yükle. Mevcut depodaki farklı çalışmalarını silme.
