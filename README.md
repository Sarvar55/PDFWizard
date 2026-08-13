# PDF Wizard

PDF Wizard is a Spring Boot REST API for processing, storing, downloading, and
emailing PDFs with JWT authentication, PostgreSQL, and MinIO.

## Main Features

- BCrypt-secured user registration and stateless JWT authorization
- PDF generation from structured JSON data
- Multi-file PDF merging and page-by-page splitting
- Stream-based upload and download boundaries
- PostgreSQL metadata persistence and MinIO object storage
- Asynchronous PDF email dispatch with local Mailpit inspection
- Docker Compose development environment

## Technology Stack

| Area | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 4.1, Spring MVC |
| Security | Spring Security, BCrypt, JJWT |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL 17 |
| PDF processing | Apache PDFBox 3 |
| Object storage | MinIO |
| Email | Spring Mail, Mailpit |
| Testing | JUnit 6, AssertJ, MockMvc, H2 |
| Build and runtime | Maven, Docker, Docker Compose |

## Architecture

The project follows **Hexagonal Architecture**, also known as the
**Ports and Adapters Architecture**. Business logic does not depend directly on
Spring MVC, JPA, MinIO, JWT, PDFBox, or the mail provider.

```text
                         Inbound adapters
                    REST controllers / JWT filter
                                |
                                v
                      Application input ports
                      Commands and queries
                                |
                                v
                 Application services + Domain model
                                |
                                v
                      Application output ports
                                |
              +-----------------+------------------+
              v                 v                  v
          JPA adapters      MinIO adapter    PDFBox/JWT/Mail
                         Outbound adapters
```

### Layers

- `domain`: Entities, value objects, roles, validation rules, and domain errors.
- `application/in`: Use-case contracts plus command and query objects.
- `application/out`: Technology-independent persistence, storage, token, PDF,
  and email ports.
- `application`: `AuthService` and `PdfService`, which coordinate use cases.
- `adapter/in/web`: HTTP request/response models, controllers, exception mapping,
  and JWT authentication filtering.
- `adapter/out`: JPA repositories, MinIO storage, PDFBox processors, JWT signing,
  BCrypt hashing, and email delivery.
- `config`: Dependency wiring, security rules, storage, and executor setup.

Application services depend on interfaces, applying Dependency Inversion and
allowing infrastructure to change without rewriting business rules.

## Data Flow and Streaming

Uploaded files are not immediately converted into a large `byte[]`. A multipart
upload is represented by `UploadedPdf`, which contains a lazy `PdfContentSource`.
That functional interface opens an `InputStream` only when PDFBox needs the data.

Downloads follow the same idea. `StorageService.load` returns an `InputStream`,
`PdfDownloadResult` carries that stream, and the controller exposes it through
Spring's `InputStreamResource`. This avoids loading a stored download entirely
into application memory before sending it to the client.

`byte[]` remains only at bounded transformation points for PDFBox output, MinIO
storage, and mail attachments. Public upload/download boundaries are stream-oriented.

## Design Patterns

- **Ports and Adapters:** Use-case and output interfaces isolate the core.
- **Adapter:** JPA, MinIO, JWT, BCrypt, PDFBox, and mail classes translate external
  APIs into application port contracts.
- **Repository:** Spring Data repositories handle database access; persistence
  adapters expose domain-focused load/save operations.
- **Mapper:** `UserMapper` and `PdfDocumentMapper` keep JPA entities out of the
  domain model.
- **Strategy:** PDF generation, merging, splitting, token handling, storage, and
  password hashing are selected through interfaces.
- **Command/Query separation:** Input models express intent without coupling HTTP
  requests directly to application services.
- **Decorator-style resource wrapping:** `InputStreamResource` decorates an
  `InputStream` with Spring resource behavior, while `ByteArrayResource` wraps
  attachment data for the mail infrastructure.
- **Factory methods and Value Objects:** Domain creation and restoration are
  controlled through methods such as `User.register`, `User.restore`,
  `Password.fromPlainText`, and `Email.of`.

## Requirements

For the containerized setup, install Docker and Docker Compose. For local Java
execution, also install JDK 17 and Maven 3.9+, or use the included Maven Wrapper.

## Quick Start with Docker

Create the environment file:

```bash
cp .env.example .env
```

Create the required local secret files:

```bash
mkdir -p secrets
openssl rand -base64 24 > secrets/postgres_password.txt
openssl rand -base64 24 > secrets/minio_root_password.txt
openssl rand -base64 32 > secrets/jwt_secret.txt
```

Start the complete stack:

```bash
docker compose --profile app up --build -d
```

| Service | Address |
| --- | --- |
| REST API | `http://localhost:8081` |
| PostgreSQL | `localhost:5435` |
| MinIO API | `http://localhost:9000` |
| MinIO Console | `http://localhost:9001` |
| Mailpit UI | `http://localhost:8026` |

```bash
docker compose --profile app logs -f app
docker compose --profile app down
```

Adding `-v` to `docker compose down` also deletes persistent volumes.

## Local Application Execution

Start only the infrastructure services:

```bash
docker compose up -d postgres minio minio-init mailpit
export SPRING_DATASOURCE_PASSWORD="$(tr -d '\n' < secrets/postgres_password.txt)"
export PDF_WIZARD_STORAGE_MINIO_SECRET_KEY="$(tr -d '\n' < secrets/minio_root_password.txt)"
export PDF_WIZARD_AUTH_JWT_SECRET="$(tr -d '\n' < secrets/jwt_secret.txt)"
./mvnw spring-boot:run
```

The local API is available at `http://localhost:8080`.

## REST API

Authentication endpoints are public. Every PDF endpoint requires:

```http
Authorization: Bearer <access-token>
```

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Register a user |
| `POST` | `/api/auth/login` | Create an access token |
| `POST` | `/api/pdf/create` | Generate and store a PDF |
| `GET` | `/api/pdf/{id}` | Read document metadata |
| `GET` | `/api/pdf/{id}/download` | Stream a PDF download |
| `DELETE` | `/api/pdf/{id}` | Delete the object and metadata |
| `POST` | `/api/pdf/merge` | Merge multipart PDF files |
| `POST` | `/api/pdf/split` | Split a multipart PDF file |
| `POST` | `/api/pdf/send-email` | Queue a document email |

## Example Usage

Register and log in:

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@example.com","password":"strong-password"}'

curl -X POST http://localhost:8081/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@example.com","password":"strong-password"}'

export ACCESS_TOKEN='<access-token-from-login-response>'
```

Generate a PDF:

```bash
curl -X POST http://localhost:8081/api/pdf/create \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"fileName":"invoice.pdf","title":"Invoice","data":{"total":1250}}'
```

Merge, split, and download:

```bash
curl -X POST http://localhost:8081/api/pdf/merge \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F 'outputFileName=merged.pdf' -F 'files=@first.pdf' -F 'files=@second.pdf'

curl -X POST http://localhost:8081/api/pdf/split \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F 'outputFileNamePrefix=page' -F 'file=@document.pdf'

curl http://localhost:8081/api/pdf/<document-id>/download \
  -H "Authorization: Bearer $ACCESS_TOKEN" --output document.pdf
```

Queue an email and inspect it at `http://localhost:8026`:

```bash
curl -X POST http://localhost:8081/api/pdf/send-email \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"documentId":"<document-id>","recipient":"recipient@example.com"}'
```

## Tests and Build

```bash
./mvnw test
./mvnw clean package
```

Tests use an in-memory H2 database and test-only JWT/storage configuration. The
packaged application is written to `target/pdf-wizard-0.0.1-SNAPSHOT.jar`.

## Configuration and Secrets

Runtime options are documented in `.env.example`. Database and MinIO passwords,
along with the Base64-encoded JWT key, are loaded through Docker secrets. Never
commit real secret files from the `secrets/` directory.

## License

This project is available under the [MIT License](LICENSE).
