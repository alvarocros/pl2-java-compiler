package compiler.syntax.nonTerminal;

import java.util.List;

import es.uned.lsi.compiler.intermediate.IntermediateCodeBuilder;
import es.uned.lsi.compiler.intermediate.QuadrupleIF;

public class Sentencia extends NonTerminal{
	
	private boolean garantizaReturn = false;
	 
	public Sentencia () {
		super();
	}
	
	public boolean isGarantizaReturn() {
	    return garantizaReturn;
	}
	
	public void setGarantizaReturn(boolean garantizaReturn) {
	    this.garantizaReturn = garantizaReturn;
	}
	
	
	public void concatenar(Sentencia listaSiguiente) {
	    List<QuadrupleIF> miLista = getIntermediateCode();
	    if (listaSiguiente != null && listaSiguiente.getIntermediateCode() != null) {
	        miLista.addAll(listaSiguiente.getIntermediateCode());
	    }
	    // Si alguna sentencia garantiza return, la lista lo garantiza
	    if (listaSiguiente != null && listaSiguiente.isGarantizaReturn()) {
	        this.garantizaReturn = true;
	    }
	}
}
