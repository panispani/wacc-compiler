lexer grammar WACCLexer;

// symbols
BANG : '!' ;
PLUS : '+' ;
MINUS : '-' ;
STAR : '*' ;
DIV : '/' ;
MOD : '%' ;
GREATER_THAN : '>' ;
GREATER_THAN_EQ : '>=';
LESS_THAN : '<' ;
LESS_THAN_EQ : '<=' ;
EQUAL : '==' ;
NOT_EQUAL : '!=' ;
AND : '&&' ;
OR : '||' ;
L_BRACKET : '(' ;
R_BRACKET : ')' ;
COMMA : ',' ;
SEMICOLON : ';' ;
EQUALS : '=' ;
L_SQ_BRACKET : '[' ;
R_SQ_BRACKET : ']' ;
UNDERSCORE : '_' ;
SINGLE_QUOTE : '\'' ;
DOUBLE_QUOTE : '"' ;
HASH : '#' ;
BACKSLASH: '\\';

//types
NEWPAIR : 'newpair' ;
FST : 'fst' ;
SND : 'snd' ;
TRUE : 'true' ;
FALSE : 'false' ;
NULL : 'null' ;
INT : 'int' ;
BOOL : 'bool' ;
CHAR : 'char' ;
STRING : 'string' ;
PAIR : 'pair' ;


//keywords
LEN : 'len' ;
ORD : 'ord' ;
CHR : 'chr' ;
BEGIN: 'begin' ;
END  : 'end' ;
IS : 'is' ;
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
CALL: 'call' ;

// compound
WS : [ \t\r\n]+ -> skip ;

fragment
LOWERCASE : [a-z] ;
fragment
UPPERCASE : [A-Z] ;
fragment
DIGITS : [0-9] ;
DIGIT : [0-9];
fragment
ID_START : (UNDERSCORE | LOWERCASE | UPPERCASE) ;
fragment
ID_CHAR : (UNDERSCORE | LOWERCASE | UPPERCASE | DIGITS) ;
IDENT: ID_START (ID_CHAR)*;

CHAR_NOT_EOL : [~\n] ;
CHAR_NO_QUOTES_BACKSLASH: [^ ' | " | \\];
ESCAPED_CHAR: [0 | b | t | n | f | r | " | ' | \\];
COMMENT: '#' .*? '\n' -> skip;
// what about whitespace
