lexer grammar WACCLexer;

// arithmetic operators
PLUS : '+' ;
MINUS : '-' ;
STAR : '*' ;
DIV : '/' ;
MOD : '%' ;
EQUALS : '=' ;


// comparison operators
GREATER_THAN : '>' ;
GREATER_THAN_EQ : '>=';
LESS_THAN : '<' ;
LESS_THAN_EQ : '<=' ;
EQUAL : '==' ;
NOT_EQUAL : '!=' ;


// boolean operators
BANG : '!' ;
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


// brackets
L_BRACKET : '(' ;
R_BRACKET : ')' ;
L_SQ_BRACKET : '[' ;
R_SQ_BRACKET : ']' ;


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

// special characters
SINGLE_QUOTE : '\'' ;
DOUBLE_QUOTE : '"' ;
UNDERSCORE : '_' ;
BACKSLASH: '\\';
ESCAPED_CHAR: '\\' [0 | b | t | n | f | r | " | ' | \\];

// function specific keywords
IS : 'is' ;
CALL: 'call' ;

// fragments
fragment
LOWERCASE : [a-z] ;
fragment
UPPERCASE : [A-Z] ;
fragment
DIGITS : [0-9] ;
fragment
ID_START : (UNDERSCORE | LOWERCASE | UPPERCASE) ;
fragment
ID_CHAR : (UNDERSCORE | LOWERCASE | UPPERCASE | DIGITS) ;


//whitespace
WS : [ \t\r\n]+ -> skip ;

// number
NUMBER :  DIGITS+ ;

// identifier
IDENT: ID_START (ID_CHAR)*;

// commment
COMMENT: '#' .*? '\n' -> skip;

// string and characters
STR_CHARACTER : (CHAR_NO_QUOTES_BACKSLASH | ESCAPED_CHAR) ;
STR_LITER : DOUBLE_QUOTE STR_CHARACTER* DOUBLE_QUOTE ;
CHAR_NOT_EOL : [~\n] ;
CHAR_NO_QUOTES_BACKSLASH: ~['"\\];