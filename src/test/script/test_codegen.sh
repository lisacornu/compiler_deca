#!/bin/bash

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

# Counter for statistics
total_files=0
successful_compilations=0
successful_executions=0
failed_compilations=0
failed_executions=0

# Function to process .deca files
process_deca_file() {
    local deca_file="$1"
    local base_name=$(basename "$deca_file" .deca)
    local dir_name=$(dirname "$deca_file")
    local expected_file="$EXPECTED_DIR/$base_name.expected"

    echo "Processing: $deca_file"
    ((total_files++))

    # Compile the .deca file and capture output
    compilation_output=$(decac "$deca_file" 2>&1)

    # Check if compilation was successful (no output)
    if [ -z "$compilation_output" ]; then
        echo "✓ Compilation successful"
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
                    echo "✓ Execution matches expected output"
                    ((successful_executions++))
                else
                    echo "✗ Execution output mismatch"
                    echo "Expected:"
                    echo "$expected_output"
                    echo "Got:"
                    echo "$ima_output"
                    ((failed_executions++))
                fi
            else
                echo "! Warning: Expected output file not found: $expected_file"
                ((failed_executions++))
            fi
        else
            echo "✗ Assembly file not generated: $dir_name/$base_name.ass"
            ((failed_executions++))
        fi
    else
        echo "✗ Compilation failed"
        echo "Compilation output:"
        echo "$compilation_output"
        ((failed_compilations++))
    fi
    echo "----------------------------------------"
}

# Find and process all .deca files
find "$TEST_DIR" -type f -name "*.deca" | while read -r file; do
    process_deca_file "$file"
done

# Print summary
echo "Testing Summary:"
echo "Total files processed: $total_files"
echo "Successful compilations: $successful_compilations"
echo "Failed compilations: $failed_compilations"
echo "Successful executions: $successful_executions"
echo "Failed executions: $failed_executions"