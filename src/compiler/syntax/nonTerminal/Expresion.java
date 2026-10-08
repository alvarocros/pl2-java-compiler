package compiler.syntax.nonTerminal;

import es.uned.lsi.compiler.intermediate.TemporalIF;
import es.uned.lsi.compiler.semantic.type.TypeIF;

public class Expresion extends NonTerminal{
	private TypeIF tipo;
	private TemporalIF temporal;
    private boolean esVariable;//Indica si el valor es el inicial un numero o un boleano como 5 o true
    private Object dir;
    private String lexema;

    public Expresion(TypeIF tipo, boolean esVariable) {
        super();// Inicializa la lista de cuádruplos de la clase base
        this.tipo = tipo;
        this.esVariable = esVariable;
    }
    
    public Expresion() {
    	super();
    }
    public TypeIF getTipo() { return tipo; }
    public boolean isEsVariable() { return esVariable; }
    
    public void setTipo(TypeIF tipo) {
    	this.tipo = tipo;
    }
   
    
    public void setesVariable(boolean esVariable) {
    	this.esVariable = esVariable;
    }
    
    public boolean EsVariable() {
    	return esVariable;
    }
    
    public void setTemporal(TemporalIF temp) { this.temporal = temp; }
    public TemporalIF getTemporal() { return temporal; }
    public void setExtraInfo(Object dir) {this.dir = dir;}
    public Object getExtraInfo() {return this.dir;}
    public void setLexema(String lexema) {this.lexema = lexema;}
    public String getLexema() {return lexema;}
}

