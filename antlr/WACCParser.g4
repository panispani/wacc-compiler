parser grammar WACCParser;
options { tokenVocab=WACCLexer; }

// Top-level rule
program : BEGIN struct* function* sequence END EOF;

struct : STRUCT name=IDENT (IS parent=IDENT)? (structMemberDeclaration SEMICOLON)+ (function)* ;
structMemberDeclaration: type IDENT ;
structType : STRUCT IDENT ;
structLiteral : LC (expression (COMMA expression)*)? RC ;
structMember : IDENT (DOT IDENT)+ ;

function : type IDENT LP parameterList? RP IS sequence END ;
parameterList : parameter (COMMA parameter)* ;
parameter : type IDENT ;

invocation : functionCall
           | instanceMethodCall
           ;

instanceMethodCall : CALL self=IDENT DOT methodName=IDENT LP argumentList? RP ;

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
              | FOR init=statement SEMICOLON
                    cond=expression SEMICOLON
                    step=statement DO body=sequence DONE                                    # For
              ;

assignLhs : variableReference # AssignLhsIdent
          | arrayElement      # AssignLhsArrayElement
          | pairElement       # AssignLhsPairElement
          | structMember      # AssignLhsStructMember
          ;

assignRhs : expression             # AssignRhsExpression
          | arrayLiteral           # AssignRhsArrayLiteral
          | pairConstructor        # AssignRhsPairConstructor
          | pairElement            # AssignRhsPairElement
          | invocation             # AssignRhsFunctionCall
          | structLiteral          # AssignRhsStructLiteral
          | structMember           # AssignRhsStructMember
          ;

type : primitiveType
     | arrayType
     | pairType
     | structType
     ;

primitiveType : INT | BOOL | CHAR | STRING ;
notNestedArrayType: primitiveType | pairType  | structType;

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
           | structMember                              # StructMemberExp
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

literal : decLiteral
        | hexLiteral
        | octalLiteral
        | binaryLiteral
        | boolLiteral
        | charLiteral
        | stringLiteral
        | pairLiteral
        ;

decLiteral    : (PLUS | MINUS)? DECIMAL ;
hexLiteral    : (PLUS | MINUS)? HEX ;
octalLiteral  : (PLUS | MINUS)? OCTAL ;
binaryLiteral : (PLUS | MINUS)? BINARY ;
boolLiteral   : TRUE | FALSE ;
charLiteral   : CHAR_LITERAL ;
stringLiteral : STRING_LITERAL ;
pairLiteral      : NULL_PAIR ;

unaryOperator : NOT | MINUS | LEN | ORD | CHR ;
