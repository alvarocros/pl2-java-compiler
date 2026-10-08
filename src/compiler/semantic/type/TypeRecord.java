package compiler.semantic.type;

import es.uned.lsi.compiler.semantic.ScopeIF;
import es.uned.lsi.compiler.semantic.symbol.SymbolIF;
import es.uned.lsi.compiler.semantic.type.TypeBase;

/**
 * Class for TypeRecord.
 */

// TODO: Student work
//       Include properties to characterize records

public class TypeRecord
    extends TypeBase
{   
      
	private ScopeIF scopeInterno;
    /**
     * Constructor for TypeRecord.
     * @param scope The declaration scope.
     */
    public TypeRecord (ScopeIF scope)
    {
    	//Como abrimos un ambito estamos en el ambito dentro de record 
    	//pero su a,bito de visibilidad es el padre
        super (scope.getParentScope() );
        this.scopeInterno = scope;
    }

    /**
     * Constructor for TypeRecord.
     * @param scope The declaration scope.
     * @param name The name of the type.
     */
    public TypeRecord (ScopeIF scope, String name)
    {   
    	//Como abrimos un ambito estamos en el ambito dentro de record 
    	//pero su a,bito de visibilidad es el padre
        super (scope.getParentScope() , name);
        this.scopeInterno = scope;
    }
   
    /**
     * Constructor for TypeRecord.
     * @param record The record to copy.
     */
    //duplica la definicion del tipo sin alterar el original
    public TypeRecord (TypeRecord record)
    {
        super (record.getScope (), record.getName ());
        this.scopeInterno = record.getInternalScope();
    } 
 
    
    /**
     * Returns the size of the type.
     * @return the size of the type.
     */
    @Override
    public int getSize ()
    {
        // TODO: Student work
    	if (scopeInterno != null) {
            int total = 0;
            for (SymbolIF simbolo : scopeInterno.getSymbolTable().getSymbols()) {
                total += simbolo.getType().getSize();
            }
            return total;
        } else {
            return 1; // Tipo simple
        }
    }
    
    
    public ScopeIF getInternalScope() {
        return scopeInterno;
    }
}
