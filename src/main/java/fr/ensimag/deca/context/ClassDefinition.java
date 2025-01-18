package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tree.ListDeclMethod;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.Map;

/**
 * Definition of a class.
 *
 * @author gl31
 * @date 01/01/2025
 */
public class ClassDefinition extends TypeDefinition {

    private int numberOfFields = 0;
    private int numberOfMethods = 0;
    private DAddr definitionAdress;

    private ArrayList<String> methodArray = new ArrayList<>();

    public ArrayList<String> getMethodArray() {
        return methodArray;
    }

    public void setMethodArray(ArrayList<String> methodArray) {
        this.methodArray = methodArray;
    }

    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void incNumberOfFields() {
        this.numberOfFields++;
    }

    public int getNumberOfMethods() {
        return numberOfMethods;
    }

    private int methodIndex = 1;

    public int getMethodIndex() {
        return methodIndex;
    }

    public void setMethodIndex(int methodIndex) {
        this.methodIndex = methodIndex;
    }

    public void setNumberOfMethods(int n) {
        Validate.isTrue(n >= 0);
        numberOfMethods = n;
    }

    public DAddr getDefinitionAdress() {
        return definitionAdress;
    }

    public void setDefinitionAdress(int definitionAdress) {
        System.out.println("on set l'adresse de " + this.getType().getName().getName() + " à : " + definitionAdress + " (GB)");
        this.definitionAdress = new RegisterOffset(definitionAdress, GPRegister.GB);
    }

    public int incNumberOfMethods() {
        numberOfMethods++;
        return numberOfMethods;
    }
    
    @Override
    public boolean isClass() {
        return true;
    }
    
    @Override
    public ClassType getType() {
        // Cast succeeds by construction because the type has been correctly set
        // in the constructor.
        return (ClassType) super.getType();
    };

    public ClassDefinition getSuperClass() {
        return superClass;
    }

    private final EnvironmentExp members;
    private final ClassDefinition superClass; 

    public EnvironmentExp getMembers() {
        return members;
    }

    public ClassDefinition(ClassType type, Location location, ClassDefinition superClass) {
        super(type, location);
        EnvironmentExp parent;
        if (superClass != null) {
            parent = superClass.getMembers();
        } else {
            parent = null;
        }
        members = new EnvironmentExp(parent);
        this.superClass = superClass;
    }

    
}
