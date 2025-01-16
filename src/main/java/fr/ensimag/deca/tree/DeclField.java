package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.rmi.UnexpectedException;
import java.util.Map;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

/**
 * Declaration of a field
 * 
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public class DeclField extends AbstractDeclField {

    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier fieldName;
    final private AbstractInitialization initialization;
    

    public DeclField(Visibility visibility,AbstractIdentifier type,AbstractIdentifier fieldName, AbstractInitialization initialization) {
        this.visibility = visibility;
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(visibility.name() + " ");
        type.decompile(s);
        s.print(" ");
        fieldName.decompile(s);
        initialization.decompile(s);
        s.print(";");
        // TODO 
        
    }

    @Override
    protected void verifyFieldMembers(DecacCompiler compiler, 
                               ClassDefinition nameClass, 
                               EnvironmentExp envExp, int i) throws ContextualError {
                                
        if (type.verifyType(compiler).isVoid()){
            throw new ContextualError("The type of this field is void.", getLocation());
        }
        
        ExpDefinition expDefinition = nameClass.getMembers().get(fieldName.getName());

        if (expDefinition!=null){
            FieldDefinition fieldDefinition = (FieldDefinition) expDefinition;
        }

        FieldDefinition fieldDef = new FieldDefinition(type.getType(), getLocation(), visibility, nameClass, i);
        // if (nameClass.getSuperClass().getMembers().get(fieldName.getName())==null){
            try{
                nameClass.getSuperClass().getMembers().declare(fieldName.getName(), fieldDef);
            } catch (DoubleDefException e){
                throw new ContextualError("The field as already been declared before.", getLocation());
            }
        // } else {
        //     throw new ContextualError("You cant update the environnement", getLocation());
        // }
        fieldName.setType(type.getType());
        fieldName.setDefinition(fieldDef);
        type.setDefinition(fieldDef);
        

    }

    
    @Override
    protected void verifyFieldBody(DecacCompiler compiler, fr.ensimag.deca.context.ClassDefinition nameClass) throws ContextualError {
        // TODO : remonter un type
        // nameClass.getType();
        initialization.verifyInitialization(compiler, type.verifyType(compiler), nameClass.getMembers(), nameClass);
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // visibility.prettyPrint(s,prefix,false);
        type.prettyPrint(s, prefix,false);
        fieldName.prettyPrint(s,prefix,false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        fieldName.iter(f);
        initialization.iter(f);
    }


    private GPRegister defaultInitField(DecacCompiler compiler, GPRegister reg) {
        DVal defaultValue;
        if (type.getType().isInt()) defaultValue = new ImmediateInteger(0);
        else if (type.getType().isBoolean()) defaultValue = new ImmediateInteger(0);
        else if (type.getType().isFloat()) defaultValue = new ImmediateFloat(0.0f);
        else defaultValue = new NullOperand();
        compiler.addInstruction(new LOAD(defaultValue, reg));
        return reg;
    }


    //Initialisation du field par sa valeur par défault
    @Override
    protected void codeGenDefaultDeclField(DecacCompiler compiler, int superOffset) {

        //On charge la valeur par default dans R0
        GPRegister initReg = defaultInitField(compiler, GPRegister.R0);

        // On récupère l'adresse de l'objet
        RegisterOffset objectAddress = new RegisterOffset(-2, Register.LB);
        compiler.addInstruction(new LOAD(objectAddress, GPRegister.R1));

        // On store l'initialisation dans le bon field de l'objet
        int fieldOffset = 1 + superOffset + fieldName.getFieldDefinition().getIndex();
        RegisterOffset heapFieldAddress = new RegisterOffset(fieldOffset, GPRegister.R1);
        compiler.addInstruction(new STORE(initReg, heapFieldAddress));

        //On set l'operand ici (car la superclass peut utiliser des méthodes @Override par
        //la sous classe utilisant ces variables)
        fieldName.getExpDefinition().setOperand(heapFieldAddress);
    }


    //Initialisation du field par sa valeur explicite
    @Override
    protected void codeGenDeclField(DecacCompiler compiler, int superOffset) {

        //Champs déjà initialiser par codeGenDefaultDeclField
        if (initialization instanceof NoInitialization)
            return;

        DVal initAddrStack = initialization.codeGenInit(compiler);
        GPRegister initReg = RegisterHandler.popIntoRegister(compiler, initAddrStack, GPRegister.R0);

        // On récupère l'adresse de l'objet
        RegisterOffset objectAddress = new RegisterOffset(-2, Register.LB);
        compiler.addInstruction(new LOAD(objectAddress, GPRegister.R1));

        // On store l'initialisation dans le bon field de l'objet
        int fieldOffset = 1 + superOffset + fieldName.getFieldDefinition().getIndex();
        RegisterOffset heapFieldAddress = new RegisterOffset(fieldOffset, GPRegister.R1);
        compiler.addInstruction(new STORE(initReg, heapFieldAddress));

        //On libère le registre utilisé
        compiler.registerHandler.SetFree(initReg);
    }



    //Initialisation du field si la classe est un enfant direct de Object (n'a pas d'extend)
    protected void codeGenObjectDirectChildDeclField(DecacCompiler compiler, int superOffset) {

        GPRegister initReg;

        if (initialization instanceof Initialization) {
            // On récupère la valeur de l'initialisation dans initReg
            DVal initAddrStack = initialization.codeGenInit(compiler);
            initReg = RegisterHandler.popIntoRegister(compiler, initAddrStack, GPRegister.R0);
        } else {
            //Initialisation de la valeur par default
            initReg = defaultInitField(compiler, GPRegister.R0);
        }

        // On récupère l'adresse de l'objet
        RegisterOffset objectAddress = new RegisterOffset(-2, Register.LB);
        compiler.addInstruction(new LOAD(objectAddress, GPRegister.R1));

        // On store l'initialisation dans le bon field de l'objet
        int fieldOffset = 1 + superOffset + fieldName.getFieldDefinition().getIndex();

        RegisterOffset heapFieldAddress = new RegisterOffset(fieldOffset, GPRegister.R1);
        compiler.addInstruction(new STORE(initReg, heapFieldAddress));
        fieldName.getExpDefinition().setOperand(heapFieldAddress); //pas sûr

        compiler.registerHandler.SetFree(initReg);
    }

}
