package compiler.lexical;

import compiler.syntax.sym;
import compiler.lexical.Token;
import es.uned.lsi.compiler.lexical.ScannerIF;
import es.uned.lsi.compiler.lexical.LexicalError;
import es.uned.lsi.compiler.lexical.LexicalErrorManager;

// DIRECTIVAS

%%
 
%public
%class Scanner
%char
%line
%column
%cup
%unicode
%ignorecase
%full
%implements ScannerIF
%scanerror LexicalError


%{
  LexicalErrorManager lexicalErrorManager = new LexicalErrorManager ();
  private int numLineas = 0;
%}  

//MACROS
  
//delimitadores
DelimCadena= \"
ParentesisAp= "("
ParentesisCierre= ")"
DelimComentario= "--"
DelimLista= ","
DelimPuntoComa= ";"
DelimTipo= ":"

//Comentarios
CaracterEntrada=[^\r\n]
Comentario= {DelimComentario} {CaracterEntrada}*

//Espacio en blanco
EspacioBlanco=[ \t\r\n\f]

//Constantes literales
NUMERO=[0-9]
LiteralEntero= 0|[1-9]{NUMERO}*
LiteralBooleano= {True} | {False}
ContenidoCadenaCaracteres= [^\"\r\n]
LiteralCadenaCaracteres= {DelimCadena} {ContenidoCadenaCaracteres}* {DelimCadena}
LiteralCadenaSinCierre= {DelimCadena} {ContenidoCadenaCaracteres}* [\r\n]
LiteralCadenaSinCierreEOF = {DelimCadena} {ContenidoCadenaCaracteres}* [\eof]

//palabras reservadas
and= "and"
begin= "begin"
Boolean= "Boolean"
constant= "constant"
else= "else"
end= "end"
False= "False"
function= "function"
if= "if"
Integer= "Integer"
is= "is"
loop= "loop"
out= "out"
procedure= "procedure"
putline= "Put_line"
record= "record"
return= "return"
then= "then"
True= "True"
type= "type"
while= "while"

//operadores
Resta= "-"
Multiplicacion= "*"
Mayor= ">"
Distinto= "/="
Asignacion= ":="
Acceso= "."

//Identificador
Digito=[0-9]
Letra=[A-Za-z]
Identificador= {Letra} ({Letra}|{Digito})*



//Fin y fin de linea
fin = "fin"{EspacioBlanco}
FinLinea=\r|\n|\r\n

// REGLAS PATRÓN - ACCIÓN

%%

<YYINITIAL> 
{

     {DelimCadena}        {
    					   Token token = new Token (sym.DELIMCADENA);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;		
    					  }    
        
     {ParentesisAp}       {
    					   Token token = new Token (sym.PARENTESISAP);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;		
    					  }  
    					
    {ParentesisCierre}    {
    					   Token token = new Token (sym.PARENTESISCIERRE);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
    			   		  }      
    					
    
     {DelimComentario}    {
    					   Token token = new Token (sym.DELIMCOMENTARIO);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;		
    					  }           
    					
     {DelimLista}         {
    					   Token token = new Token (sym.DELIMLISTA);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;				
    					  }           
    					
     {DelimPuntoComa}     {
    					   Token token = new Token (sym.DELIMPUNTOCOMA);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;		
    					  }  
    					
     {DelimTipo}          {
    					   Token token = new Token (sym.DELIMTIPO);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;			
    					  }                               


     {Comentario}         {                  
                          }
                        
    
     {LiteralEntero}      {  
                           
        				  	Token token = new Token (sym.ENTERO);
                          	token.setLine (yyline + 1);
                          	token.setColumn (yycolumn + 1);
                          	token.setLexema (yytext ());     
                          	return token;                                                 
                          
                          }
                        
     
     {LiteralBooleano}    {  
                           Token token = new Token (sym.BOOLEANO);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
                          }
                        
               
{LiteralCadenaCaracteres} {  
                           Token token = new Token (sym.CADENACARACTERES);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           //token.setLexema (yytext ());
                           token.setLexema (yytext ().substring(1,yytext().length()-1)); 
           			       return token;
                          }          
                          
{LiteralCadenaSinCierre}   {
						    LexicalError error = new LexicalError ();
                            error.setLine (yyline + 1);
                            error.setColumn (yycolumn + 1);
                            error.setLexema (yytext ().substring(0,yytext().length()-1));
                            lexicalErrorManager.lexicalFatalError ("Error en linea = " + error.getLine() +
                            	", columna = " + error.getColumn() + ". " + error.getLexema() + 
                            ". Cadena de caracteres sin cerrar.");

                           }                          
                                
{LiteralCadenaSinCierreEOF}   {
						    LexicalError error = new LexicalError ();
                            error.setLine (yyline + 1);
                            error.setColumn (yycolumn + 1);
                            error.setLexema (yytext ().substring(0,yytext().length()));
                            lexicalErrorManager.lexicalFatalError ("Error en linea = " + error.getLine() +
                            	", columna = " + error.getColumn() + ". " + error.getLexema() + 
                            ". Cadena de caracteres sin cerrar.");

                           }                          
                                                                
                                           
     {and} 	              {
			   			   Token token = new Token(sym.AND);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	 {begin} 	          {
			   			   Token token = new Token(sym.BEGIN);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	 {Boolean} 	          {
			               Token token = new Token(sym.BOOLEAN);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	 {constant} 	      {
			               Token token = new Token(sym.CONSTANT);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
     {else} 	          {
			               Token token = new Token(sym.ELSE);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	 {end}                {
			               Token token = new Token(sym.END);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
			
	 {function} 	      {
			               Token token = new Token(sym.FUNCTION);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	 {if} 	              {
			   			   Token token = new Token(sym.IF);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	{Integer} 	          {
			               Token token = new Token(sym.INTEGER);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	{is} 	              {
			               Token token = new Token(sym.IS);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	{loop} 	              {
			               Token token = new Token(sym.LOOP);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	{out} 	              {
			               Token token = new Token(sym.OUT);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	{procedure} 	      {
			               Token token = new Token(sym.PROCEDURE);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }

	
			
	{record} 	          {
			               Token token = new Token(sym.RECORD);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	{return} 	          {
			   			   Token token = new Token(sym.RETURN);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	{then}     	          {
			               Token token = new Token(sym.THEN);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }

    {type} 	              {
			               Token token = new Token(sym.TYPE);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
	{while} 	          {
			               Token token = new Token(sym.WHILE);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }
			
			
    {putline} 	          {
			               Token token = new Token(sym.PUTLINE);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
			              }


    {Resta}               {  
                           Token token = new Token (sym.MINUS);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
                          }
                        
    {Multiplicacion}      {  
                           Token token = new Token (sym.MULT);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
                          }
                        
    {Mayor}               {  
                           Token token = new Token (sym.MAYOR);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
                          }
                        
    {Distinto}            {  
                           Token token = new Token (sym.DISTINTO);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
                          }
                        
    {Asignacion}          {  
                           Token token = new Token (sym.ASIGNACION);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
                          }
                        
    {Acceso}              {  
                           Token token = new Token (sym.ACCESO);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;
                          }                    
                          
    {Identificador}       {  
                           Token token = new Token (sym.IDENTIFICADOR);
                           token.setLine (yyline + 1);
                           token.setColumn (yycolumn + 1);
                           token.setLexema (yytext ());
           			       return token;   
           			      }     
           			      
  
                       			                                          
   {EspacioBlanco}	{}

   {fin} {}
   
   {FinLinea}  {++numLineas;}
   
    
    // error en caso de coincidir con ningun patron
	[^]     
                        {                                               
                           LexicalError error = new LexicalError ();
                           error.setLine (yyline + 1);
                           error.setColumn (yycolumn + 1);
                           error.setLexema (yytext ());
                           lexicalErrorManager.lexicalFatalError ("Error en la linea = " + error.getLine() +
                            ", columna = " + error.getColumn() + ".  Se ha encontrado un caracter no esperado: " + error.getLexema());
                        }
    
}


                         


