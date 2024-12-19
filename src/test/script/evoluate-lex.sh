#!/bin/sh

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

for i in src/test/deca/syntax/valid/provided/*.deca
do
    # Exécuter la commande sans afficher la sortie
    test_lex "$i" > /dev/null 2>&1
    
    # Vérifie le code de sortie de test_lex
    if [ $? -eq 0 ]; then
        echo "Succès attendu pour le fichier $i"
    else
        echo "Échec non attendu pour le fichier $i"
    fi
done

#regarder tout les takens possible 
test_lex src/test/deca/syntax/invalid/provided/regarderqqtaken.deca

#cas d'erreur prévu a revoir parceque y a des fichier qui sont valide lexi mais pas syntax 
test_lex src/test/deca/syntax/invalid/provided/lettre_bizarre.deca > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "Succès non attendu pour le fichier src/test/deca/syntax/invalid/provided/lettre_bizarre.deca"
else
    echo "Échec attendu pour le fichier src/test/deca/syntax/invalid/provided/lettre_bizarre.deca"
fi
