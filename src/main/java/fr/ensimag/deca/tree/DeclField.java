package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;

import java.io.PrintStream;
import java.lang.instrument.ClassDefinition;
import java.rmi.UnexpectedException;

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

        // TODO 
        
    }

    @Override
    protected void verifyFieldMembers(DecacCompiler compiler, 
                               fr.ensimag.deca.context.ClassDefinition nameClass, 
                               EnvironmentExp envExp) throws ContextualError {
        if (type.verifyType(compiler).isVoid()){
            throw new ContextualError("The type of this field is void.", getLocation());
        }
        nameClass.incNumberOfFields();
        FieldDefinition fieldDef = new FieldDefinition(type.verifyType(compiler), getLocation(), visibility, nameClass, nameClass.getNumberOfFields());
        if (nameClass.getSuperClass().getMembers().get(fieldName.getName())!=null){
            try{
                nameClass.getSuperClass().getMembers().declare(fieldName.getName(), fieldDef);
            } catch (DoubleDefException e){
                throw new ContextualError("The field as already been declared before.", getLocation());
            }
        } else {
            throw new ContextualError("You cant update the environnement", getLocation());
        }
        fieldName.verifyExpr(compiler, envExp, nameClass);
        fieldName.setDefinition(fieldDef);
        

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
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        fieldName.iter(f);
    }

}
