# 📚 BookHub - Backend Spring Boot

API REST de la plateforme de gestion de bibliothèque communautaire **BookHub**.  
Développée avec Spring Boot 3.2+ dans le cadre du projet DEV25_0364B.

---

## 🛠️ Stack technique

| Technologie | Version |
|---|---|
| Java | 25.0.3 |
| Spring Boot | 4.6.0 |
| Spring Security + JWT | dépend des dépendances du projet |
| Spring Data JPA | dépend des dépendances du projet |
| SQL Server | 2019+ |
| Gradle | dépend des dépendances du wrapper |
| Swagger / OpenAPI | 3.x |
| JUnit | 5.x |

---

## 📋 Prérequis

- [JDK 17+](https://adoptium.net/)
- [Gradle](https://gradle.org/) (ou utiliser le wrapper `./gradlew`)
- [SQL Server 2019+](https://www.microsoft.com/fr-fr/sql-server/)

---

## 🚀 Installation et lancement

### 1. Cloner le dépôt

```bash
git clone https://github.com/fanny-pretre/bookhub-backend.git
cd bookhub-backend
```

### 2. Configurer la base de données

Créer une base de données SQL Server nommée `bookhub`, puis exécuter les scripts dans l'ordre :

```bash
sql/
├── 01_create_tables.sql
├── 02_constraints.sql
├── 03_stored_procedures.sql
├── 04_triggers.sql
└── 05_data_test.sql
```

### 3. Configurer l'application

Copier le fichier de configuration et renseigner vos valeurs :

```bash
cp src/main/resources/application.example.properties src/main/resources/application.properties
```

```properties
# application.properties

# Base de données
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=bookhub
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

# JWT
jwt.secret=YOUR_SECRET_KEY_256_BITS_MINIMUM
jwt.expiration=86400000

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

> ⚠️ Ne jamais committer `application.properties`, il est dans le `.gitignore`.

### 4. Lancer l'application

```bash
./gradlew bootRun
```

L'API est accessible sur **http://localhost:8080**  
La documentation Swagger est disponible sur **http://localhost:8080/swagger-ui.html**

---

## 🏗️ Structure du projet

```
src/main/java/com/bookhub/
├── config/                # Configuration Spring Security, JWT, CORS
├── controller/            # Contrôleurs REST
│   ├── AuthController
│   ├── BookController
│   ├── LoanController
│   ├── ReservationController
│   └── RatingController
├── service/               # Logique métier
├── repository/            # Interfaces Spring Data JPA
├── entity/                # Entités JPA (User, Book, Loan, Reservation, Rating)
├── dto/                   # Objets de transfert (Request / Response)
├── exception/             # Gestion centralisée des erreurs
└── security/              # JwtUtil, JwtFilter, UserDetailsServiceImpl
```

---

## 🔌 Endpoints API

| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Inscription |
| POST | `/api/auth/login` | Public | Connexion (retourne JWT) |
| GET | `/api/books` | Public | Liste paginée des livres |
| GET | `/api/books/{id}` | Public | Détail d'un livre |
| GET | `/api/books/search` | Public | Recherche par titre / auteur / ISBN |
| POST | `/api/books` | LIBRARIAN | Ajouter un livre |
| PUT | `/api/books/{id}` | LIBRARIAN | Modifier un livre |
| DELETE | `/api/books/{id}` | ADMIN | Supprimer un livre |
| POST | `/api/loans` | USER | Emprunter un livre |
| GET | `/api/loans/my` | USER | Mes emprunts |
| GET | `/api/loans` | LIBRARIAN | Tous les emprunts |
| PUT | `/api/loans/{id}/return` | LIBRARIAN | Enregistrer un retour |
| POST | `/api/reservations` | USER | Réserver un livre |
| GET | `/api/reservations/my` | USER | Mes réservations |
| DELETE | `/api/reservations/{id}` | USER | Annuler une réservation |
| POST | `/api/books/{id}/ratings` | USER | Noter / commenter un livre |
| PUT | `/api/ratings/{id}` | USER | Modifier sa note |
| DELETE | `/api/ratings/{id}` | LIBRARIAN | Supprimer un commentaire |

Codes HTTP utilisés : `200`, `201`, `204`, `400`, `401`, `403`, `404`

---

## 🔐 Sécurité

- Mots de passe hachés avec **BCrypt**
- Tokens **JWT HS256**, expiration 24h
- Protection **CSRF**, configuration **CORS** restrictive
- Validation des entrées côté serveur avec `@Valid` + Jakarta Validation
- Requêtes **paramétrées via JPA** (protection injection SQL)
- Conformité **RGPD** : droit d'accès, rectification, suppression de compte

---

## 🧪 Tests

```bash
# Lancer les tests unitaires
./gradlew test

# Avec rapport de couverture (JaCoCo)
./gradlew test jacocoTestReport
```

Le rapport HTML est généré dans `build/reports/jacoco/test/html/index.html`.

> Couverture minimale attendue : **20%**

---

## 👥 Équipe

| Nom | Rôle |
|---|---|
| Fanny | — |
| Emma | — |
| Sofia | — |
| Agathe | — |

---

## 📄 Licence

Projet réalisé dans le cadre de la formation **CDA** - ENI École Informatique.
