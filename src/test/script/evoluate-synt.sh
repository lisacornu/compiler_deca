#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

# Fonction pour tester les fichiers invalides
test_synt_invalide () {
    # $1 = premier argument.
    if test_synt "$1" > /dev/null 2>&1
    then
        echo "Succès inattendu de test_synt sur $1."
    else
        echo "Échec attendu pour test_synt sur $1."
    fi
}    

# Parcourir et tester les fichiers invalides
for cas_de_test in src/test/deca/syntax/invalid/personal/*.deca
do
    test_synt_invalide "$cas_de_test"
done

# Fonction pour tester les fichiers valides et comparer avec .arbre
test_synt_valide () {
    # $1 = premier argument (le fichier .deca).
    fichier_arbre_attendu="src/test/deca/syntax/expected/$(basename "${1%.deca}.arbre")"

    # Exécuter test_synt et capturer la sortie dans un fichier temporaire
    fichier_arbre_temp="$1.temp_arbre"
    test_synt "$1" > "$fichier_arbre_temp" 2>/dev/null

    # Comparer le fichier généré avec le fichier attendu
    if diff -u "$fichier_arbre_attendu" "$fichier_arbre_temp" > /dev/null 2>&1
    then
        echo "Succès attendu pour test_synt sur $1 : l'arbre correspond."
    else
        echo "Erreur : l'arbre généré pour $1 ne correspond pas à $fichier_arbre_attendu."
        echo "Différences :"
        diff -u "$fichier_arbre_attendu" "$fichier_arbre_temp"
    fi

    # Supprimer le fichier temporaire
    rm -f "$fichier_arbre_temp"
}

# Parcourir et tester les fichiers valides
for cas_de_test in src/test/deca/syntax/valid/personal/*.deca
do
    test_synt_valide "$cas_de_test"
done
