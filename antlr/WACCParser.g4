parser grammar WACCParser;
options { tokenVocab=WACCLexer; }

// Top-level rule
program : BEGIN function* statement END EOF;

function : type IDENT LP parameterList? RP IS statement END ;
parameterList : parameter (COMMA parameter)* ;
parameter : type IDENT ;

functionCall : CALL IDENT LP argumentList? RP ;
argumentList : expression (COMMA expression)* ;

statement : NOP                                                # Skip
          | type IDENT ASSIGN assignRhs                        # Declare
          | assignLhs ASSIGN assignRhs                         # Assign
          | READ assignLhs                                     # Read
          | expressionAction expression                        # Action
          | IF expression THEN statement ELSE statement FI     # Conditional
          | WHILE expression DO statement DONE                 # Loop
          | BEGIN statement END                                # Scope
          | statement SEMICOLON statement                      # Sequence
          ;


assignLhs : IDENT | arrayElement | pairElement ;
assignRhs : expression | arrayLiteral | pairConstructor | pairElement | functionCall ;

type : primitiveType
     | arrayType
     | pairType
     ;

primitiveType : INT | BOOL | CHAR | STRING ;
notNestedArrayType: primitiveType | pairType ;

arrayType    : notNestedArrayType (LB RB)+ ;

arrayElement : IDENT (LB expression RB)+ ;
arrayLiteral : LB (expression (COMMA expression)*)? RB ;

pairType        : PAIR LP firstType=pairElementType COMMA secondType=pairElementType RP ;
pairElementType : primitiveType | arrayType | erasedPair ;
pairConstructor : NEWPAIR LP expression COMMA expression RP ;
pairElement     : FST expression | SND expression ;
erasedPair      : PAIR ;

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
