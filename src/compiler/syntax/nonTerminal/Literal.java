package compiler.syntax.nonTerminal;

import es.uned.lsi.compiler.semantic.type.TypeIF;

public class Literal extends NonTerminal{
	
	private TypeIF type;
	private String valor;
	
	public Literal(String valor, TypeIF type) {
		
		this.type = type;
		this.valor = valor;
		
	}
	
	public String getValor() {
		return this.valor;
	}
	
	public TypeIF getType() {
		return this.type;
	}

}
