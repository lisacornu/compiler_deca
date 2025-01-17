#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
ORANGE='\033[0;33m'
BOLD='\033[1m'
NC='\033[0m'

# remonte à la racine du projet
while [ ! -f pom.xml ] && [ "$PWD" != "/" ]; do
    cd ..
done

if [ ! -f pom.xml ]; then
    echo -e "${RED}Error: Could not find project root (pom.xml)${NC}"
    exit 1
fi


for cmd in "clean" "compile" "test-compile"; do
    echo -e "${BOLD}Running 'mvn $cmd'...${NC}"
    if ! mvn "$cmd"; then
        echo -e "${RED}Error: 'mvn $cmd' failed. Aborting.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ 'mvn $cmd' succeeded.${NC}"
done


PERSONAL_TEST_DIR="src/test/deca/codegen/valid/personal/stdin"
GENERAL_TEST_DIR="src/test/deca/codegen/valid"
INVALID_TEST_DIR="src/test/deca/codegen/invalid"
EXPECTED_DIR="src/test/deca/codegen/valid/expected"


if [ ! -d "$PERSONAL_TEST_DIR" ]; then
    echo -e "${RED}Error: Personal test directory not found: $PERSONAL_TEST_DIR${NC}"
    exit 1
fi
if [ ! -d "$GENERAL_TEST_DIR" ]; then
    echo -e "${RED}Error: General test directory not found: $GENERAL_TEST_DIR${NC}"
    exit 1
fi
if [ ! -d "$INVALID_TEST_DIR" ]; then
    echo -e "${RED}Error: Invalid test directory not found: $INVALID_TEST_DIR${NC}"
    exit 1
fi


total_files=0
successful_compilations=0
successful_executions=0
failed_compilations=0
failed_executions=0
total_executions=0
successful_invalid=0
failed_invalid=0

# Gère les .deca qu'il faut juste compiler (ceux avec entrées)
process_personal_file() {
    local deca_file="$1"
    local base_name=$(basename "$deca_file" .deca)

    echo "Processing (compilation only): $deca_file"

    # Compile
    compilation_output=$(decac "$deca_file" 2>&1)

    # Si la sortie contient . ou / la compilation est echouée
    if [ -z "$compilation_output" ] || ! [[ "$compilation_output" =~ [./] ]]; then
        echo -e "${GREEN}✓ Compilation successful${NC}"
        ((successful_compilations++))
    else
        echo -e "${RED}✗ Compilation failed${NC}"
        echo "Compilation output:"
        echo "$compilation_output"
        ((failed_compilations++))
    fi
    echo "----------------------------------------"
}

# execution et compilation d'un .deca
process_general_file() {
    local deca_file="$1"
    local base_name=$(basename "$deca_file" .deca)
    local dir_name=$(dirname "$deca_file")
    local expected_file="$EXPECTED_DIR/$base_name.expected"

    echo "Processing (compilation and execution): $deca_file"

    # Compile les .deca
    compilation_output=$(decac "$deca_file" 2>&1)

    # Compilation échoué si la sortie contient . ou /
    if [ -z "$compilation_output" ] || ! [[ "$compilation_output" =~ [./] ]]; then
        echo -e "${GREEN}✓ Compilation successful${NC}"
        ((successful_compilations++))

        # Vérif que les .ass sont générés
        if [ -f "$dir_name/$base_name.ass" ]; then
            ((total_executions++))

            ima_output=$(ima "$dir_name/$base_name.ass" 2>&1)

            # Vérifie que les fichiers existent
            if [ -f "$expected_file" ]; then
                expected_output=$(<"$expected_file")

                # Compare les sorties
                if [ "$ima_output" == "$expected_output" ]; then
                    echo -e "${GREEN}✓ Execution matches expected output${NC}"
                    ((successful_executions++))
                else
                    echo -e "${RED}✗ Execution output mismatch${NC}"
                    echo "Expected:"
                    echo "$expected_output"
                    echo "Got:"
                    echo "$ima_output"
                    ((failed_executions++))
                fi
            else
                echo -e "${ORANGE}! Warning: Expected output file not found: $expected_file${NC}"
                ((failed_executions++))
            fi

            # Retire les .ass
            rm -f "$dir_name/$base_name.ass"
        else
            echo -e "${RED}✗ Assembly file not generated: $dir_name/$base_name.ass${NC}"
            ((failed_executions++))
        fi
    else
        echo -e "${RED}✗ Compilation failed${NC}"
        echo "Compilation output:"
        echo "$compilation_output"
        ((failed_compilations++))
    fi
    echo "----------------------------------------"
}

# Gère les fichiers .deca invalide
process_invalid_file() {
    local deca_file="$1"
    local base_name=$(basename "$deca_file" .deca)

    echo "Processing (invalid test): $deca_file"

    # Compile
    compilation_output=$(decac "$deca_file" 2>&1)

    # Si la sortie contient . ou / elle est considéré comme échoué
    if [[ "$compilation_output" =~ [./] ]]; then
        echo -e "${GREEN}✓ Invalid test passed${NC}"
        ((successful_invalid++))
    else
        echo -e "${RED}✗ Invalid test failed${NC}"
        echo "Compilation output:"
        echo "$compilation_output"
        ((failed_invalid++))
    fi

    # retire les fichiers .ass générés
    rm -f "$(dirname "$deca_file")/$base_name.ass"

    echo "----------------------------------------"
}


# shellcheck disable=SC2044
for deca_file in $(find "$PERSONAL_TEST_DIR" -type f -name "*.deca"); do
    ((total_files++))
    process_personal_file "$deca_file"
done


# shellcheck disable=SC2044
for deca_file in $(find "$GENERAL_TEST_DIR" -type f -name "*.deca" ! -path "$PERSONAL_TEST_DIR/*"); do
    ((total_files++))
    process_general_file "$deca_file"
done


# shellcheck disable=SC2044
for deca_file in $(find "$INVALID_TEST_DIR" -type f -name "*.deca"); do
    ((total_files++))
    process_invalid_file "$deca_file"
done

total_compilation_sucess=$((successful_compilations + successful_invalid))
total_compilation_fail=$((failed_compilations + failed_invalid))

echo -e "\n${BOLD}Testing Summary:${NC}"

echo -e "\n${BOLD}Compiling Tests Summary:${NC}"
echo "Total files compiled: $total_files"
echo -e "${GREEN}Successful compilations tests: $total_compilation_sucess${NC}"
echo -e "${RED}Failed compilations tests : $total_compilation_fail${NC}"

echo -e "\n${BOLD}Executing Tests Summary:${NC}"
echo "Total executions attempted: $total_executions"
echo -e "${GREEN}Successful executions: $successful_executions${NC}"
echo -e "${RED}Failed executions: $failed_executions${NC}"
