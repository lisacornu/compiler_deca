#!/bin/sh
# Changer le répertoire de travail vers le répertoire racine du projet
cd "$(dirname "$0")"/../../.. || exit 1

# Ajouter le répertoire contenant les scripts de test à la variable PATH
PATH=./src/test/script/launchers:"$PATH"

# Fonction pour tester les fichiers invalides
test_contexte_invalide () {
    # $1 = premier argument (chemin du fichier de test)
    
    # Appeler la fonction test_context sur le fichier donné, rediriger la sortie vers /dev/null
    if test_context "$1" > /dev/null 2>&1
    then
        # Si la commande réussit, afficher un message d'erreur
        echo "Succès inattendu de test_context sur $1."
    else
        # Si la commande échoue, afficher un message attendu
        echo "Échec attendu pour test_context sur $1."
    fi
}    

# Parcourir tous les fichiers dans le répertoire des tests invalides et les tester
for cas_de_test in src/test/deca/context/invalid/*.deca
do
    # Appeler la fonction test_contexte_invalide pour chaque fichier trouvé
    test_contexte_invalide "$cas_de_test"
done

# Fonction pour tester les fichiers valides et comparer avec le fichier .arbre attendu
test_contexte_valide () {
    # $1 = premier argument (le fichier .deca à tester)
    
    # Déterminer le chemin du fichier .arbre attendu en remplaçant l'extension .deca par .arbre
    fichier_arbre_attendu="src/test/deca/context/expected/$(basename "${1%.deca}.arbre")"

    # Exécuter la commande test_context sur le fichier .deca et capturer la sortie dans un fichier temporaire
    fichier_arbre_temp="$1.temp_arbre"
    test_context "$1" > "$fichier_arbre_temp" 2>/dev/null

    # Comparer le fichier généré avec le fichier .arbre attendu
    if diff -u "$fichier_arbre_attendu" "$fichier_arbre_temp" > /dev/null 2>&1
    then
        # Si les fichiers sont identiques, afficher un message de succès
        echo "Succès attendu pour test_context sur $1 : l'arbre correspond."
    else
        # Si les fichiers diffèrent, afficher un message d'erreur et les différences
        echo "Erreur : l'arbre généré pour $1 ne correspond pas à $fichier_arbre_attendu."
        echo "Différences :"
        diff -u "$fichier_arbre_attendu" "$fichier_arbre_temp"
    fi

    # Supprimer le fichier temporaire après la comparaison
    rm -f "$fichier_arbre_temp"
}

# Parcourir tous les fichiers dans le répertoire des tests valides et les tester
for cas_de_test in src/test/deca/context/valid/others/*.deca
do
    # Appeler la fonction test_contexte_valide pour chaque fichier trouvé
    test_contexte_valide "$cas_de_test"
done

