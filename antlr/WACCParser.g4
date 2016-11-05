parser grammar WACCParser;
options { tokenVocab=WACCLexer; }

// Top-level rule
program : BEGIN function* statement END EOF;

function : type IDENT LP parameterList? RP IS statement END ;
parameterList : parameter (COMMA parameter)* ;
parameter : type IDENT ;

functionCall : CALL IDENT LP argumentList? RP ;
argumentList : expression (COMMA expression)* ;

statement : NOP
          | type IDENT ASSIGN assignRhs
          | assignLhs ASSIGN assignRhs
          | READ assignLhs
          | expressionAction expression
          | IF expression THEN statement ELSE statement FI
          | WHILE expression DO statement DONE
          | BEGIN statement END
          | statement SEMICOLON statement
          ;


assignLhs : IDENT | arrayElement | pairElement ;
assignRhs : expression | arrayLiteral | pairConstructor | pairElement | functionCall ;

type : baseType | arrayType | pairType ;
baseType : INT | BOOL | CHAR | STRING ;

arrayType    : (baseType | pairType) (LB RB)+ ;
arrayElement : IDENT (LB expression RB)+ ;
arrayLiteral : LB (expression (COMMA expression)*)? RB ;

pairType        : PAIR LP pairElementType COMMA pairElementType RP ;
pairElementType : baseType | arrayType | PAIR ;
pairConstructor : NEWPAIR LP expression COMMA expression RP ;
pairElement     : FST expression | SND expression ;

expressionAction : FREE | RETURN | EXIT | PRINT | PRINTLN ;

expression : literal
           | IDENT
           | arrayElement
           | unaryOperator expression
           | expression binaryOperator expression
           | LP expression RP
           ;

literal : intLiteral | boolLiteral | CHAR_LITERAL | STRING_LITERAL | NULL_PAIR ;
intLiteral    : (PLUS | MINUS)? NUMBER ;
boolLiteral   : TRUE | FALSE ;

unaryOperator : NOT | MINUS | LEN | ORD | CHR ;
binaryOperator : arithmeticOperator | comparisonOperator | ASSIGN | NOT_EQUAL | EQUAL | AND | OR ;
arithmeticOperator : MUL | DIV | MOD | PLUS | MINUS ;
comparisonOperator : GREATER_THAN | GREATER_THAN_EQ | LESS_THAN | LESS_THAN_EQ ;
