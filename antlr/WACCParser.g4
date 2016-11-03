parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

// program
prog : BEGIN func* stat END EOF;

// functions
func : type ident L_BRACKET param_list? R_BRACKET IS stat END ;

param_list : param (COMMA param)* ;

param : type ident ;


// statement
stat : NOP
     | type ident EQUALS assign_rhs
     | assign_lhs EQUALS assign_rhs
     | READ assign_lhs
     | FREE expr
     | RETURN expr
     | EXIT expr
     | PRINT expr
     | PRINTLN expr
     | IF expr THEN stat ELSE stat FI
     | WHILE expr DO stat DONE
     | BEGIN stat END
     | stat SEMICOLON stat
     ;

// assignment
assign_lhs : ident
           | array_elem
           | pair_elem
           ;

assign_rhs : expr
           | array_liter
           | NEWPAIR L_BRACKET expr COMMA expr R_BRACKET
           | pair_elem
           | CALL ident L_BRACKET arg_list? R_BRACKET
           ;

arg_list : expr (COMMA expr)* ;

// types
type : base_type
     | array_type
     | pair_type
     ;

base_type : INT | BOOL | CHAR | STRING ;

array_type : (base_type | pair_type) (L_SQ_BRACKET R_SQ_BRACKET)+ ;

pair_type : PAIR L_BRACKET pair_elem_type COMMA pair_elem_type R_BRACKET ;

pair_elem_type : base_type
               | array_type
               | PAIR
               ;

// expressions
expr : int_liter
     | bool_liter
     | char_liter
     | str_liter
     | pair_liter
     | ident
     | array_elem
     | unary_oper expr
     | expr binary_oper expr
     | L_BRACKET expr R_BRACKET
     ;

// operators
unary_oper : BANG
           | MINUS
           | LEN
           | ORD
           | CHR
           ;

binary_oper : STAR
            | DIV
            | MOD
            | PLUS
            | MINUS
            | GREATER_THAN
            | GREATER_THAN_EQ
            | LESS_THAN
            | LESS_THAN_EQ
            | EQUAL
            | NOT_EQUAL
            | AND
            | OR
            ;

// identifier
ident : IDENT;

// array and pair elements
array_elem : ident (L_SQ_BRACKET expr R_SQ_BRACKET)+ ;

pair_elem : FST expr
          | SND expr
          ;

// literals
int_sign : PLUS | MINUS ;

int_liter : int_sign? NUMBER ;

bool_liter : TRUE | FALSE ;

char_liter : CHAR_LITER ;

str_liter : STR_LITER;

array_liter : L_SQ_BRACKET (expr (COMMA expr)*)? R_SQ_BRACKET ;

pair_liter : NULL ;

// comment
comment : COMMENT;
