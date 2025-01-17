package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tree.ListDeclMethod;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

/**
 * Definition of a class.
 *
 * @author gl31
 * @date 01/01/2025
 */
public class ClassDefinition extends TypeDefinition {


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
        this.definitionAdress = new RegisterOffset(definitionAdress, GPRegister.GB);
    }

    public int incNumberOfMethods() {
        numberOfMethods++;
        return numberOfMethods;
    }

    private int numberOfFields = 0;
    private int numberOfMethods = 0;
    private DAddr definitionAdress;
    
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


    public void codeGenMethodsVTable (DecacCompiler compiler, ListDeclMethod listMethod) {

        // store le pointeur vers chaque méthode de la classe
        for (AbstractDeclMethod m : listMethod.getList()) {
            System.out.println("index de la méthode : " + m.getMethodName().getMethodDefinition().getIndex());
            compiler.addInstruction(new LOAD(new LabelOperand( new Label (
                    "code." + this.getType().getName().getName() + "." + m.getMethodName().getName().getName()
            )), GPRegister.R0));

            MethodDefinition metDef = (MethodDefinition) m.getMethodName().getDefinition();
            compiler.addInstruction(new STORE(
                    GPRegister.R0, new RegisterOffset(compiler.headOfGBStack + metDef.getIndex(), GPRegister.GB)
            ));
        }
    }
    
}
