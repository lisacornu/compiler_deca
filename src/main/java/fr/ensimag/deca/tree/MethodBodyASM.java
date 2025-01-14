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
public class MethodBodyASM extends AbstractMethodBody {

    
    final private AbstractStringLiteral instructAss;

    public MethodBodyASM(AbstractStringLiteral instructAss) {
        this.instructAss = instructAss;
    }

    @Override
    protected void verifyMethodBody(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        instructAss.decompile(s);
        
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        instructAss.iter(f);
        
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        instructAss.prettyPrint(s, prefix, false);
        
    }

    @Override
    protected void codeGenMethodBody(DecacCompiler compiler) {

        
    }
}
