#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

# exemple de définition d'une fonction
test_synt_invalide () {
    # $1 = premier argument.
    if test_synt "$1" > /dev/null 2>&1
    then
        echo "Succes inattendu de test_synt sur $1."
    else
        echo "Echec attendu pour test_synt sur $1."
    fi
}    

 for cas_de_test in src/test/deca/syntax/invalid/provided/*.deca
 do
    test_synt_invalide "$cas_de_test"
done

# exemple de définition d'une fonction
test_synt_valide () {
    # $1 = premier argument.
    if test_synt "$1" > /dev/null 2>&1
    then
        echo "Succes attendu de test_synt sur $1."
    else
        echo "Echec inattendu pour test_synt sur $1."
    fi
}    

 for cas_de_test in src/test/deca/syntax/valid/provided/*.deca
 do
    test_synt_valide "$cas_de_test"
done



