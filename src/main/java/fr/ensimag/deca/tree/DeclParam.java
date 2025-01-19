package fr.ensimag.deca.tree;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ParamDefinition;
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
public class DeclParam extends AbstractDeclParam {

    final private AbstractIdentifier type;
    final private AbstractIdentifier nameParam;

    public AbstractIdentifier getNameParam () {
        return this.nameParam;
    }

    public DeclParam(AbstractIdentifier type,AbstractIdentifier nameParam) {
        this.type = type;
        this.nameParam = nameParam;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        nameParam.decompile(s);
        // TODO 
        
    }

    @Override
    protected Type verifyParamMembers(DecacCompiler compiler) throws ContextualError {
        if (type.verifyType(compiler).isVoid()){
            throw new ContextualError("Your parameter is Void Type and it can't" + type, getLocation());
        }
        type.verifyType(compiler);
        return type.getType();
    }

    
    @Override
    protected void verifyParamBody(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError {
        Type paramType = type.verifyType(compiler);
        ParamDefinition paramDef = new ParamDefinition(paramType, getLocation());
        nameParam.setDefinition(paramDef);
        nameParam.setType(paramType);
        try{
            localEnv.declare(nameParam.getName(), paramDef);
        } catch (DoubleDefException e){
            throw new ContextualError("The Param as already been defined before.", getLocation());
        }
}


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // visibility.prettyPrint(s,prefix,false);
        type.prettyPrint(s, prefix,false);
        nameParam.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        nameParam.iter(f);
    }

}
