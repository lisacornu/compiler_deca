package fr.ensimag.deca.context;
import java.util.*;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl31
 * @date 01/01/2025
 */
public class EnvironmentExp {

    EnvironmentExp parentEnvironment;
    Map<Symbol, Definition> envMap;
    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
        this.envMap = new HashMap<Symbol,Definition>();
    }
    public Map<Symbol, Definition> getMap(){
        return envMap;
    }
    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public ExpDefinition get(Symbol key) {
        if (!(key instanceof Symbol)){
            throw new UnsupportedOperationException("This is not a Symbol");
        }
        if (envMap.containsKey(key)){
            return (ExpDefinition) envMap.get(key);
        } 
        // If not found, search in the parent environment (recursively)
        if (this.parentEnvironment != null) {
            return parentEnvironment.get(key);
        }
        return null;
        
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary 
     * - or, hides the previous declaration otherwise.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException {
        if (!(def instanceof Definition)){
            throw new UnsupportedOperationException("This is not a definition");
        }
        if (envMap.containsKey(name)){
            throw new DoubleDefException();
        } else {
            envMap.put(name,(Definition) def);
        }
    }

}
