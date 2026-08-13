# PDF Wizard

PDF Wizard; PDF oluşturma, birleştirme, bölme, indirme ve e-posta işlemleri sunan
bir Spring Boot REST API'sidir. JWT, MinIO ve PostgreSQL kullanır.

## Özellikler

- BCrypt parola doğrulamalı kullanıcı kaydı ve JWT güvenliği
- JSON verisinden PDF oluşturma, PDF birleştirme ve sayfalara bölme
- PDF metadata görüntüleme ve dosya indirme
- PDF'i asenkron olarak e-posta ile gönderme
- PostgreSQL metadata persistence ve MinIO object storage
- Mailpit ile yerel e-posta testi
- Docker Compose ile hazır geliştirme ortamı

## Teknolojiler

Java 17, Spring Boot 4.1, Spring Security, JWT, Spring Data JPA, PostgreSQL 17,
Apache PDFBox 3, MinIO, Mailpit, Maven ve Docker Compose.

## Mimari

Proje hexagonal architecture yaklaşımıyla düzenlenmiştir:

```text
adapter/in/web  -> application/in -> application services
                                      |
domain         <----------------------+
                                      |
adapter/out    <- application/out <---+
```

- `domain`: İş kuralları, value object'ler ve domain hataları
- `application/in`: Use-case arayüzleri ve command/query modelleri
- `application/out`: Persistence, storage, token ve e-posta portları
- `adapter/in`: REST controller ve JWT request filtresi
- `adapter/out`: JPA, MinIO, PDFBox, JWT ve mail adaptörleri
- `config`: Spring bean ve güvenlik yapılandırmaları

## Gereksinimler

Docker ile çalıştırmak için:

- Docker
- Docker Compose

Yerel olarak çalıştırmak için ayrıca:

- JDK 17+
- Maven 3.9+ veya projedeki Maven Wrapper
- Çalışan PostgreSQL, MinIO ve SMTP servisi

## Hızlı Başlangıç

Ortam dosyasını oluşturun:

```bash
cp .env.example .env
```

Gerekli secret dosyalarını oluşturun:

```bash
mkdir -p secrets
openssl rand -base64 24 > secrets/postgres_password.txt
openssl rand -base64 24 > secrets/minio_root_password.txt
openssl rand -base64 32 > secrets/jwt_secret.txt
```

Tüm servisleri başlatın:

```bash
docker compose --profile app up --build -d
```

Servis adresleri:

| Servis | Adres |
| --- | --- |
| REST API | `http://localhost:8081` |
| PostgreSQL | `localhost:5435` |
| MinIO API | `http://localhost:9000` |
| MinIO Console | `http://localhost:9001` |
| Mailpit UI | `http://localhost:8026` |

Logları izlemek için:

```bash
docker compose --profile app logs -f app
```

Servisleri durdurmak için:

```bash
docker compose --profile app down
```

Volume'ları da kaldırmak isterseniz komuta `-v` ekleyin. Bu işlem saklanan
verileri siler.

## Yerel Çalıştırma

Bağımlılık servislerini başlatın:

```bash
docker compose up -d postgres minio minio-init mailpit
```

Gerekli environment değerlerini tanımlayıp uygulamayı çalıştırın:

```bash
export SPRING_DATASOURCE_PASSWORD="$(tr -d '\n' < secrets/postgres_password.txt)"
export PDF_WIZARD_STORAGE_MINIO_SECRET_KEY="$(tr -d '\n' < secrets/minio_root_password.txt)"
export PDF_WIZARD_AUTH_JWT_SECRET="$(tr -d '\n' < secrets/jwt_secret.txt)"
./mvnw spring-boot:run
```

Bu yöntemde API `http://localhost:8080` adresinde çalışır.

## API

Kimlik doğrulama endpoint'leri herkese açıktır. Diğer tüm endpoint'lerde şu
header gereklidir:

```http
Authorization: Bearer <access-token>
```

| Method | Endpoint | Açıklama |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Kullanıcı oluşturur |
| `POST` | `/api/auth/login` | Access token üretir |
| `POST` | `/api/pdf/create` | JSON verisinden PDF oluşturur |
| `GET` | `/api/pdf/{id}` | PDF metadata döndürür |
| `GET` | `/api/pdf/{id}/download` | PDF dosyasını indirir |
| `DELETE` | `/api/pdf/{id}` | PDF ve metadatasını siler |
| `POST` | `/api/pdf/merge` | PDF dosyalarını birleştirir |
| `POST` | `/api/pdf/split` | PDF'i sayfalara böler |
| `POST` | `/api/pdf/send-email` | E-posta gönderimini kuyruğa alır |

## Kullanım Örnekleri

Kullanıcı kaydı:

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@example.com","password":"strong-password"}'
```

Giriş:

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@example.com","password":"strong-password"}'
```

Dönen `accessToken` değerini kullanın:

```bash
export ACCESS_TOKEN='<access-token>'
```

PDF oluşturma:

```bash
curl -X POST http://localhost:8081/api/pdf/create \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "fileName":"invoice.pdf",
    "title":"Invoice",
    "data":{"customer":"Ada Lovelace","total":1250}
  }'
```

PDF birleştirme:

```bash
curl -X POST http://localhost:8081/api/pdf/merge \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F 'outputFileName=merged.pdf' \
  -F 'files=@first.pdf;type=application/pdf' \
  -F 'files=@second.pdf;type=application/pdf'
```

PDF bölme:

```bash
curl -X POST http://localhost:8081/api/pdf/split \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F 'outputFileNamePrefix=chapter' \
  -F 'file=@document.pdf;type=application/pdf'
```

PDF indirme:

```bash
curl http://localhost:8081/api/pdf/<document-id>/download \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  --output document.pdf
```

E-posta gönderme:

```bash
curl -X POST http://localhost:8081/api/pdf/send-email \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"documentId":"<document-id>","recipient":"recipient@example.com"}'
```

## Test ve Build

Testleri çalıştırın:

```bash
./mvnw test
```

Uygulama paketini oluşturun:

```bash
./mvnw clean package
```

Oluşan JAR dosyası `target/pdf-wizard-0.0.1-SNAPSHOT.jar` altındadır.

## Yapılandırma

Başlıca environment değişkenleri `.env.example` dosyasında bulunur:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `PDF_WIZARD_AUTH_JWT_ISSUER`
- `PDF_WIZARD_AUTH_JWT_ACCESS_TOKEN_TTL`
- `PDF_WIZARD_STORAGE_MINIO_ENDPOINT`
- `PDF_WIZARD_STORAGE_MINIO_ACCESS_KEY`
- `PDF_WIZARD_STORAGE_MINIO_BUCKET`
- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `PDF_WIZARD_MAIL_FROM`

Parolalar ve JWT anahtarı repoya eklenmemeli; `secrets/` altındaki dosyalardan
Docker secret olarak okunmalıdır.

## Lisans

Bu proje [MIT License](LICENSE) ile lisanslanmıştır.
