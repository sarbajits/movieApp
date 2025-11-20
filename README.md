# 🎬 MovieApp Service

A production-ready Spring Boot backend for managing movie data, file handling, and secure authentication. This service features JWT security, PDF generation, dynamic email templating, and full Docker support.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square&logo=spring)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=flat-square&logo=docker)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat-square)

---

## 🚀 Features

### 🔐 Authentication & Security
- **JWT Authentication:** Stateless security using Access and Refresh tokens.
- **Forgot Password Flow:** OTP-based password reset with HTML email templates.
- **Role-Based Access:** Secure endpoints protected by Spring Security.

### 🎥 Movie Management
- **CRUD Operations:** Full management of movie data.
- **Advanced Querying:** Pagination and sorting support for listing movies.
- **Media Support:** Movie poster upload and retrieval.

### 📂 File & Document Handling
- **File System Storage:** Upload and serve images/files locally.
- **PDF Generation:** Generate encrypted, watermarked PDFs dynamically.

### ⚙️ DevOps & Utils
- **Dockerized:** Includes `Dockerfile` and `docker-compose` for easy deployment.
- **Email Service:** Thymeleaf-based HTML email templates.

---

## 🛠️ Tech Stack

- **Core:** Java 21, Spring Boot 3
- **Database:** MySQL (Production), H2 (Testing)
- **Security:** Spring Security, JWT (JJWT)
- **Build Tool:** Maven
- **Containerization:** Docker, Docker Compose

---

## ⚙️ Configuration & Environment Variables

**⚠️ Security Warning:** This project uses sensitive configuration. Create a `.env` file in the root directory using the keys below. **Never commit your `.env` file to Git.**

| Variable Key | Description | Example Value |
| :--- | :--- | :--- |
| `SPRING_MAIL_USERNAME` | SMTP Email Address | `admin@gmail.com` |
| `SPRING_MAIL_PASSWORD` | SMTP App Password | `xxxx-xxxx-xxxx-xxxx` |
| `MYSQL_DB_USERNAME` | Database User | `root` |
| `MYSQL_DB_PASSWORD` | Database Password | `password` |
| `JWT_SECRET` | Secret key for signing tokens | `SuperSecretKey123...` |
| `PROJECT_FILE_LOCATION` | Local folder path for uploads | `user/photos/` |

---

## 🏃‍♂️ Getting Started

### Prerequisites
- Java 21 JDK
- Maven
- Docker (Optional, for containerized run)

### Option 1: Run Locally (Manual)

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/sarbajits/movie-app.git](https://github.com/sarbajits/movie-app.git)
    ```
2.  **Configure Database:** Ensure MySQL is running or update `app.prop` to use H2.
3.  **Build and Run:**
    ```bash
    # Linux / Mac
    ./mvnw clean package -DskipTests
    java -jar target/movieApp-0.0.1-SNAPSHOT.jar

    # Windows
    mvnw.cmd clean package -DskipTests
    java -jar target/movieApp-0.0.1-SNAPSHOT.jar
    ```

### Option 2: Run with Docker (Recommended)

1.  Ensure your `.env` file is created with the correct credentials.
2.  Run the compose command:
    ```bash
    docker-compose up --build -d
    ```
    *This spins up the App, MySQL, and phpMyAdmin containers.*

---
## 🧭 API Reference

The endpoints are categorized below for easy reference.

### 1. 🍿 Movie Management (`/api/v1/movie`)

Endpoints for creating, retrieving, updating, and deleting movie records. Includes dedicated endpoints for pagination, sorting, and reporting.

| Method | Path | Summary | Parameters/Body | Response Schema |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/movie/add` | Add a new movie. | `file` (binary), `movieDtoObj` (string) | `MovieDto` |
| **POST** | `/api/v1/movie/edit` | Edit an existing movie. | `file` (binary), `movieDtoObj` (string) | `MovieDto` |
| **GET** | `/api/v1/movie/{id}` | Get a movie by ID. | `id` (path, integer) | `MovieDto` |
| **GET** | `/api/v1/movie/delete/{id}` | Delete a movie by ID. | `id` (path, integer) | `boolean` |
| **GET** | `/api/v1/movie/all` | Get all movies with pagination and sorting. | `pageNumber`, `pageSize`, `sortBy`, `sortOrder` (query) | `MoviePageResponse` |
| **GET** | `/api/v1/movie/allByPage` | Get all movies with basic pagination. | `pageNumber`, `pageSize` (query) | `MoviePageResponse` |
| **GET** | `/api/v1/movie/all-old` | Get all movies without pagination. | None | Array of `MovieDto` |
| **GET** | `/api/v1/movie/pdf` | Generate PDF of movies (with pagination/sorting). | `pageNumber`, `pageSize`, `sortBy`, `sortOrder`, `email` (query) | `application/pdf` (byte array) |

### 2. 🔐 Authentication (`/api/v1/auth`)

Endpoints for user registration, login, and token management.

| Method | Path | Summary | Request Body | Response Schema |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/auth/register` | Register a new user. | `RegisterRequest` | `AuthResponse` |
| **POST** | `/api/v1/auth/login` | Log in and obtain access tokens. | `LoginRequest` | `AuthResponse` |
| **POST** | `/api/v1/auth/refresh` | Refresh access token using a refresh token. | `RefreshTokenRequest` | `AuthResponse` |

### 3. 🔑 Password & Account Reset

Endpoints for handling password changes and the recovery flow.

| Method | Path | Summary | Parameters/Body |
| :--- | :--- | :--- | :--- |
| **POST** | `/resetPassword/{email}` | Change password for a logged-in user. | `email` (path), `ResetPassword` (body) |
| **POST** | `/forgotPassword/verifyMail/{email}` | Send an OTP for password reset. | `email` (path) |
| **POST** | `/forgotPassword/verifyOtp/{email}/{otp}` | Verify the received OTP. | `email` (path), `otp` (path, integer) |
| **POST** | `/forgotPassword/changePassword/{email}/{otp}` | Set a new password after successful OTP verification. | `email`, `otp` (path), `ChangePassword` (body) |

### 4. 🖼 File & Utility

Endpoints for file storage, simple checks, and external services.

| Method | Path | Summary | Parameters/Body | Response Content |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/file/upload` | Upload a file. | `file` (binary) | `string` (file URL/ID) |
| **GET** | `/file/{fileName}` | Serve (retrieve) a file by name. | `fileName` (path) | File content |
| **GET** | `/pdf/send/{email}/{text}` | Send a custom PDF via email. | `email`, `text` (path) | `byte` array (file content) |
| **GET** | `/api/v1/movie/debug` | Debug endpoint (health check/auth test). | None | `string` |
| **GET** | `/api/v1/movie/mail` | Test mail sending functionality. | `MailBody` (body) | OK |
| **GET** | `/` | Basic server health check. | None | `string` |

---

## 📄 License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.