package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.VariableDefinition;

import static org.mockito.ArgumentMatchers.isNotNull;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

/**
 * @author gl31
 * @date 14/01/2025
 */
public class MethodBody extends AbstractMethodBody {

    
    final private ListDeclVar listVar;
    final private ListInst listInst;

    public MethodBody(ListDeclVar listVar, ListInst listInst) {
        this.listInst = listInst;
        this.listVar = listVar;
    }

    @Override
    protected void verifyMethodBody(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        listVar.verifyListDeclVariable(compiler, localEnv, currentClass);
        listInst.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        listVar.decompile(s);
        listInst.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        listVar.iter(f);
        listInst.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        listVar.prettyPrint(s, prefix, false);
        listInst.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenMethodBody(DecacCompiler compiler, DeclClass declClass) {
        listVar.codeGenListDeclVarMethod(compiler);
        listInst.codeGenListInst(compiler);
    }
}
