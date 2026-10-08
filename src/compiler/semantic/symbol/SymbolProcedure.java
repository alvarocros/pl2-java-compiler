package compiler.semantic.symbol;

import java.util.ArrayList;

import es.uned.lsi.compiler.semantic.ScopeIF;
import es.uned.lsi.compiler.semantic.symbol.SymbolBase;
import es.uned.lsi.compiler.semantic.type.TypeIF;

/**
 * Class for SymbolProcedure.
 */

// TODO: Student work
//       Include properties to characterize procedure calls

public class SymbolProcedure
    extends SymbolBase
{
	private ArrayList<SymbolParameter> parameters; 
    /**
     * Constructor for SymbolProcedure.
     * @param scope The declaration scope.
     * @param name The symbol name.
     * @param type The symbol type.
     */
    public SymbolProcedure (ScopeIF scope, 
                            String name,
                            TypeIF type)
    {
        super (scope, name, type);
        this.parameters = new ArrayList<SymbolParameter>();
    } 
    
    /**
     * Añade un parámetro a la lista de la función.
     * Lo usamos en el parser.cup al detectar los parámetros en el ámbito.
     */
    public void addParameter(SymbolParameter parameter) {
        if (parameter != null) {
            this.parameters.add(parameter);
        }
    }

    /**
     * Devuelve la lista de parámetros para comprobar las llamadas.
     */
    public ArrayList<SymbolParameter> getParameters() {
        return this.parameters;
    }

    /**
     * Opcional: Devuelve el número de parámetros (útil para errores rápidos)
     */
    public int getParameterCount() {
        return this.parameters.size();
    }
}
