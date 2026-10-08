package compiler.code;

import java.util.Arrays;
import java.util.List;

import compiler.intermediate.Label;
import compiler.intermediate.Temporal;
import compiler.intermediate.Value;
import compiler.intermediate.Variable;
import compiler.semantic.type.TypeSimple;

import es.uned.lsi.compiler.code.ExecutionEnvironmentIF;
import es.uned.lsi.compiler.code.MemoryDescriptorIF;
import es.uned.lsi.compiler.code.RegisterDescriptorIF;
import es.uned.lsi.compiler.intermediate.OperandIF;
import es.uned.lsi.compiler.intermediate.QuadrupleIF;

/**
 * Class for the ENS2001 Execution environment.
 */

public class ExecutionEnvironmentEns2001 
    implements ExecutionEnvironmentIF
{    
    private final static int      MAX_ADDRESS = 65535; 
    private final static String[] REGISTERS   = {
       ".PC", ".SP", ".SR", ".IX", ".IY", ".A", 
       ".R0", ".R1", ".R2", ".R3", ".R4", 
       ".R5", ".R6", ".R7", ".R8", ".R9"
    };
    
    private RegisterDescriptorIF registerDescriptor;
    private MemoryDescriptorIF   memoryDescriptor;
    
    /**
     * Constructor for ENS2001Environment.
     */
    public ExecutionEnvironmentEns2001 ()
    {       
        super ();
    }
    
    /**
     * Returns the size of the type within the architecture.
     * @return the size of the type within the architecture.
     */
    @Override
    public final int getTypeSize (TypeSimple type)
    {      
        return 1;  
    }
    
    /**
     * Returns the registers.
     * @return the registers.
     */
    @Override
    public final List<String> getRegisters ()
    {
        return Arrays.asList (REGISTERS);
    }
    
    /**
     * Returns the memory size.
     * @return the memory size.
     */
    @Override
    public final int getMemorySize ()
    {
        return MAX_ADDRESS;
    }
           
    /**
     * Returns the registerDescriptor.
     * @return Returns the registerDescriptor.
     */
    @Override
    public final RegisterDescriptorIF getRegisterDescriptor ()
    {
        return registerDescriptor;
    }

    /**
     * Returns the memoryDescriptor.
     * @return Returns the memoryDescriptor.
     */
    @Override
    public final MemoryDescriptorIF getMemoryDescriptor ()
    {
        return memoryDescriptor;
    }

    /**
     * Translate a quadruple into a set of final code instructions. 
     * @param cuadruple The quadruple to be translated.
     * @return a quadruple into a set of final code instructions. 
     */
    @Override
    public final String translate(QuadrupleIF quadruple) {
    	 //TODO: Student work
        StringBuilder b = new StringBuilder();
        String op = quadruple.getOperation();

        String o1 = operacion(quadruple.getFirstOperand());
        String o2 = operacion(quadruple.getSecondOperand());
        String r  = operacion(quadruple.getResult());

        b.append("; " + quadruple.toString() + "\n");

        switch (op) {
            case "VARGLOBAL":
                // r es la variable (dirección absoluta), o1 es el 0
                b.append("MOVE #0, " + r + "\n");
                break;

            case "MV":
                b.append("MOVE " + o1 + ", " + r + "\n");
                break;

            case "MVA":
            	// Queremos la dirección, no el contenido
                String dir = o1.replace("/", "");
                b.append("MOVE #" + dir + ", " + r + "\n");
                break;

            case "MVP":
                // Leer valor de la dirección contenida en o1
            	 b.append("MOVE #" + o1.replace("/","") + ", .R0\n");
            	 b.append("MOVE [.R0], " + r + "\n");
            	 break;
            case "MVPP"://(doble indirección) para campos de registro:
                b.append("MOVE #" + o1.replace("/","") + ", .R0\n");
                b.append("MOVE [.R0], .R0\n");          // R0 = dirección del campo
                b.append("MOVE [.R0], " + r + "\n");    // lee el valor en esa dirección
                break;

            case "STP":
            	b.append("MOVE #" + r.replace("/","") + ", .R0\n");  // R0 = 65532
                b.append("MOVE [.R0], .R0\n");                        // R0 = contenido de 65532 = 65535
                b.append("MOVE " + o1 + ", [.R0]\n");                 // guarda /65531 en dirección 65535
                break;

            case "ADD":
                b.append("MOVE " + o1 + ", .A\n");
                b.append("ADD " + o2 + ", .A\n");
                b.append("MOVE .A, " + r + "\n");
                break;

            case "SUB":
                    b.append("SUB " + o1 + ", " + o2 + "\n");
                    b.append("MOVE .A, " + r + "\n");
                    break;
                    
            case "MUL":
                b.append("MOVE " + o1 + ", .A\n");
                b.append("MUL " + o2 + ", .A\n");
                b.append("MOVE .A, " + r + "\n");
                break;

            case "AND":
                String nombreAND = r.replace("/", "");
                b.append("CMP #0, " + o1 + "\n");
                b.append("BZ /andF" + nombreAND + "\n");
                b.append("CMP #0, " + o2 + "\n");
                b.append("BZ /andF" + nombreAND + "\n");
                b.append("MOVE #1, " + r + "\n");
                b.append("BR /andE" + nombreAND + "\n");
                b.append("andF" + nombreAND + ":\n");
                b.append("MOVE #0, " + r + "\n");
                b.append("andE" + nombreAND + ":\n");
                break;

            case "GR":
                String nombreGR = r.replace("/", "");
                b.append("MOVE " + o1 + ", .A\n");  // A = o1 = 2
                b.append("SUB " + o2 + ", .A\n");   // A = o2 - o1 = 1 - 2 = -1 (negativo si o1>o2)
                b.append("BZ /grE" + nombreGR + "\n");
                b.append("BP /grE" + nombreGR + "\n"); // positivo: o2>o1, falso
                b.append("MOVE #1, " + r + "\n");
                b.append("BR /grE2" + nombreGR + "\n");
                b.append("grE" + nombreGR + ":\n");
                b.append("MOVE #0, " + r + "\n");
                b.append("grE2" + nombreGR + ":\n");
                break;

            case "NE":
                // Distinto: o1 /= o2
            	String nombreNE = r.replace("/", "");
                b.append("CMP " + o2 + ", " + o1 + "\n");
                b.append("BNZ /neT" + nombreNE + "\n");
                b.append("MOVE #0, " + r + "\n");
                b.append("BR /neE" + nombreNE + "\n");
                b.append("neT" + nombreNE + ":\n");
                b.append("MOVE #1, " + r + "\n");
                b.append("neE" + nombreNE + ":\n");
                break;

            case "BRF":
                // Saltar si falso (0)
                b.append("CMP #0, " + r + "\n");
                b.append("BZ /" + o1 + "\n");
                break;

            case "BR":
                b.append("BR /" + r + "\n");
                break;

            case "INL":
                // Etiqueta
                b.append(r + ":\n");
                break;

            case "HALT":
                b.append("HALT\n");
                break;

            case "WRITEINT":
                b.append("WRINT " + r + "\n");
                break;
                
            case "CADENA_BOOL_TRUE":
                b.append("wbTrue: DATA \"true\"\n");
                break;

            case "CADENA_BOOL_FALSE":
            	b.append("wbFalseStr: DATA \"false\"\n");
                break;
                
            case "WRITEBOOL":
            	String nombreWB = r.replace("/", "");
                b.append("CMP #0, " + r + "\n");
                b.append("BZ /wbF" + nombreWB + "\n");
                b.append("WRSTR /wbTrue\n");
                b.append("BR /wbE" + nombreWB + "\n");
                b.append("wbF" + nombreWB + ":\n");
                b.append("WRSTR /wbFalseStr\n");
                b.append("wbE" + nombreWB + ":\n");
                break;

            case "WRITESTRING":
                b.append("WRSTR /" + o1 + "\n");
                break;

            case "WRITELN":
                b.append("WRCHAR #10\n");
                break;

            case "CADENA":
                // Declaración de cadena de caracteres en memoria
            	String textoCADENA = quadruple.getResult().toString();
                b.append(o1 + ": DATA \"" + textoCADENA + "\"\n");
                break;

            default:
                b.append("; Operacion " + op + " pendiente de implementar\n");
                break;
        }

        return b.toString();
    }
   
    /**
     * Formatea los operandos (/, # o etiquetas).
     */
    private String operacion(OperandIF o) {
        if (o == null) return null;
        if (o instanceof Variable) {
            return "/" + ((Variable)o).getAddress();
        }
        if (o instanceof Value) {
            return "#" + ((Value)o).getValue();
        }
        if (o instanceof Temporal) {
            return "/" + ((Temporal)o).getAddress();
        }
        if (o instanceof Label) {
            return ((Label)o).getName();
        }
        return null;
    }
    
    
}
