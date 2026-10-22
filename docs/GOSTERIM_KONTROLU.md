# İşlevleri gösterme ve ekran görüntüsü alma

Bu liste uygulama sahibi tarafından uygun cihaz/Firebase ortamında uygulanacak kontrol planıdır; tamamlanmış test sonucu değildir.

| Adım | İşlem | Beklenen gözlem |
|---|---|---|
| 1 | app çalıştır | ÇiftçiDestek açılış ekranı ve iki giriş türü |
| 2 | Kullanıcı Girişi → Kayıt Ol | Ad soyad, e-posta ve şifre alanları; geçerli test bilgileriyle Firebase hesabı |
| 3 | Kullanıcı girişi | Haritanın açılması, konum izni isteği |
| 4 | Firestore'da en az iki test noktası bulundur | Haritada işaretçiler |
| 5 | Farklı kategori/ilçe seç; metin ara | Uygun kayıtların gösterilmesi |
| 6 | İşaretçiye dokun | Ad, kategori, ilçe, telefon ve açıklama |
| 7 | Telefon / Web / Yol Tarifi | Uygun dış uygulamanın açılması |
| 8 | Konum gönder | Yakındaki kayıtların kilometre cinsinden sıralanması |
| 9 | Test noktasının ≤100 m yakınına konum gönder | Uygulama içi "Tarım Noktasına Girdiniz" diyaloğu |
| 10 | Yönetici hesabıyla giriş | Liste/arama, yeni nokta, güncelleme ve silme |

Emülatörün konum kontrolünden test noktasının enlem/boylamını verin. İzin ve Google Play Services çalışır olmalı. Fiziksel cihazda konum hataları oluşabileceği için metre sınırında kesin bir doğruluk iddia etmeyin.

Ekran görüntülerini Android Studio emülatörünün ekran yakalama düğmesiyle veya cihazdan alın. Gerçek e-posta/telefon ve başka kişilerin verilerini paylaşmayın. Önerilen adlar: assets/ana-ekran.png, assets/harita.png, assets/admin-panel.png. Bu dosyalar pakette mevcut değildir; oluşturduktan sonra README'ye göreli Markdown görsel bağlantısıyla eklenebilir.

Aynı SDK, uygulama sürümü, test verisi ve konumla adımları tekrar edin; gözlenen başarısızlıkları ve cihaz/Android sürümünü kaydedin. Performans ölçülmedikçe hız/doğruluk yüzdesi eklemeyin.
