<h1 align="center"> 📚 BookHub — Back-end ! 📚 </h1>

# 📄 A propos :

Ce dépôt contient le back-end de l'application BookHub, développé avec Java Spring Boot.
Il expose une API REST sécurisée par JWT, consommée par le front-end Angular.

# 💻 Stack Technique :

![Java](https://img.shields.io/badge/-Java-FF6D49?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-FF6D49?style=flat&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/-Spring%20Security-FF6D49?style=flat&logo=springsecurity&logoColor=white)
![SQL Server](https://img.shields.io/badge/-SQL%20Server-FF6D49?style=flat&logo=microsoftsqlserver&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-FF6D49?style=flat&logo=swagger&logoColor=white)
![GIT](https://img.shields.io/badge/-Git-FF6D49?style=flat&logo=git&logoColor=white)
![GITHUB](https://img.shields.io/badge/-GitHub-FF6D49?style=flat&logo=github&logoColor=white)

# 👨‍🔧 Installation :

### Prérequis
- Java 25
- SQL Server
- Gradle

### Lancement

En attendant la mise en place d'une base de données embarquée, le projet utilise SQL Server.
Avant de lancer l'application, exécutez manuellement les scripts SQL disponibles sur le [sharepoint](https://campuseni-my.sharepoint.com/personal/emma_baudin2024_campus-eni_fr/_layouts/15/onedrive.aspx?id=%2Fpersonal%2Femma%5Fbaudin2024%5Fcampus%2Deni%5Ffr%2FDocuments%2FBOOKHUB%2F01%20%2D%20CONCEPTION%2FScript%20BDD&viewid=c08f0a1f%2D0471%2D40ae%2Dbc54%2Df00843c7d760&sharingv2=true&fromShare=true&at=9&CT=1778488230891&OR=OWA%2DNT%2DMail&CID=41aaed58%2D3805%2D0741%2Dd7d0%2D4e178b683a97&FolderCTID=0x012000B3408ECBF3FF014B9A49BDE7BED4E29E&TeamsCID=451c6bfb%2Dce2a%2D4baf%2D82c2%2D19e6751770e8).

L'API sera disponible sur `http://localhost:8080`.

# 🔚 Endpoints :

### 🔐 Authentification
| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| POST | `/api/auth/register` | — | Inscription d'un nouvel utilisateur |
| POST | `/api/auth/login` | — | Connexion et génération du token JWT |

### 📚 Livres
| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| GET | `/api/books` | Authentifié | Liste complète des livres |
| GET | `/api/books/search` | Authentifié | Recherche filtrée et paginée |
| GET | `/api/books/{id}` | Authentifié | Détail d'un livre |
| POST | `/api/books` | LIBRARIAN / ADMIN | Création d'un livre |
| PUT | `/api/books/{isbn}` | LIBRARIAN / ADMIN | Modification d'un livre |
| DELETE | `/api/books/{isbn}` | LIBRARIAN / ADMIN | Suppression d'un livre |

### 📖 Emprunts
| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| GET | `/api/loans/my/{userId}` | Authentifié | Liste des emprunts d'un utilisateur |
| GET | `/api/loans` | LIBRARIAN / ADMIN | Liste de tous les emprunts |
| GET | `/api/loans/{id}` | LIBRARIAN / ADMIN | Détail d'un emprunt |
| POST | `/api/loans` | LIBRARIAN / ADMIN | Création / validation d'un emprunt |
| POST | `/api/loans/{id}/return` | LIBRARIAN / ADMIN | Enregistrement du retour d'un livre |

### 🔖 Réservations
| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| GET | `/api/reservations/my` | Authentifié | Liste des réservations de l'utilisateur |
| POST | `/api/reservations` | Authentifié | Création d'une réservation |
| DELETE | `/api/reservations/{id}` | Authentifié | Annulation d'une réservation |
| GET | `/api/reservations` | LIBRARIAN / ADMIN | Liste de toutes les réservations |
| GET | `/api/reservations/{id}` | LIBRARIAN / ADMIN | Détail d'une réservation |
| POST | `/api/reservations/{id}/validate` | LIBRARIAN / ADMIN | Validation d'une réservation |

### ✍️ Auteurs
| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| GET | `/api/auteurs` | LIBRARIAN / ADMIN | Liste des auteurs |
| POST | `/api/auteurs` | LIBRARIAN / ADMIN | Création d'un auteur |
| DELETE | `/api/auteurs/{id}` | LIBRARIAN / ADMIN | Suppression d'un auteur |

### 👤 Utilisateurs
| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| GET | `/api/users/{id}` | Authentifié | Consultation du profil utilisateur |
| PUT | `/api/users/{id}` | Authentifié | Modification du profil utilisateur |
| PUT | `/api/users/{id}/password` | Authentifié | Modification du mot de passe |
| GET | `/api/users` | LIBRARIAN / ADMIN | Liste des utilisateurs |
| DELETE | `/api/users/{id}` | LIBRARIAN / ADMIN | Suppression d'un utilisateur |

# 📎 Liens utiles :

* [Front-end du projet](https://github.com/fanny-pretre/bookhub-frontend/)
* [Swagger / documentation API](http://localhost:8080/swagger-ui/index.html)
* [Dossier de conception](https://campuseni-my.sharepoint.com/:w:/r/personal/emma_baudin2024_campus-eni_fr/_layouts/15/Doc.aspx?sourcedoc=%7B14782B95-3094-4926-8251-4B831BDB1D46%7D&file=%F0%9F%93%98%20Document%20de%20conception%20%E2%80%94%20BookHub.docx&action=default&mobileredirect=true)
* [Dossier de choix techniques](https://campuseni-my.sharepoint.com/:w:/r/personal/emma_baudin2024_campus-eni_fr/_layouts/15/Doc.aspx?sourcedoc=%7B0DB1C78C-5096-41E1-8BA6-6C6157460280%7D&file=BookHub-%20Choix%20Techniques.docx&action=default&mobileredirect=true)
* [Manuel utilisateur](https://campuseni-my.sharepoint.com/:w:/r/personal/emma_baudin2024_campus-eni_fr/_layouts/15/Doc.aspx?sourcedoc=%7BBE889983-9FDE-4A31-9C32-6323DAB25ED4%7D&file=Manuel_utilisateur_BookHub.docx&action=default&mobileredirect=true)
* [Cahier de la gestion de projet](https://campuseni-my.sharepoint.com/:w:/r/personal/emma_baudin2024_campus-eni_fr/_layouts/15/Doc.aspx?sourcedoc=%7B1BF07D8A-65B1-4BF9-9F1C-186C74DD7BB8%7D&file=Gestion_de_projet_BookHub.docx&action=default&mobileredirect=true)

# ©️ Remerciements :

BookHub est un projet réalisé dans le cadre d'une formation en développement web à l'ENI de Niort par l'équipe de la Poule Request Agency.
* [Emma Baudin](https://www.linkedin.com/in/emmbdn)
* [Agathe Perrin](https://fr.linkedin.com/in/agathe-perrin)
* [Fanny Prêtre](https://fr.linkedin.com/in/fanny-pr%C3%AAtre)
* [Sofia Renault-Shlyapnikova](https://www.linkedin.com/in/sofia-renault-shlyapnikova?utm_source=share_via&utm_content=profile&utm_medium=member_ios)

Merci à toute l'équipe pédagogique pour son accompagnement tout au long du projet (**Souheil Sultan**).
