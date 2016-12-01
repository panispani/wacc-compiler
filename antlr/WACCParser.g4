parser grammar WACCParser;
options { tokenVocab=WACCLexer; }

// Top-level rule
program : BEGIN struct* function* sequence END EOF;

struct : STRUCT IDENT (structMember SEMICOLON)* ;
structMember : type IDENT ;

function : type IDENT LP parameterList? RP IS sequence END ;
parameterList : parameter (COMMA parameter)* ;
parameter : type IDENT ;

functionCall : CALL IDENT LP argumentList? RP ;
argumentList : expression (COMMA expression)* ;

sequence : statement (SEMICOLON statement)* ;

statement : NOP                                                                           # Skip
          | type IDENT ASSIGN assignRhs                                                   # Declare
          | assignLhs ASSIGN assignRhs                                                    # Assign
          | READ assignLhs                                                                # Read
          | FREE expression                                                               # Free
          | RETURN expression                                                             # Return
          | EXIT expression                                                               # Exit
          | PRINT expression                                                              # Print
          | PRINTLN expression                                                            # PrintLn
          | conditionalStatement                                                          # Conditional
          | loopStatement                                                                 # Loop
          | BEGIN sequence END                                                            # Scope
          ;

conditionalStatement : IF expression THEN trueSequence=sequence FI                              # IfSimple
                     | IF expression THEN trueSequence=sequence ELSE falseSequence=sequence FI  # IfElse
                     | IF expression THEN trueSequence=sequence ELSE conditionalStatement       # IfRecursive
                     ;

loopStatement : WHILE expression DO sequence DONE                                              # While
              | DO sequence WHILE expression                                                   # DoWhile
              | FOR (init=statement)? SEMICOLON
                    cond=expression SEMICOLON
                    (step=statement)? DO body=sequence DONE                               # For
              ;

assignLhs : variableReference # AssignLhsIdent
          | arrayElement      # AssignLhsArrayElement
          | pairElement       # AssignLhsPairElement
          ;

assignRhs : expression      # AssignRhsExpression
          | arrayLiteral    # AssignRhsArrayLiteral
          | pairConstructor # AssignRhsPairConstructor
          | pairElement     # AssignRhsPairElement
          | functionCall    # AssignRhsFunctionCall
          ;

type : primitiveType
     | arrayType
     | pairType
     ;

primitiveType : INT | BOOL | CHAR | STRING ;
notNestedArrayType: primitiveType | pairType ;

arrayType    : notNestedArrayType (LB RB)+ ;

arrayElement : variableReference (LB expression RB)+ ;
arrayLiteral : LB (expression (COMMA expression)*)? RB ;

pairType        : PAIR LP firstType=pairElementType COMMA secondType=pairElementType RP ;
pairElementType : primitiveType | arrayType | erasedPair ;
pairConstructor : NEWPAIR LP expression1=expression COMMA expression2=expression RP ;
pairElement     : selector=FST expression | selector=SND expression ;
erasedPair      : PAIR ;

expression : literal                                   # LiteralExp
           | variableReference                         # VariableRefExp
           | arrayElement                              # ArrayElemExp
           | unaryOperator expression                  # UnaryOperatorExp
           | LP expression RP                          # BracketedExp
           | expression op=MUL expression              # BinaryOperatorExp
           | expression op=DIV expression              # BinaryOperatorExp
           | expression op=MOD expression              # BinaryOperatorExp
           | expression op=PLUS expression             # BinaryOperatorExp
           | expression op=MINUS expression            # BinaryOperatorExp
           | expression op=GREATER_THAN expression     # BinaryOperatorExp
           | expression op=GREATER_THAN_EQ expression  # BinaryOperatorExp
           | expression op=LESS_THAN expression        # BinaryOperatorExp
           | expression op=LESS_THAN_EQ expression     # BinaryOperatorExp
           | expression op=EQUAL expression            # BinaryOperatorExp
           | expression op=NOT_EQUAL expression        # BinaryOperatorExp
           | expression op=AND expression              # BinaryOperatorExp
           | expression op=OR expression               # BinaryOperatorExp
           ;


variableReference : IDENT ;

literal : intLiteral | boolLiteral | charLiteral | stringLiteral | pairLiteral ;
intLiteral    : (PLUS | MINUS)? NUMBER ;
boolLiteral   : TRUE | FALSE ;
charLiteral   : CHAR_LITERAL ;
stringLiteral : STRING_LITERAL ;
pairLiteral      : NULL_PAIR ;


unaryOperator : NOT | MINUS | LEN | ORD | CHR ;
