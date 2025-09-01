e # 🏎️ PitMotion API

API RESTful pour gérer et suivre la Formule 1 en temps réel.  
Développée avec **Java 23**, **Spring Boot 3.x**, **PostgreSQL**, **Liquibase** et **Kafka**.

---

## 🚀 Démarrage rapide

### 1. Cloner le dépôt
```bash
git clone https://github.com/hEmi44/f1-pitmotion-api.git
cd f1-pitmotion-api
```

### 2. Configurer l’environnement
Copier le fichier d’exemple et l’adapter :
```bash
cp .env.exemple .env
```
Remplissez les variables avec vos informations (DB, API keys, etc.).

### 3. Lancer les services Docker
Depuis la racine du projet :
```bash
docker compose up -d
```
Cela démarre PostgreSQL, Kafka et les autres services requis.

---

## ⚙️ Lancer l’application

Le projet est piloté par le script **`start.sh`**, qui permet de choisir un profil (http, import, scheduler, queue).

### 🌐 Mode API HTTP
Démarre l’API REST :
```bash
./start.sh --http
```
L’API sera accessible sur [http://localhost:8080](http://localhost:8080).

### 📥 Mode Import
Importe les données depuis la F1 API.

- Import **complet** (toutes saisons) :
  ```bash
  ./start.sh --import --all
  ```

- Import de **l’année courante** :
  ```bash
  ./start.sh --import --year
  ```

- Import d’une **année spécifique** :
  ```bash
  ./start.sh --import --year=2023
  ```

### ⏱️ Mode Scheduler
Lance le scheduler Spring qui synchronise automatiquement la base :
```bash
./start.sh --scheduler
```

### 📡 Mode Queue
Active Kafka pour la gestion des événements et notifications :
```bash
./start.sh --queue
```

---

## 📝 Résumé des commandes

```bash
# Démarrage complet
docker compose up -d

# API HTTP
./start.sh --http

# Import complet
./start.sh --import --all

# Import année spécifique
./start.sh --import --year=2024

# Scheduler
./start.sh --scheduler

# Queue
./start.sh --queue
```

---

## 📚 Documentation

- **Swagger / OpenAPI** : disponible à `/swagger-ui.html` une fois l’API lancée.  
- **Liquibase** : migrations stockées dans `src/main/resources/db/changelog/`.  
- **Kafka** : utilisé pour la gestion des événements (notifications début de session, pronostics).

---

## 👨‍💻 Développement

- Java 23
- Spring Boot 3.x
- PostgreSQL + Liquibase
- Docker / Docker Compose
- Kafka
- Maven

---

## 📌 Objectifs du projet

- Importer automatiquement les données de la Formule 1 (saisons, pilotes, équipes, circuits, résultats).
- Fournir des endpoints REST pour consulter le calendrier, les classements et résultats.
- Offrir un système de **trackers** (GP, pilotes, équipes) avec notifications.
- Proposer un module de **pronostics** entre amis (hors MVP initial).

---

## 📄 Licence

Projet réalisé dans le cadre d’un **Travail de Fin d’Études (TFE)**.  
Auteur : [Emilien Plaitin](https://github.com/hEmi44)
