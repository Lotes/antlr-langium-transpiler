lexer grammar LexBasic;
fragment EscAny
   : Esc .
   ;

fragment Esc
   : '\\'
   ;

fragment NameChar
   : '\u203F' .. '\u2040'
   ;