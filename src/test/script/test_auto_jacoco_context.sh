#! /bin/sh

# Auteur : gl31
# Version initiale : 10/01/2025

# Test couverture contextuelle.
set +e

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"
for file_invalid in src/test/deca/context/invalid/*; do
    test_context "$file_invalid" > poubelle.txt
done
for file_valid_perso in src/test/deca/context/valid/personal/*; do
    test_context "$file_valid_perso" > poubelle.txt
done

for file_valid in src/test/deca/context/valid/provided/*; do
    test_context "$file_valid" > poubelle.txt
done

