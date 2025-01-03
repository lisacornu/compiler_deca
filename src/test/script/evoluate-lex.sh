#!/bin/sh

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

# Partie 1 : Tester les fichiers valides
for i in src/test/deca/syntax/valid/provided/*.deca
do
    test_lex "$i" > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        echo "Succès attendu pour le fichier $i"
    else
        echo "Échec non attendu pour le fichier $i"
        #exit 1;
    fi
done

# Partie 2 : Capturer et comparer les tokens pour un fichier spécifique
fichier_test="src/test/deca/syntax/invalid/provided/regarderqqtaken.deca"
resultat_attendu="src/test/deca/syntax/expected/regarderqqtaken.tokens"

# Exécuter la commande pour capturer la sortie des tokens
test_lex "$fichier_test" > sortie_actuelle.tokens 2>&1

# Comparer avec le résultat attendu
if diff -u "$resultat_attendu" sortie_actuelle.tokens > diff_resultat.txt; then
    echo "Les tokens correspondent au résultat attendu pour $fichier_test."
else
    echo "Les tokens ne correspondent PAS au résultat attendu pour $fichier_test."
    echo "Différences :"
    cat diff_resultat.txt
fi

# Nettoyer les fichiers temporaires
rm -f sortie_actuelle.tokens diff_resultat.txt
