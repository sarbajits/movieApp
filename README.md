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
    git clone [https://github.com/your-username/movie-app.git](https://github.com/your-username/movie-app.git)
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

## 📡 API Endpoints

Base URL: `http://localhost:8080`

### 🟢 Public / Auth
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Register a new user |
| `POST` | `/api/v1/auth/login` | Login and receive JWT |
| `POST` | `/forgotPassword/verifyEmail` | Send OTP for password reset |
| `POST` | `/forgotPassword/verifyOtp` | Verify received OTP |

### 🔒 Protected (Requires JWT)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/movie/add` | Add a movie (Multipart: JSON + File) |
| `GET` | `/api/v1/movie/{id}` | Get specific movie details |
| `GET` | `/api/v1/movie/all` | Get all movies (Supports `?page=1&sortBy=name`) |
| `GET` | `/api/v1/movie/pdf` | Download movie list as PDF |
| `POST` | `/file/upload` | Upload a generic file |
| `GET` | `/file/{fileName}` | Serve an uploaded file |

---

## 📄 License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.