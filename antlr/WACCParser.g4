parser grammar WACCParser;
options { tokenVocab=WACCLexer; }

// Top-level rule
program : BEGIN function* sequence END EOF;

function : type IDENT LP parameterList? RP IS sequence END ;
parameterList : parameter (COMMA parameter)* ;
parameter : type IDENT ;

functionCall : CALL IDENT LP argumentList? RP ;
argumentList : expression (COMMA expression)* ;

sequence : statement (SEMICOLON statement)* ;

statement : NOP                                                # Skip
          | type IDENT ASSIGN assignRhs                        # Declare
          | assignLhs ASSIGN assignRhs                         # Assign
          | READ assignLhs                                     # Read
          | FREE expression                                    # Free
          | RETURN expression                                  # Return
          | EXIT expression                                    # Exit
          | PRINT expression                                   # Print
          | PRINTLN expression                                 # PrintLn
          | IF expression THEN statement ELSE statement FI     # Conditional
          | WHILE expression DO statement DONE                 # Loop
          | BEGIN statement END                                # Scope
          | IF expression THEN sequence ELSE sequence FI       # Conditional
          | WHILE expression DO sequence DONE                  # Loop
          | BEGIN sequence END                                 # Scope
          ;


assignLhs : IDENT           # AssignLhsIdent
          | arrayElement    # AssignLhsArrayElement
          | pairElement     # AssignLhsPairElement
          ;

assignRhs : expression      # AssignRhsExpression
          | arrayLiteral    # AssingRhsArrayLiteral
          | pairConstructor # AssingRhsPairConstructor
          | pairElement     # AssingRhsPairElement
          | functionCall    # AssingRhsFunctionCall
          ;

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

expression : literal
           | IDENT
           | arrayElement
           | unaryOperator expression
           | expression binaryOperator expression
           | LP expression RP
           ;

literal : intLiteral | boolLiteral | charLiteral | stringLiteral | pairLiteral ;
intLiteral    : (PLUS | MINUS)? NUMBER ;
boolLiteral   : TRUE | FALSE ;
charLiteral   : CHAR_LITERAL ;
stringLiteral : STRING_LITERAL ;
pairLiteral      : NULL_PAIR ;

unaryOperator : NOT | MINUS | LEN | ORD | CHR ;
binaryOperator : arithmeticOperator | comparisonOperator | ASSIGN | NOT_EQUAL | EQUAL | AND | OR ;
arithmeticOperator : MUL | DIV | MOD | PLUS | MINUS ;
comparisonOperator : GREATER_THAN | GREATER_THAN_EQ | LESS_THAN | LESS_THAN_EQ ;
