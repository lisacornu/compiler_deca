package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

// A FAIRE: étendre cette classe pour traiter la partie "avec objet" de Déca
/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl31
 * @date 01/01/2025
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {
        
        envTypes = new HashMap<Symbol, TypeDefinition>();
        
        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("String");
        STRING = new StringType(stringSymb);
        // envTypes.put(stringSymb, new TypeDefinition(STRING, Location.BUILTIN));

        // not added to envTypes, it's not visible for the user.
        
    
    }

    /**
     * Add of a class in the environnement
     * @param compiler
     * @param nameOfClass
     * @throws DoubleDefException
     */
    public void addOfTypeClass(DecacCompiler compiler, String nameOfClass) throws DoubleDefException{
        Symbol newSymb = compiler.createSymbol(nameOfClass);
        if (defOfType(newSymb)==null){
            envTypes.put(newSymb, new TypeDefinition(new ClassType(newSymb), null));
        } else{
            throw new DoubleDefException();
        }
    }
    private final Map<Symbol, TypeDefinition> envTypes;

    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public final VoidType    VOID;
    public final IntType     INT;
    public final FloatType   FLOAT;
    public final StringType  STRING;
    public final BooleanType BOOLEAN;

}
