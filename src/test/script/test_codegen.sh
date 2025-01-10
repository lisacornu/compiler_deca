#!/bin/bash

#-----------------------------------------------------------------------------------------------
#
# Ce test lance la commande decac sur tous les fichier .deca du répertoire de test
# de codegen et considère les test comme passé si les test valide (respectivement invalide)
# ne produisent pas de sorti (respectivement en produisent) et considère le test comme échoué
# dans le cas inverse
#
#-----------------------------------------------------------------------------------------------


# Dossiers contenant les fichiers à tester
VALID_DIR="../deca/codegen/valid"
INVALID_DIR="../deca/codegen/invalid"

# Vérification si les répertoires existent et contiennent des fichiers .deca
if [ ! -d "$VALID_DIR" ]; then
    echo "Le répertoire $VALID_DIR n'existe pas."
    exit 1
fi

if [ ! -d "$INVALID_DIR" ]; then
    echo "Le répertoire $INVALID_DIR n'existe pas."
    exit 1
fi

# Vérification des fichiers .deca
valid_deca_files=$(find "$VALID_DIR" -type f -name "*.deca")
invalid_deca_files=$(find "$INVALID_DIR" -type f -name "*.deca")

if [ -z "$valid_deca_files" ] && [ -z "$invalid_deca_files" ]; then
    echo "Aucun fichier .deca trouvé dans les répertoires $VALID_DIR et $INVALID_DIR."
    exit 1
fi

# Statistiques
total_tests=0
passed_tests=0
failed_tests=()

function print_green {
    echo -e "\033[32m$1\033[0m"
}

function print_red {
    echo -e "\033[31m$1\033[0m"
}

function print_bold {
    echo -e "\033[1m$1\033[0m"
}

# Test du répertoire valide
echo "Vérification des fichiers .deca valide :"

# Récupère les fichier un par un
while IFS= read -r file; do
    total_tests=$((total_tests + 1))

    # lancement de la commande decac
    output=$(decac "$file" 2>&1)
    exit_code=$?

    # Vérification de la sortie
    if [ $exit_code -eq 0 ] && [ -z "$output" ]; then
        passed_tests=$((passed_tests + 1))
        print_green "test n°$total_tests passed : $(basename "$file")"
    else
        # Si un message est affiché --> problème si c'est un test valide
        print_red "$(basename "$file") erreur :"
        echo -e "$output"  # Affiche le message d'erreur sous le nom du fichier
        failed_tests+=("$(basename "$file")")
    fi
done <<< "$valid_deca_files"

# Test des fichiers invalide
echo "Vérification des fichiers .deca invalide :"

# Récupère les fichier un par un
while IFS= read -r file; do
    total_tests=$((total_tests + 1))

    output=$(decac "$file" 2>&1)
    exit_code=$?

    # Vérification de la sortie : inverse des test valide
    if [ $exit_code -eq 0 ] && [ -z "$output" ]; then
        print_red "$(basename "$file") erreur :"
        echo -e "$output"  # Affiche le message d'erreur sous le nom du fichier
        failed_tests+=("$(basename "$file")")
    else
        # Si la commande affiche quelque chose (erreur ou autre) --> le test est réussi pour "invalid"
        passed_tests=$((passed_tests + 1))
        print_green "test n°$total_tests passed : $(basename "$file")"
    fi
done <<< "$invalid_deca_files"

# Résumé
if [ $total_tests -gt 0 ]; then
    passed_percentage=$((100 * passed_tests / total_tests))
else
    passed_percentage=0
fi

echo ""
print_bold "Résumé des tests :"

if [ ${#failed_tests[@]} -gt 0 ]; then
    print_bold "Tests échoués :"
    for failed_test in "${failed_tests[@]}"; do
        echo "  - $failed_test"
    done
else
    echo "  Aucun test échoué."
fi

find "$VALID_DIR" -type f -name "*.ass" -exec rm -f {} \;
find "$INVALID_DIR" -type f -name "*.ass" -exec rm -f {} \;

echo ""
print_bold "Résumé final :"
echo "  - Nombre total de tests : $total_tests"
print_green "  --> Tests réussis : $passed_tests"
print_red "  --> Tests échoués : ${#failed_tests[@]}"
print_bold "Taux de réussite : $passed_percentage%"
