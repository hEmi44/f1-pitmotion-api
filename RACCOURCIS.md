# 🚀 Raccourcis F1 PitMotion API

## 📁 Raccourcis Cursor

### Ouvrir le projet dans Cursor
```bash
# Depuis n'importe où
cursor /Users/emilien/Desktop/f1-pitmotion-api

# Ou depuis le dossier du projet
cursor .
```

### Raccourcis Cursor utiles
- `Cmd + Shift + P` : Palette de commandes
- `Cmd + P` : Recherche rapide de fichiers
- `Cmd + Shift + F` : Recherche globale
- `Cmd + \` : Diviser l'éditeur
- `Cmd + J` : Basculer le terminal
- `Cmd + Shift + E` : Explorateur de fichiers

## 🐳 Raccourcis Docker (Sans Cache)

### Reconstruire complètement le container
```bash
# Arrêter et supprimer tous les containers
docker-compose down

# Supprimer les images pour forcer la reconstruction
docker-compose down --rmi all

# Reconstruire sans cache
docker-compose build --no-cache

# Redémarrer les services
docker-compose up -d
```

### Script rapide pour rebuild complet
```bash
# Créer un alias dans votre .zshrc
alias f1-rebuild="cd /Users/emilien/Desktop/f1-pitmotion-api && docker-compose down --rmi all && docker-compose build --no-cache && docker-compose up -d"
```

### Commandes Docker courantes
```bash
# Voir les logs en temps réel
docker-compose logs -f app

# Entrer dans le container
docker-compose exec app bash

# Redémarrer un service spécifique
docker-compose restart app

# Voir l'état des services
docker-compose ps
```

## 🛠️ Raccourcis de Développement

### Scripts disponibles dans devops/
```bash
# Lancer l'application (profil HTTP par défaut)
./devops/start.sh

# Lancer avec import
./devops/start.sh --import

# Lancer avec import de toutes les données
./devops/start.sh --import --all

# Lancer avec import d'une année spécifique
./devops/start.sh --import --year=2024

# Compiler le projet
./devops/compile.sh

# Formater le code
./devops/format.sh

# Lancer les tests
./devops/test.sh

# Installation des dépendances
./devops/install.sh
```

### Commandes Maven directes
```bash
# Compiler
mvn compile

# Tests
mvn test

# Package
mvn package

# Spring Boot run avec profil HTTP
mvn spring-boot:run -Dspring-boot.run.profiles=http

# Spring Boot run avec profil import
mvn spring-boot:run -Dspring-boot.run.profiles=import
```

## 🔧 Alias Shell Recommandés

Ajoutez ces alias dans votre `~/.zshrc` :

```bash
# Navigation rapide
alias f1="cd /Users/emilien/Desktop/f1-pitmotion-api"
alias f1-cursor="cursor /Users/emilien/Desktop/f1-pitmotion-api"

# Docker
alias f1-up="cd /Users/emilien/Desktop/f1-pitmotion-api && docker-compose up -d"
alias f1-down="cd /Users/emilien/Desktop/f1-pitmotion-api && docker-compose down"
alias f1-rebuild="cd /Users/emilien/Desktop/f1-pitmotion-api && docker-compose down --rmi all && docker-compose build --no-cache && docker-compose up -d"
alias f1-logs="cd /Users/emilien/Desktop/f1-pitmotion-api && docker-compose logs -f app"
alias f1-shell="cd /Users/emilien/Desktop/f1-pitmotion-api && docker-compose exec app bash"

# Développement
alias f1-start="cd /Users/emilien/Desktop/f1-pitmotion-api && ./devops/start.sh"
alias f1-import="cd /Users/emilien/Desktop/f1-pitmotion-api && ./devops/start.sh --import"
alias f1-test="cd /Users/emilien/Desktop/f1-pitmotion-api && ./devops/test.sh"
alias f1-compile="cd /Users/emilien/Desktop/f1-pitmotion-api && ./devops/compile.sh"
alias f1-format="cd /Users/emilien/Desktop/f1-pitmotion-api && ./devops/format.sh"
```

## 🚀 Workflow Recommandé

### 1. Ouvrir le projet
```bash
f1-cursor
```

### 2. Démarrer l'environnement
```bash
f1-up
```

### 3. Si problème de cache, rebuild complet
```bash
f1-rebuild
```

### 4. Voir les logs
```bash
f1-logs
```

### 5. Entrer dans le container pour debug
```bash
f1-shell
```

### 6. Lancer l'application
```bash
f1-start
```

## 📝 Notes

- Le container utilise le port configuré dans les variables d'environnement
- Les volumes sont montés pour le développement en temps réel
- PostgreSQL, Kafka et Kafka UI sont inclus dans docker-compose
- Les scripts devops/ sont déjà exécutables et prêts à l'emploi

