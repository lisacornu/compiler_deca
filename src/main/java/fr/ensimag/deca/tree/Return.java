package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.commons.lang.Validate;

/**
 * Return
 *
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public abstract class Return extends AbstractInst {
    private final AbstractExpr rvalue;

    public Return(AbstractExpr rvalue){
        this.rvalue = rvalue;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
            Type type = rvalue.verifyExpr(compiler, localEnv, currentClass);

        if (type.isVoid()){
            throw new UnsupportedOperationException("This return type is void");
        }
        rvalue.verifyRValue(compiler, localEnv, currentClass, type);
    }


    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // TODO
    }




    @Override
    protected void decompileInst(IndentPrintStream s) {
        s.print("return ");
        rvalue.decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        rvalue.prettyPrint(s, prefix, false);
    }
}
