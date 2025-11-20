# MovieApp

Lightweight Spring Boot service to manage movies, files and authentication with email/OTP support. Production-ready features include JWT auth, file upload/serve, PDF generation and Docker setup.

## Quick links
- Run entry: [`sarba.movieApp.MovieAppApplication`](src/main/java/sarba/movieApp/MovieAppApplication.java) — [src/main/java/sarba/movieApp/MovieAppApplication.java](src/main/java/sarba/movieApp/MovieAppApplication.java)  
- Main REST controllers:
  - [`sarba.movieApp.controllers.MovieController`](src/main/java/sarba/movieApp/controllers/MovieController.java) — [src/main/java/sarba/movieApp/controllers/MovieController.java](src/main/java/sarba/movieApp/controllers/MovieController.java)  
  - [`sarba.movieApp.controllers.FileController`](src/main/java/sarba/movieApp/controllers/FileController.java) — [src/main/java/sarba/movieApp/controllers/FileController.java](src/main/java/sarba/movieApp/controllers/FileController.java)  
  - [`sarba.movieApp.controllers.ForgotPasswordController`](src/main/java/sarba/movieApp/controllers/ForgotPasswordController.java) — [src/main/java/sarba/movieApp/controllers/ForgotPasswordController.java](src/main/java/sarba/movieApp/controllers/ForgotPasswordController.java)  
  - [`sarba.movieApp.controllers.PdfController`](src/main/java/sarba/movieApp/controllers/PdfController.java) — [src/main/java/sarba/movieApp/controllers/PdfController.java](src/main/java/sarba/movieApp/controllers/PdfController.java)  
- Auth & security:
  - [`sarba.movieApp.auth.config.SecurityConfiguration`](src/main/java/sarba/movieApp/auth/config/SecurityConfiguration.java) — [src/main/java/sarba/movieApp/auth/config/SecurityConfiguration.java](src/main/java/sarba/movieApp/auth/config/SecurityConfiguration.java)  
  - [`sarba.movieApp.auth.services.JwtService`](src/main/java/sarba/movieApp/auth/services/JwtService.java) — [src/main/java/sarba/movieApp/auth/services/JwtService.java](src/main/java/sarba/movieApp/auth/services/JwtService.java)  
- Services & utils:
  - [`sarba.movieApp.service.MovieServiceImpl`](src/main/java/sarba/movieApp/service/MovieServiceImpl.java) — [src/main/java/sarba/movieApp/service/MovieServiceImpl.java](src/main/java/sarba/movieApp/service/MovieServiceImpl.java)  
  - [`sarba.movieApp.service.FileServiceImpl`](src/main/java/sarba/movieApp/service/FileServiceImpl.java) — [src/main/java/sarba/movieApp/service/FileServiceImpl.java](src/main/java/sarba/movieApp/service/FileServiceImpl.java)  
  - [`sarba.movieApp.service.EmailService`](src/main/java/sarba/movieApp/service/EmailService.java) — [src/main/java/sarba/movieApp/service/EmailService.java](src/main/java/sarba/movieApp/service/EmailService.java)  
  - [`sarba.movieApp.utils.EmailTemplateUtils`](src/main/java/sarba/movieApp/utils/EmailTemplateUtils.java) — [src/main/java/sarba/movieApp/utils/EmailTemplateUtils.java](src/main/java/sarba/movieApp/utils/EmailTemplateUtils.java)  
- Templates & config:
  - Email template: [src/main/resources/templates/password-reset-otp-template.html](src/main/resources/templates/password-reset-otp-template.html)  
  - App properties (local): [src/main/resources/app.prop](src/main/resources/app.prop)  
  - Docker: [Dockerfile](Dockerfile), [docker-compose.yml](docker-compose.yml), [.env](.env)

## Features
- CRUD and paginated/sorted listing for movies (poster upload).
- File upload and serve endpoints.
- OTP-based forgot-password flow with HTML email template.
- JWT authentication and refresh-token flow.
- PDF generation (watermark, encryption) and optional email send.
- Docker Compose for local MySQL + app + phpMyAdmin.

## Prerequisites
- Java 21 JDK
- Maven (or use the included wrapper: `./mvnw` / `mvnw.cmd`)
- Docker & Docker Compose (if running containers)

## Build & Run (local)
1. Build:
   - Linux / macOS:
     - ./mvnw clean package -DskipTests
   - Windows (PowerShell/CMD):
     - mvnw.cmd clean package -DskipTests
2. Run JAR:
   - java -jar target/movieApp-0.0.1-SNAPSHOT.jar

## Run with Docker
1. Copy or update credentials in `.env` and ensure `SPRING_MAIL_*` set.
   - [.env](.env)
2. Build and run:
   - docker build -t movieapp:latest .
   - docker-compose up --build -d
3. App URL: http://localhost:8080/

## Environment & config
- Local resource file location: `project.fileLocation` (see [src/main/resources/app.prop](src/main/resources/app.prop)). The app reads this property via `@Value("${project.fileLocation}")` in services such as [`sarba.movieApp.service.MovieServiceImpl`](src/main/java/sarba/movieApp/service/MovieServiceImpl.java).
- SMTP config: set `SPRING_MAIL_USERNAME` and `SPRING_MAIL_PASSWORD` in env or `.env`. The JavaMail usage is in [`sarba.movieApp.service.EmailService`](src/main/java/sarba/movieApp/service/EmailService.java).

## Useful endpoints
- Health / root: GET /
- Movies
  - POST /api/v1/movie/add (multipart: file + movie json) — see [`sarba.movieApp.controllers.MovieController`](src/main/java/sarba/movieApp/controllers/MovieController.java)
  - GET /api/v1/movie/{id}
  - GET /api/v1/movie/all (pagination & sorting)
  - GET /api/v1/movie/pdf (generate PDF)
- File
  - POST /file/upload
  - GET /file/{fileName} — serves uploaded image (see [`sarba.movieApp.controllers.FileController`](src/main/java/sarba/movieApp/controllers/FileController.java))
- Auth / Password flows
  - /api/v1/auth/* (register/login/refresh) — see [`sarba.movieApp.controllers.AuthController`](src/main/java/sarba/movieApp/controllers/AuthController.java)
  - /forgotPassword/* (OTP flow) — see [`sarba.movieApp.controllers.ForgotPasswordController`](src/main/java/sarba/movieApp/controllers/ForgotPasswordController.java)

## Tests
- Unit tests run with Maven:
  - ./mvnw test
  - Test class example: [src/test/java/sarba/movieApp/MovieAppApplicationTests.java](src/test/java/sarba/movieApp/MovieAppApplicationTests.java)

## Key implementation notes
- File upload stores files under `project.fileLocation` and returns a generated filename (see [`sarba.movieApp.service.FileServiceImpl`](src/main/java/sarba/movieApp/service/FileServiceImpl.java)).
- OTP email uses a cached template loader [`sarba.movieApp.utils.EmailTemplateUtils`](src/main/java/sarba/movieApp/utils/EmailTemplateUtils.java) and sends HTML via [`sarba.movieApp.service.EmailService`](src/main/java/sarba/movieApp/service/EmailService.java).
- JWT generation/validation implemented in [`sarba.movieApp.auth.services.JwtService`](src/main/java/sarba/movieApp/auth/services/JwtService.java).
- Global exception handling: [`sarba.movieApp.exceptions.GlobalExceptionHandler`](src/main/java/sarba/movieApp/exceptions/GlobalExceptionHandler.java).

## Files referenced
- [Dockerfile](Dockerfile)  
- [docker-compose.yml](docker-compose.yml)  
- [.env](.env)  
- [src/main/resources/templates/password-reset-otp-template.html](src/main/resources/templates/password-reset-otp-template.html)  
- [src/main/resources/app.prop](src/main/resources/app.prop)

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.