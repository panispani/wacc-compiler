lexer grammar WACCLexer;

// arithmetic operators
PLUS : '+' ;
MINUS : '-' ;
MUL : '*' ;
DIV : '/' ;
MOD : '%' ;

ASSIGN : '=' ;

// comparison operators
GREATER_THAN : '>' ;
GREATER_THAN_EQ : '>=';
LESS_THAN : '<' ;
LESS_THAN_EQ : '<=' ;
EQUAL : '==' ;
NOT_EQUAL : '!=' ;

// boolean operators
NOT : '!' ;
AND : '&&' ;
OR : '||' ;

// boolean values
TRUE : 'true' ;
FALSE : 'false' ;

// base types
INT : 'int' ;
BOOL : 'bool' ;
CHAR : 'char' ;
STRING : 'string' ;
PAIR : 'pair' ;

// pairs
NEWPAIR : 'newpair' ;
FST : 'fst' ;
SND : 'snd' ;
NULL_PAIR : 'null' ;

//advanced types
STRUCT : 'struct' ;

// parentheses and brackets
LP : '(' ;
RP : ')' ;
LB : '[' ;
RB : ']' ;

// stat keywords
NOP : 'skip' ;
READ : 'read' ;
FREE : 'free' ;
RETURN : 'return' ;
EXIT : 'exit' ;
PRINT : 'print' ;
PRINTLN : 'println' ;
IF : 'if' ;
THEN : 'then' ;
ELSE : 'else' ;
FI : 'fi' ;
WHILE : 'while' ;
FOR : 'for' ;
DO : 'do' ;
DONE : 'done' ;
BEGIN: 'begin' ;
END  : 'end' ;

// punctuation
COMMA : ',' ;
SEMICOLON : ';' ;
HASH : '#' ;

// unary operators
LEN : 'len' ;
ORD : 'ord' ;
CHR : 'chr' ;

// function specific keywords
IS : 'is' ;
CALL: 'call' ;

BINARY : BINARY_INTRO BIN_DIGIT+ ;
fragment
BINARY_INTRO: '0b' ;
fragment
BIN_DIGIT : [0-1] ;

HEX : HEX_INTRO HEX_DIGIT+ ;
fragment
HEX_INTRO: '0x' ;
fragment
HEX_DIGIT : [0-9A-Fa-f] ;

OCTAL : OCTAL_INTRO OCT_DIGIT* ;
fragment
OCTAL_INTRO: '0' ;
fragment
OCT_DIGIT : [0-7] ;

DECIMAL :  DECIMAL_INTRO (DEC_DIGIT*) ;
fragment
DECIMAL_INTRO: [1-9] ;
fragment
DEC_DIGIT : [0-9] ;

IDENT: ID_START (ID_CHAR)*;
fragment
ID_START : (UNDERSCORE | ALPHA) ;
fragment
ID_CHAR : (UNDERSCORE | ALPHA | DEC_DIGIT) ;
fragment
ALPHA : [a-zA-Z] ;
fragment
UNDERSCORE : '_' ;

//whitespace
WS : [ \t\r\n]+ -> skip ;

// commment
COMMENT: '#' .*? '\n' -> skip;

// string and characters
STRING_LITERAL : DOUBLE_QUOTE (ESCAPED_CHAR | CHARACTER)*? DOUBLE_QUOTE ;
CHAR_LITERAL : SINGLE_QUOTE CHARACTER SINGLE_QUOTE ;
fragment
CHARACTER : (ESCAPED_CHAR | SINGLE_CHARACTER) ;
fragment
SINGLE_CHARACTER: ~['"\\];
fragment
SINGLE_QUOTE : '\'' ;
fragment
DOUBLE_QUOTE : '"' ;
fragment
ESCAPED_CHAR: BACKSLASH [0 | b | t | n | f | r | " | ' | \\];
fragment
BACKSLASH: '\\';
