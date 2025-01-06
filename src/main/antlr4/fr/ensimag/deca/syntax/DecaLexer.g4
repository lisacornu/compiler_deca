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

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {
              skip(); // avoid producing a token
          }
    ;

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

fragment SIGN : ('+' | '-')?;
fragment NUM : DIGIT+;
fragment DEC : NUM '.' NUM;
fragment EXP : ('E' | 'e') SIGN NUM;
fragment FLOATDEC : (DEC | DEC EXP) ('F' | 'f')?;
fragment DIGITHEX : DIGIT | 'a' .. 'f' | 'A' .. 'F';
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f' )?;
FLOAT : FLOATDEC | FLOATHEX;
INT : SIGN? NUM;
TRUE : 'true';
FALSE : 'false';
THIS : 'this';
NULL : 'null';
CLASS : 'class';
EXTENDS : 'extends';

PROTECTED : 'protected';

ASM : 'asm';
fragment EOL : '\n';
fragment STRING_CAR : ~('\\'|'"' | '\n' ) ;
STRING : '"' (STRING_CAR | '\\"' | '\\\\')* '"';
MULTI_LINE_STRING : '"' (STRING_CAR | EOL |'\\"' | '\\\\')* '"';


fragment LETTER : 'a' .. 'z' | 'A' .. 'Z';
fragment DIGIT : '0' .. '9';

IDENT : (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;



COMMENT : '//' (~('\n'))* '\n' {skip();};
MULTI_LINE_COMMENT : '/*' .*? '*/' {skip();};

fragment FILENAME : ( LETTER | DIGIT | '.' | '-' | '_' )+;
INCLUDE : '#include'  (' ')* '"' FILENAME '"';
