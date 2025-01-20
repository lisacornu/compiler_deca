#!/bin/bash

# remonte à la racine du projet
while [ ! -f pom.xml ] && [ "$PWD" != "/" ]; do
    cd ..
done

# Couleurs pour l'affichage
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Compteurs pour les statistiques
total_tests=0
passed_tests=0
failed_tests=0

# Fonction pour tester un fichier
test_file() {
    local file="$1"
    total_tests=$((total_tests + 1))

    # Créer un fichier temporaire et y stocker la sortie de decac -p
    temp_file=$(mktemp)
    ./src/main/bin/decac -p "$file" > "$temp_file" 2>/dev/null

    # Comparer la sortie directe de decac -p avec celle obtenue en l'appliquant sur le fichier temporaire
    if diff <(./src/main/bin/decac -p "$temp_file" 2>/dev/null) <(./src/main/bin/decac -p "$file" 2>/dev/null); then
        echo -e "${GREEN}✓ Test passed${NC}: $file"
        passed_tests=$((passed_tests + 1))
    else
        echo -e "${RED}✗ Test failed${NC}: $file"
        echo "Differences trouvées entre les deux exécutions:"
        diff <(./src/main/bin/decac -p "$temp_file" 2>/dev/null) <(./src/main/bin/decac -p "$file" 2>/dev/null)
        failed_tests=$((failed_tests + 1))
    fi

    # Supprimer le fichier temporaire
    rm "$temp_file"
}

# Vérifier si le dossier de test existe
if [ ! -d "src/test/deca/syntax/valid" ]; then
    echo "Erreur: Le dossier src/test/deca/syntax/valid n'existe pas"
    exit 1
fi

# Chercher tous les fichiers .deca et les tester
echo "Démarrage des tests..."
find src/test/deca/syntax/valid -name "*.deca" -type f | while read -r file; do
    test_file "$file"
done

# Afficher les statistiques
echo -e "\nRésultats des tests:"
echo "Total des tests: $total_tests"
echo -e "${GREEN}Tests réussis: $passed_tests${NC}"
echo -e "${RED}Tests échoués: $failed_tests${NC}"

# Sortir avec un code d'erreur si des tests ont échoué
if [ $failed_tests -gt 0 ]; then
    exit 1
else
    exit 0
fi