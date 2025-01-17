#!/bin/bash

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
ORANGE='\033[0;33m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# Va à la racine du projet
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
EXPECTED_DIR="src/test/deca/codegen/valid/expected"

# Vérifie si les dossier existent
if [ ! -d "$PERSONAL_TEST_DIR" ]; then
    echo -e "${RED}Error: Personal test directory not found: $PERSONAL_TEST_DIR${NC}"
    exit 1
fi
if [ ! -d "$GENERAL_TEST_DIR" ]; then
    echo -e "${RED}Error: General test directory not found: $GENERAL_TEST_DIR${NC}"
    exit 1
fi

# Initialisation
total_files=0
successful_compilations=0
successful_executions=0
failed_compilations=0
failed_executions=0
total_executions=0

# Traite un fichier . deca
process_personal_file() {
    local deca_file="$1"
    local base_name=$(basename "$deca_file" .deca)

    echo "Processing (compilation only): $deca_file"


    compilation_output=$(decac "$deca_file" 2>&1)

    # Vérifie si la compilation affiche qq chose ou non
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

# compile et execute un fichie deca
process_general_file() {
    local deca_file="$1"
    local base_name=$(basename "$deca_file" .deca)
    local dir_name=$(dirname "$deca_file")
    local expected_file="$EXPECTED_DIR/$base_name.expected"

    echo "Processing (compilation and execution): $deca_file"

    # Compile the .deca file and capture output
    compilation_output=$(decac "$deca_file" 2>&1)

    # Il y a une erreur si il y a . ou / dans la sortie (arbitraire)
    if [ -z "$compilation_output" ] || ! [[ "$compilation_output" =~ [./] ]]; then
        echo -e "${GREEN}✓ Compilation successful${NC}"
        ((successful_compilations++))

        # vérifie que les .ass sont générés
        if [ -f "$dir_name/$base_name.ass" ]; then
            ((total_executions++))
            # Execute
            ima_output=$(ima "$dir_name/$base_name.ass" 2>&1)

            # vérifie si les .expected existent
            if [ -f "$expected_file" ]; then
                expected_output=$(<"$expected_file")

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


for deca_file in $(find "$PERSONAL_TEST_DIR" -type f -name "*.deca"); do
    ((total_files++))
    process_personal_file "$deca_file"
done

for deca_file in $(find "$GENERAL_TEST_DIR" -type f -name "*.deca" ! -path "$PERSONAL_TEST_DIR/*"); do
    ((total_files++))
    process_general_file "$deca_file"
done

echo -e "\n${BOLD}Testing Summary:${NC}"
echo "Total files processed: $total_files"
echo "Successful compilations: $successful_compilations"
echo "Failed compilations: $failed_compilations"
echo "Total executions attempted: $total_executions"
echo "Successful executions: $successful_executions"
echo "Failed executions: $failed_executions"
