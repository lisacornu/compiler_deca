#! /bin/sh

# Auteur : gl31
# Version initiale : 10/01/2025

# Test couverture contextuelle.
set +e

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"
for file_invalid in src/test/deca/context/invalid/*; do
    test_context "$file_invalid"
done
for file_valid in src/test/deca/context/valid/*; do
    test_context "$file_valid"
done

