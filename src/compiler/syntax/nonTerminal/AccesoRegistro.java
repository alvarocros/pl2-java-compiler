package compiler.syntax.nonTerminal;

import java.util.ArrayList;

import compiler.intermediate.Temporal;
import es.uned.lsi.compiler.semantic.ScopeIF;
import es.uned.lsi.compiler.semantic.type.TypeIF;

public class AccesoRegistro extends Expresion{
	
	private Expresion expresionPadre;
	private String nombreCampo;

	public AccesoRegistro(Expresion padre, String nombreCampo, TypeIF tipoCampo, ScopeIF scope) {
		super();
		this.expresionPadre = padre;
        this.nombreCampo = nombreCampo;
		// TODO Auto-generated constructor stub
     // 2. Seteamos el Tipo
        this.setTipo(tipoCampo);

        // 3. ACTUALIZACIÓN CLAVE: El "lugar" ahora es un Temporal
        // Construimos el nombre del acceso (ej: "persona.nombre")
        String nombreAcceso = padre.getTemporal().toString() + "." + nombreCampo;
        this.setTemporal(new Temporal(nombreAcceso, scope));

        // 4. Mantenemos el estado de variable
        this.setesVariable(true);

        // 5. ESCALADO DE CÓDIGO: 
        // Si el padre tenía código previo (ej: p[i].x), lo heredamos.
        if (padre.getIntermediateCode() != null) {
            this.setIntermediateCode(padre.getIntermediateCode());
        } else {
            this.setIntermediateCode(new ArrayList<>());
        }
	}
	
	public Expresion getBaseExpresion() { return this.expresionPadre;}
	public String getCampo() {return this.nombreCampo;}
	
	// --- MÉTODOS LISTOS PARA EL FUTURO ---

    public boolean isNested() {
        // Si el padre es, a su vez, una ExpresionRegistro, es anidado
        return (expresionPadre instanceof AccesoRegistro);
    }

    public int getOffset() {
        // AHORA: Devuelve 0. 
        // LUEGO: Aquí implementarás la lógica de buscar en el TypeRegistro 
        // del 'padre' el desplazamiento del campo 'nombreCampo'.
        return 0; 
    }

}
