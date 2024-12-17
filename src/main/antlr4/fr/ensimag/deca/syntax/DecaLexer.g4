lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

EOL : '\n';

OBRACE : '{';
CBRACE : '}';

OPARENT : '(';
CPARENT : ')';

SEMI : ';';
COMMA : ',';

EQUALS : '=';
EQEQ : '==';
NEQ : '!=';
LEQ : '<=';
GEQ : '>=';
GT : '>';
LT : '<';

PRINT : 'print';
PRINTLN : 'println';
PRINTX : 'printx';
PRINTLNX : 'printlnx';

WHILE : 'while';
FOR : 'for';

IF : 'if';
ELSE : 'else';

OR : '||';
AND : '&&';

INSTANCEOF : 'instanceof';
RETURN : 'return';

PLUS : '+';
MINUS : '-';
TIMES : '*';
SLASH : '/';
PERCENT : '%;';
EXCLAM : '!';
DOT : '.';

READINT : 'readInt';
READFLOAT : 'readFloat';
NEW : 'new';

INT : 'int';
STRING : 'String';
FLOAT : 'float';
TRUE : 'true';
FALSE : 'false';
THIS : 'this';
NULL : 'null';

fragment LETTER : 'a' .. 'z' | 'A' .. 'Z';
fragment DIGIT : '0' .. '9';

IDENT : (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;

CLASS : 'class';
EXTENDS : 'extends';

PROTECTED : 'protected';

ASM : 'asm';
STRING_CAR : ~('"' | '\\');
MULTI_LINE_STRING : '"'(STRING_CAR | EOL | '\\"' | '\\\\')*'"';

COMMENT : '//' (~('\n'))* EOL {skip();};
MULTI_LINE_COMMENT : '/*' .*? '*/' {skip();};