grammar WACCParser;
import WACCLexer;

prog : BEGIN func* stat END EOF;

func : type ident L_BRACKET param_list? R_BRACKET IS stat END ;

param_list : param (COMMA param)* ;

param : type ident ;

//stat_list : stat SEMICOLON stat_list | stat ;

stat : SKIP
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

pair_elem : FST expr
          | SND expr
          ;

type : base_type
     | array_type
     | pair_type
     ;

base_type : INT | BOOL | CHAR | STRING ;

array_type : type L_SQ_BRACKET R_SQ_BRACKET ;

pair_type : PAIR L_BRACKET pair_elem_type COMMA pair_elem_type R_BRACKET ;

pair_elem_type : base_type
               | array_type
               | PAIR
               ;

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

ident : (UNDERSCORE | LOWERCASE | UPPERCASE) (UNDERSCORE | LOWERCASE | UPPERCASE | DIGIT )* ;

array_elem : ident (L_SQ_BRACKET expr R_SQ_BRACKET)+ ;

int_liter : int_sign? DIGIT ;

int_sign : PLUS | MINUS ;

bool_liter : TRUE | FALSE ;

char_liter : SINGLE_QUOTE char SINGLE_QUOTE ;

str_liter : DOUBLE_QUOTE char* DOUBLE_QUOTE ;

char : CHAR_NO_QUOTES_BACKSLASH | BACKSLASH ESCAPED_CHAR ;

array_liter : L_SQ_BRACKET (expr (COMMA expr)*)? R_SQ_BRACKET ;

pair_liter : NULL ;

comment : HASH CHAR_NOT_EOL EOF ;
