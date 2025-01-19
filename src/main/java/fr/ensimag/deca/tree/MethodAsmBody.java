package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

import static org.mockito.ArgumentMatchers.isNotNull;

import java.io.PrintStream;

/**
 * @author gl31
 * @date 14/01/2025
 */
public class MethodAsmBody extends AbstractMethodBody {

    
    final private StringLiteral instructAss;

    public MethodAsmBody(StringLiteral instructAss) {
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
        instructAss.prettyPrint(s, prefix, true);
        
    }

    static int userMethodCpt = 0;

    @Override
    protected void codeGenMethodBody(DecacCompiler compiler, DeclClass declClass, String methodName) {
        compiler.addLabel(new Label("userMethod_"+userMethodCpt));
        compiler.addInstruction(new WSTR(this.instructAss.getValue()));
    }
}
