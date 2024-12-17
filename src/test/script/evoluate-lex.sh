#!/bin/sh

for i in ~/gl31/src/test/deca/syntax/valid/provided/*.deca
do
echo "$i"
# Remplacer <executable> par test_synt ou test_lex
# ou test_context ou decac
test_lex "$i"
done