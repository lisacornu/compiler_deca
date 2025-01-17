#!/bin/bash

# Color definitions
GREEN='\033[0;32m'
RED='\033[0;31m'
ORANGE='\033[0;33m'
NC='\033[0m' # No Color

# Go to project root (where pom.xml is located)
while [ ! -f pom.xml ] && [ "$PWD" != "/" ]; do
    cd ..
done

if [ ! -f pom.xml ]; then
    echo "Error: Could not find project root (pom.xml)"
    exit 1
fi

# Test directory path
TEST_DIR="src/test/deca/codegen/valid"
EXPECTED_DIR="$TEST_DIR/expected"

# Check if test directory exists
if [ ! -d "$TEST_DIR" ]; then
    echo "Error: Test directory not found: $TEST_DIR"
    exit 1
fi

# Initialize counters
total_files=0
successful_compilations=0
successful_executions=0
failed_compilations=0
failed_executions=0

# Process all .deca files and store results
while IFS= read -r deca_file; do
    ((total_files++))

    echo "Processing: $deca_file"
    base_name=$(basename "$deca_file" .deca)
    dir_name=$(dirname "$deca_file")
    expected_file="$EXPECTED_DIR/$base_name.expected"

    # Compile the .deca file and capture output
    compilation_output=$(decac "$deca_file" 2>&1)

    # Check if compilation was successful (no output)
    if [ -z "$compilation_output" ]; then
        echo -e "${GREEN}✓ Compilation successful${NC}"
        ((successful_compilations++))

        # Check if .ass file was generated
        if [ -f "$dir_name/$base_name.ass" ]; then
            # Run ima and capture output
            ima_output=$(ima "$dir_name/$base_name.ass" 2>&1)

            # Check if expected file exists
            if [ -f "$expected_file" ]; then
                expected_output=$(<"$expected_file")

                # Compare outputs
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
done < <(find "$TEST_DIR" -type f -name "*.deca")

# Print summary
echo "Testing Summary:"
echo "Total files processed: $total_files"
echo "Successful compilations: $successful_compilations"
echo "Failed compilations: $failed_compilations"
echo "Successful executions: $successful_executions"
echo "Failed executions: $failed_executions"