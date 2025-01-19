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

    public AbstractIdentifier getFieldName() {
        return fieldName;
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
                               EnvironmentExp envExp) throws ContextualError {
                                
        if (type.verifyType(compiler).isVoid()){
            throw new ContextualError("The type of this field is void.", getLocation());
        }
        
        ExpDefinition expDefinition = nameClass.getMembers().get(fieldName.getName());

        if (expDefinition!=null){
            FieldDefinition fieldDefinition = (FieldDefinition) expDefinition;
        }

        nameClass.incNumberOfFields();
        FieldDefinition fieldDef = new FieldDefinition(type.getType(), getLocation(), visibility, nameClass, nameClass.getNumberOfFields());


        // if (nameClass.getSuperClass().getMembers().get(fieldName.getName())==null){
            try{
                nameClass.getMembers().declare(fieldName.getName(), fieldDef);
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

    //Initialise reg par sa valeur par default
    private GPRegister defaultInitField(DecacCompiler compiler, GPRegister reg) {
        DVal defaultValue;
        if (type.getType().isInt()) defaultValue = new ImmediateInteger(0);
        else if (type.getType().isBoolean()) defaultValue = new ImmediateInteger(0);
        else if (type.getType().isFloat()) defaultValue = new ImmediateFloat(0.0f);
        else defaultValue = new NullOperand();
        compiler.addInstruction(new LOAD(defaultValue, reg));
        return reg;
    }

    //Load l'adresse de l'objet dans reg et renvoi reg
    private GPRegister loadObjectAdress(DecacCompiler compiler, GPRegister reg) {
        RegisterOffset objectAddress = new RegisterOffset(-2, Register.LB);
        compiler.addInstruction(new LOAD(objectAddress, GPRegister.R1));
        return reg;
    }

    //Initialise le champ et renvoi son adresse dans le tas
    private RegisterOffset initField(DecacCompiler compiler, GPRegister initReg, GPRegister objReg, int superOffset) {

        int fieldOffset = superOffset + fieldName.getFieldDefinition().getIndex();
        RegisterOffset heapFieldAddress = new RegisterOffset(fieldOffset, objReg);
        compiler.addInstruction(new STORE(initReg, heapFieldAddress));
        return heapFieldAddress;
    }

    //Initialisation du champ par sa valeur par défault
    @Override
    protected void codeGenDefaultDeclField(DecacCompiler compiler, int superOffset) {

        GPRegister initReg = defaultInitField(compiler, GPRegister.R0); //On charge la valeur par default dans R0
        GPRegister objReg = loadObjectAdress(compiler, GPRegister.R1); // On récupère l'adresse de l'objet dans R1
        RegisterOffset heapFieldAddress = initField(compiler, initReg, objReg, superOffset); // On store l'initialisation dans le bon field de l'objet
        //fieldName.getExpDefinition().setOperand(heapFieldAddress); //On set l'operand
    }


    //Initialisation du field par sa valeur explicite
    @Override
    protected void codeGenDeclField(DecacCompiler compiler, int superOffset) {

        if (initialization instanceof NoInitialization) return; //Champs déjà initialiser par la valeur par défault

        DVal initAddrStack = initialization.codeGenInit(compiler); // On génère le code de l'expression
        GPRegister initReg = RegisterHandler.popIntoRegister(compiler, initAddrStack, GPRegister.R0);

        GPRegister objReg = loadObjectAdress(compiler, GPRegister.R1); // On récupère l'adresse de l'objet dans R1
        initField(compiler, initReg, objReg, superOffset); // On store l'initialisation dans le bon field de l'objet
        compiler.registerHandler.SetFree(initReg); //On libère le registre utilisé
    }


    //Initialisation du field si la classe est un enfant direct de Object (n'a pas d'extend)
    protected void codeGenObjectDirectChildDeclField(DecacCompiler compiler, int superOffset) {

        GPRegister initReg;
        if (initialization instanceof Initialization) {
            DVal initAddrStack = initialization.codeGenInit(compiler); // On récupère la valeur de l'initialisation dans initReg

            initReg = RegisterHandler.popIntoRegister(compiler, initAddrStack, GPRegister.R0);
        } else {
            initReg = defaultInitField(compiler, GPRegister.R0); //Initialisation de la valeur par default
        }

        GPRegister objReg = loadObjectAdress(compiler, GPRegister.R1); // On récupère l'adresse de l'objet
        RegisterOffset heapFieldAddress = initField(compiler, initReg, objReg, superOffset); // On store l'initialisation dans le bon field de l'objet
        fieldName.getExpDefinition().setOperand(heapFieldAddress); //On set l'operand
        compiler.registerHandler.SetFree(initReg); //On libère le registre utilisé
    }

}
