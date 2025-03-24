# Yoga App !



# 🧘 Yoga App

A complete web application — from backend to frontend — to manage yoga class reservations!

---

## 🛠️ Technologies

### Back-end
![Java](https://img.shields.io/badge/Java-17.0.8-orange)  
![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.6.1-green)  
![Maven](https://img.shields.io/badge/Maven-3.9.9-pink)  
![JUnit](https://img.shields.io/badge/Junit-5.12.0-red)

---

## 🚀 Getting Started

### 1. Clone the project

```bash
git clone https://github.com/MickC0/FS-P5-Savasana.git
```

---

### 2. Database (MySQL)

- Install and start mysql
- Open a terminal in `/YogaApp/ressources/sql`
- Connect to mysql with root user
- Execute :
```sql
SOURCE reset_db.sql;
CREATE USER 'TheUsername'@'%' IDENTIFIED BY 'ThePassword';
GRANT ALL ON yogadb.* TO 'TheUsername'@'%';
```
📌 The username and password must be the same as in the `application.properties`

---

### 3. Start backend

```bash
cd YogaApp/back
```

Set MySQL username and password dans :  
`src/main/resources/application.properties`

#### Start in development mode :
```bash
mvn spring-boot:run
```

#### Build and execute the jar :
```bash
mvn package
java -jar target/yoga-app-0.0.1-SNAPSHOT.jar
```

## ✅ Tests Back-end

📁 Folder : `/YogaApp/back`

### Tests unitaires & d'intégration (JUnit)

```bash
mvn clean verify
```

📄 Unit tests report JaCoCo : `/target/site/jacoco-ut-coverage-report/index.html`

📸 ![JUnit Coverage Report](../ressources/coverage-report/Backend_tests-unitaires.png)


📄 Integration tests report JaCoCo : `/target/site/jacoco-it-coverage-report/index.html`

📸 ![JUnit Coverage Report](../ressources/coverage-report/Backend_tests-integration.png)

---

## 📦 Autres ressources

### Postman

Postman collection :  
`/YogaApp/ressources/postman/yoga.postman_collection.json`

👉 [Import in Postman](https://learning.postman.com/docs/getting-started/importing-and-exporting/importing-and-exporting-overview/)

---

## 📎 Contributors

Me

