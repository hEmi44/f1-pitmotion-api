#!/usr/bin/env bash

# ---------------------------------------
# start.sh : lance l'application avec le bon profil et options d'import
# Usage:
#   ./start.sh [--import] [--all] [--year] [--year=YYYY]
# ---------------------------------------

set -euo pipefail

PROFILE="http"
YEAR_ARG=""
ALL_FLAG=""
YEAR_SET=false
ALL_SET=false

CURRENT_YEAR=$(date +%Y)

for arg in "$@"; do
  case "$arg" in
    --import|--queue|--scheduler)
      PROFILE="${arg#--}"
      ;;
    --all)
      ALL_FLAG="--all"
      ALL_SET=true
      ;;
    --year=*)
      YEAR_ARG="$arg"
      YEAR_SET=true
      ;;
    --year)
      YEAR_ARG="--year=$CURRENT_YEAR"
      YEAR_SET=true
      ;;
    *)
      echo "Argument inconnu : $arg" >&2
      exit 1
      ;;
  esac
done

if [[ "$PROFILE" != "import" ]]; then
  if $ALL_SET; then
    echo "L'option --all n'est valide qu'avec --import, elle sera ignorée." >&2
    ALL_FLAG=""
  fi
  if $YEAR_SET; then
    echo "L'option --year n'est valide qu'avec --import, elle sera ignorée." >&2
    YEAR_ARG=""
  fi
fi

ARGS=()
if [[ "$PROFILE" == "import" ]]; then
  if [[ -n "$ALL_FLAG" ]]; then
    ARGS+=("$ALL_FLAG")
  elif [[ -n "$YEAR_ARG" ]]; then
    ARGS+=("$YEAR_ARG")
  fi
fi

MAVEN_PROFILES="-Dspring-boot.run.profiles=$PROFILE"
if [[ "$PROFILE" == "import" ]]; then
  MAVEN_ARGS="-Dspring-boot.run.arguments=${ARGS[*]}"
else
  MAVEN_ARGS=""
fi

eval mvn spring-boot:run $MAVEN_PROFILES $MAVEN_ARGS
