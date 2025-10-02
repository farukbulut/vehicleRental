# Araç Kiralama Sistemi (Vehicle Rental System)

## 📋 Proje Hakkında

Bu proje, modern yazılım geliştirme prensiplerini merkeze alarak geliştirilmiş terminal tabanlı bir araç kiralama uygulamasıdır. Katmanlı mimari yaklaşımı ile esneklik, ölçeklenebilirlik ve bakım kolaylığı sağlar.

## 🏗️ Mimari Yapı

Proje **Katmanlı Mimari (Layered Architecture)** prensibine göre tasarlanmıştır:

```
com.carrental/
├── model/          # Veri modelleri ve iş mantığı
│   ├── enums/      # Enum tanımları
│   └── ...
├── dao/            # Veritabanı erişim katmanı
├── service/        # İş kuralları ve servis katmanı
├── ui/             # Kullanıcı arayüzü katmanı
├── util/           # Yardımcı sınıflar
├── exception/      # Özel exception sınıfları
└── Main.java       # Uygulama giriş noktası
```

### Katmanlar ve Sorumluluklar

1. **Model Katmanı**: Veri modellerini ve temel iş mantığını içerir
2. **DAO Katmanı**: Veritabanı işlemlerini yönetir (CRUD)
3. **Service Katmanı**: İş kurallarını uygular ve transaction yönetimi yapar
4. **UI Katmanı**: Kullanıcı etkileşimini yönetir
5. **Utility Katmanı**: Yardımcı fonksiyonlar (şifre hashleme, validasyon)
6. **Exception Katmanı**: Özel hata yönetimi

## ✨ Özellikler

### Kimlik Doğrulama ve Yetkilendirme
- ✅ E-posta ve şifre ile giriş/kayıt
- ✅ SHA-256 algoritması ile şifre hash'leme
- ✅ Rol tabanlı yetkilendirme (ADMIN, CORPORATE, INDIVIDUAL)

### Araç Yönetimi
- ✅ Üç farklı araç tipi: Otomobil, Helikopter, Motosiklet
- ✅ Araç CRUD işlemleri (Admin)
- ✅ Araç arama ve filtreleme
- ✅ Sayfalama (Pagination) desteği
- ✅ Her araç tipi için farklı fiyat tarifeleri

### Kiralama Sistemi
- ✅ Saatlik, günlük, haftalık, aylık kiralama
- ✅ Otomatik fiyat hesaplama
- ✅ Tarih çakışma kontrolü
- ✅ Depozito yönetimi
- ✅ Kiralama iptali ve iade işlemleri
- ✅ Transaction yönetimi (Atomicity)

### İş Kuralları
- ✅ Kurumsal müşteriler minimum 1 ay kiralama yapmalı
- ✅ 2.000.000 TL üzeri araçlar için:
  - Minimum 30 yaş şartı
  - %10 depozito gerekliliği
- ✅ Tarih ve saat çakışma kontrolü
- ✅ Girdi validasyonu

### Teknik Özellikler
- ✅ PostgreSQL veritabanı entegrasyonu
- ✅ JDBC ile veritabanı işlemleri
- ✅ Transaction yönetimi (Commit/Rollback)
- ✅ Exception handling
- ✅ Seed data (Örnek veriler)

## 🛠️ Teknolojiler

- **Java**: 21
- **Veritabanı**: PostgreSQL 16
- **Build Tool**: Maven
- **JDBC Driver**: PostgreSQL JDBC 42.7.1

## 📦 Kurulum

### Gereksinimler
- Java JDK 21 veya üzeri
- PostgreSQL 16 veya üzeri
- Maven 3.6 veya üzeri

### Adımlar

1. **Projeyi klonlayın**:
```bash
git clone <repository-url>
cd vehicleRental
```

2. **PostgreSQL veritabanını oluşturun**:
```sql
CREATE DATABASE vehiclerental;
```

3. **Veritabanı şemasını ve seed data'yı yükleyin**:
```bash
psql -U postgres -d vehiclerental -f src/main/java/com/carrental/sql/db.sql
```

4. **Veritabanı bağlantı ayarlarını güncelleyin**:
`src/main/java/com/carrental/dao/DatabaseConnection.java` dosyasında:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/vehiclerental";
private static final String USER = "postgres";
private static final String PASSWORD = "your_password";
```

5. **Maven bağımlılıklarını yükleyin**:
```bash
mvn clean install
```

6. **Uygulamayı çalıştırın**:
```bash
mvn exec:java -Dexec.mainClass="com.carrental.Main"
```

## 👥 Varsayılan Kullanıcılar

Sistem, test için hazır kullanıcılarla birlikte gelir (Şifre: `password123`):

| E-posta | Şifre | Rol | Açıklama |
|---------|-------|-----|----------|
| admin@vehiclerental.com | password123 | ADMIN | Tam sistem erişimi |
| corporate@company.com | password123 | CORPORATE | Kurumsal müşteri |
| individual@email.com | password123 | INDIVIDUAL | Bireysel müşteri |

## 🎯 Kullanım

### Admin Kullanıcısı
1. Admin hesabıyla giriş yapın
2. Araç ekleme, güncelleme, silme, listeleme işlemleri yapabilirsiniz
3. Tüm kiralamaları görüntüleyebilirsiniz

### Müşteri Kullanıcısı (Individual/Corporate)
1. Müşteri hesabıyla giriş yapın
2. Müsait araçları arayın ve görüntüleyin
3. Araç kiralayın
4. Kiralamalarınızı görüntüleyin
5. Kiralamalarınızı iptal edin

## 📊 Veritabanı Şeması

### Tablolar
- **users**: Kullanıcı bilgileri
- **vehicles**: Araç bilgileri
- **rentals**: Kiralama kayıtları
- **deposits**: Depozito kayıtları

### İlişkiler
- User (1) → (N) Rental
- Vehicle (1) → (N) Rental
- Rental (1) → (1) Deposit

## 🧪 İş Kuralları Örnekleri

### Kurumsal Müşteri Kuralı
```java
// Kurumsal müşteriler minimum 1 ay kiralama yapmalı
if (user.getRole() == UserRole.CORPORATE) {
    if (pricingType != PricingType.MONTHLY || duration < 1) {
        throw new BusinessRuleException("Kurumsal müşteriler en az 1 ay kiralama yapmalıdır");
    }
}
```

### Yüksek Değerli Araç Kuralı
```java
// 2.000.000 TL üzeri araçlar için 30 yaş ve depozito şartı
if (vehicle.getValueTl().compareTo(new BigDecimal("2000000")) > 0) {
    if (user.getAge() < 30) {
        throw new BusinessRuleException("Bu araç için minimum 30 yaş gereklidir");
    }
    // %10 depozito hesaplanır ve uygulanır
}
```

## 🔒 Güvenlik

- Şifreler SHA-256 algoritması ile hash'lenerek saklanır
- Şifre doğrulaması hash karşılaştırması ile yapılır
- Girdi validasyonu tüm kullanıcı girdilerinde uygulanır
- SQL Injection koruması (PreparedStatement kullanımı)

## 🔄 Transaction Yönetimi

Kiralama işlemleri transaction içinde yönetilir:

```java
try {
    connection.setAutoCommit(false);
    
    // 1. Kiralama kaydı oluştur
    // 2. Depozito kaydı oluştur (gerekirse)
    // 3. Araç durumunu güncelle
    
    connection.commit();
} catch (Exception e) {
    connection.rollback();
    throw e;
}
```

## 📝 Kod Standartları

- ✅ Anlaşılır ve açıklayıcı değişken/fonksiyon isimleri
- ✅ Java naming conventions
- ✅ Kapsamlı JavaDoc yorumları
- ✅ Tutarlı kod formatı ve girinti
- ✅ SOLID prensipleri
- ✅ DRY (Don't Repeat Yourself) prensibi

## 🚀 Geliştirme İpuçları

### Yeni Araç Tipi Ekleme
1. `VehicleType` enum'ına yeni tip ekleyin
2. `Vehicle` soyut sınıfından türeyen yeni sınıf oluşturun
3. `VehicleDAO.mapResultSetToVehicle()` metodunu güncelleyin
4. Fiyatlandırma metodlarını implement edin

### Yeni İş Kuralı Ekleme
1. `BusinessRules` sınıfına yeni validation metodu ekleyin
2. İlgili servis katmanında kuralı uygulayın
3. Uygun exception fırlatın

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun