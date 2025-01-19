package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * Return
 *
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public class Return extends AbstractInst {
    private AbstractExpr rvalue;

    private ClassDefinition classDefinition;

    public Return(AbstractExpr rvalue){
        this.rvalue = rvalue;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.classDefinition = currentClass;

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
        DVal result = this.rvalue.codeGenExpr(compiler);
        compiler.addInstruction(new LOAD(result, Register.R0));
//        compiler.addInstruction(
//                new BRA(
//                        new Label("fin."+ this.classDefinition.getType().getName().getName()+"."+this.methodName.getName().getName()
//                        )
//                )
//        );
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
