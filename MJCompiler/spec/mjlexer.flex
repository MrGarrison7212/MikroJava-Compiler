package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

%%

%{

	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" " 	{ }
"\b" 	{ }
"\t" 	{ }
"\r\n" 	{ }
"\f" 	{ }

"program"   { return new_symbol(sym.PROG, yytext()); }
"print" 	{ return new_symbol(sym.PRINT, yytext()); }
"read" 		{ return new_symbol(sym.READ, yytext()); }	
"return" 	{ return new_symbol(sym.RETURN, yytext()); }
"void" 		{ return new_symbol(sym.VOID, yytext()); }
"const" 	{ return new_symbol(sym.CONST, yytext()); }
"new" 		{ return new_symbol(sym.NEW, yytext()); }
"return" 	{ return new_symbol(sym.RETURN, yytext()); }
"goto" 		{ return new_symbol(sym.GOTO, yytext()); }

"$" 		{ return new_symbol(sym.DOL, yytext()); }
"^" 		{ return new_symbol(sym.CAP, yytext()); }
"@" 		{ return new_symbol(sym.AT, yytext()); }
"|" 		{ return new_symbol(sym.VLINE, yytext()); }
"!" 		{ return new_symbol(sym.REPL, yytext()); }
"#" 		{ return new_symbol(sym.HASH, yytext()); }
"final" 	{ return new_symbol(sym.FINAL, yytext()); }



"+" 		{ return new_symbol(sym.PLUS, yytext()); }
"=" 		{ return new_symbol(sym.EQUAL, yytext()); }
"-" 		{ return new_symbol(sym.MINUS, yytext()); }
"*" 		{ return new_symbol(sym.MUL, yytext()); }
"/" 		{ return new_symbol(sym.DIV, yytext()); }
"%" 		{ return new_symbol(sym.MOD, yytext()); }
"++" 		{ return new_symbol(sym.INC, yytext()); }
"--" 		{ return new_symbol(sym.DEC, yytext()); }
"." 		{ return new_symbol(sym.DOT, yytext()); }
"?" 		{ return new_symbol(sym.QUE, yytext()); }
":" 		{ return new_symbol(sym.COL, yytext()); }
">" 		{ return new_symbol(sym.GT, yytext()); }
"<" 		{ return new_symbol(sym.LT, yytext()); }
"==" 		{ return new_symbol(sym.EQ, yytext()); }
"!=" 		{ return new_symbol(sym.NE, yytext()); }
">=" 		{ return new_symbol(sym.GE, yytext()); }
"<=" 		{ return new_symbol(sym.LE, yytext()); }




";" 		{ return new_symbol(sym.SEMI, yytext()); }
"," 		{ return new_symbol(sym.COMMA, yytext()); }
"(" 		{ return new_symbol(sym.LPAREN, yytext()); }
")" 		{ return new_symbol(sym.RPAREN, yytext()); }
"[" 		{ return new_symbol(sym.LSQUARE, yytext()); }
"]"			{ return new_symbol(sym.RSQUARE, yytext()); }
"{" 		{ return new_symbol(sym.LBRACE, yytext()); }
"}"			{ return new_symbol(sym.RBRACE, yytext()); }

<YYINITIAL> "//" 		     { yybegin(COMMENT); }
<COMMENT> .      { yybegin(COMMENT); }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }

"true"|"false"	{return new_symbol (sym.BOOLCONST, new Boolean(yytext())); }
[0-9]+  		{ return new_symbol(sym.NUMCONST, new Integer (yytext())); }
"'"."'"			{ return new_symbol(sym.CHARCONST, new Character (yytext().charAt(1))); }
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* 	{return new_symbol (sym.IDENT, yytext()); }

. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1)); }






