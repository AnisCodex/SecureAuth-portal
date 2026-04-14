
# 🔐 Spring Boot Login & Registration App

A full-stack **Spring Boot** web application that implements user authentication with **Login & Registration**, using **PostgreSQL**, **Spring Data JPA**, and **Thymeleaf**.

---

## 🚀 Features

* ✅ User Registration with validation
* ✅ User Login authentication
* ✅ Session-based authentication (login/logout)
* ✅ Protected profile page
* ✅ PostgreSQL database integration
* ✅ Clean UI with Thymeleaf
* ✅ MVC architecture (Controller → Service → Repository)

---

## 🛠️ Tech Stack

* **Backend:** Spring Boot, Spring MVC
* **Database:** PostgreSQL
* **ORM:** Spring Data JPA / Hibernate
* **Frontend:** Thymeleaf, HTML, CSS
* **Build Tool:** Maven

---

## 📁 Project Structure

```
SpringBootRegLoginApp/
├── src/main/java/in/sp/
│   ├── controllers/
│   ├── entities/
│   ├── repositories/
│   └── services/
├── src/main/resources/
│   ├── templates/
│   ├── static/
│   └── application.properties
├── pom.xml
```

---

## ⚙️ Configuration

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/reg_login_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## ▶️ How to Run

1. Clone the repository:

```bash
git clone https://github.com/your-username/SpringBootRegLoginApp.git
```

2. Open in IDE (Spring Tool Suite / IntelliJ)

3. Create PostgreSQL database:

```
reg_login_db
```

4. Run the application:

```
SpringBootRegLoginAppApplication.java
```

5. Open browser:

```
http://localhost:8080/
```

---

## 🔐 Authentication Flow

* Register a new user
* Login with credentials
* Session is created
* Access protected profile page
* Logout invalidates session

---

## 💡 Future Improvements

* 🔒 Password encryption (BCrypt)
* 🔑 Spring Security integration
* 🌐 REST API version
* 📱 Responsive UI

---

## 👨‍💻 Author

**Anis Rahman**
Java Full Stack Developer

---

## ⭐ If you like this project

Give it a ⭐ on GitHub!
