package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Single precision, InstanceOf
 *
 * @author Fabien Galzi
 * @date 15/01/2025
 */
public class InstanceOf extends AbstractExpr {
    private AbstractExpr expr;
    private AbstractIdentifier type;
    public InstanceOf(AbstractExpr expr, AbstractIdentifier type) {
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeExpr = expr.verifyExpr(compiler, localEnv, currentClass);
        Type typeType = type.verifyType(compiler);
        if(!((typeExpr.isClass()|| typeExpr.isNull())||typeType.isClass())){
            throw new ContextualError("You cant do Instanceof " + typeExpr, getLocation());
        }
        setType(compiler.environmentType.defOfType(compiler.createSymbol("boolean")).getType());
        return getType();
    }

    @Override
    public void printExprValue(DecacCompiler compiler) {
        // TODO
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        expr.decompile(s);
        s.print(" instanceOf ");
        type.decompile(s);
        s.print(")");
    }


    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        type.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, true);
    }

    static private int cpt_instanceof = 0;

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
//        Label instanceOfBeginning = new Label("InstanceOfBegin"+compiler.getInstanceOfCounter());
//        Label instanceOfSucceeded = new Label("InstanceOfSuccess"+compiler.getInstanceOfCounter());

        DVal locationResult = this.expr.codeGenExpr(compiler);
        GPRegister reg = RegisterHandler.popIntoRegister(compiler, locationResult, Register.R1);


//        DAddr addr = type.getClassDefinition().write(compiler).getAddr();
//        compiler.allocR2();
//        compiler.addInstruction(new LEA(addr,Register.getR(2)));
//        compiler.addLabel(instanceOfBeginning);
//
//        compiler.addInstruction(new CMP(Register.getR(2),Register.R0));
//        compiler.addInstruction(new SEQ(Register.R1));
//        compiler.addInstruction(new CMP(new ImmediateInteger(1),Register.R1));
//        compiler.addInstruction(new BEQ(instanceOfSucceeded));
//        compiler.addInstruction(new LOAD(Register.R0,Register.R0));
//        compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
//        compiler.addInstruction(new BEQ(instanceOfSucceeded));
//        compiler.addInstruction(new BRA(instanceOfBeginning));
//
//        compiler.addLabel(instanceOfSucceeded);
//
//        if (reg.isGPRegister()) {
//            compiler.addInstruction(new LOAD(Register.R1,(GPRegister) reg));
//        }
//        else if ( reg.isRegisterOffset() ) {
//            compiler.addInstruction(new STORE(Register.R1,compiler.translate( (RegisterOffset) reg )));
//        }

        return reg;
    }

}
