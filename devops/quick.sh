#!/usr/bin/env bash

# ---------------------------------------
# quick.sh : Script utilitaire pour les opérations courantes
# Usage: ./quick.sh [command]
# ---------------------------------------

set -euo pipefail

PROJECT_ROOT="/Users/emilien/Desktop/f1-pitmotion-api"
cd "$PROJECT_ROOT"

show_help() {
    echo "🚀 F1 PitMotion API - Script Utilitaire"
    echo ""
    echo "Usage: ./quick.sh [command]"
    echo ""
    echo "Commandes disponibles:"
    echo "  cursor          Ouvrir le projet dans Cursor"
    echo "  rebuild         Reconstruire le container sans cache"
    echo "  up              Démarrer les services Docker"
    echo "  down            Arrêter les services Docker"
    echo "  logs            Voir les logs en temps réel"
    echo "  shell           Entrer dans le container"
    echo "  start           Lancer l'application (profil HTTP)"
    echo "  import          Lancer l'application (profil import)"
    echo "  import-all      Lancer l'import de toutes les données"
    echo "  import-year     Lancer l'import de l'année courante"
    echo "  test            Lancer les tests"
    echo "  compile         Compiler le projet"
    echo "  format          Formater le code"
    echo "  status          Voir l'état des services"
    echo "  clean           Nettoyer les containers et images"
    echo "  help            Afficher cette aide"
    echo ""
}

case "${1:-help}" in
    "cursor")
        echo "🔍 Ouverture du projet dans Cursor..."
        cursor .
        ;;
    
    "rebuild")
        echo "🔄 Reconstruction complète du container sans cache..."
        docker-compose down --rmi all
        docker-compose build --no-cache
        docker-compose up -d
        echo "✅ Reconstruction terminée !"
        ;;
    
    "up")
        echo "🚀 Démarrage des services Docker..."
        docker-compose up -d
        echo "✅ Services démarrés !"
        ;;
    
    "down")
        echo "🛑 Arrêt des services Docker..."
        docker-compose down
        echo "✅ Services arrêtés !"
        ;;
    
    "logs")
        echo "📋 Affichage des logs en temps réel..."
        docker-compose logs -f app
        ;;
    
    "shell")
        echo "🐚 Connexion au container..."
        docker-compose exec app bash
        ;;
    
    "start")
        echo "🏁 Lancement de l'application (profil HTTP)..."
        ./devops/start.sh
        ;;
    
    "import")
        echo "📥 Lancement de l'application (profil import)..."
        ./devops/start.sh --import
        ;;
    
    "import-all")
        echo "📥 Lancement de l'import de toutes les données..."
        ./devops/start.sh --import --all
        ;;
    
    "import-year")
        echo "📥 Lancement de l'import de l'année courante..."
        ./devops/start.sh --import --year
        ;;
    
    "test")
        echo "🧪 Lancement des tests..."
        ./devops/test.sh
        ;;
    
    "compile")
        echo "🔨 Compilation du projet..."
        ./devops/compile.sh
        ;;
    
    "format")
        echo "✨ Formatage du code..."
        ./devops/format.sh
        ;;
    
    "status")
        echo "📊 État des services Docker:"
        docker-compose ps
        echo ""
        echo "📊 Utilisation des ressources:"
        docker stats --no-stream
        ;;
    
    "clean")
        echo "🧹 Nettoyage des containers et images..."
        docker-compose down --rmi all --volumes --remove-orphans
        docker system prune -f
        echo "✅ Nettoyage terminé !"
        ;;
    
    "help"|*)
        show_help
        ;;
esac

