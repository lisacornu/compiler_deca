package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.IMAProgram;

/**
 * Return
 *
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public class Return extends AbstractInst {
    private AbstractExpr rvalue;

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
        if (returnType.isFloat() && rvalue.getType().isInt()){
            rvalue = new ConvFloat(rvalue);
        }
        rvalue.setType(returnType);
    }


    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // TODO
    }




    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        rvalue.decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        rvalue.prettyPrint(s, prefix, true);
    }
    @Override
    protected void iterChildren(TreeFunction f) {
        rvalue.iter(f);
    }
}
