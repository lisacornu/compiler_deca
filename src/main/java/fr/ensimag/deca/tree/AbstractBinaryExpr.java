package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Binary expressions.
 *
 * @author gl31
 * @date 01/01/2025
 */
public abstract class AbstractBinaryExpr extends AbstractExpr {

    public AbstractExpr getLeftOperand() {
        return leftOperand;
    }

    public AbstractExpr getRightOperand() {
        return rightOperand;
    }

    protected void setLeftOperand(AbstractExpr leftOperand) {
        Validate.notNull(leftOperand);
        this.leftOperand = leftOperand;
    }

    protected void setRightOperand(AbstractExpr rightOperand) {
        Validate.notNull(rightOperand);
        this.rightOperand = rightOperand;
    }

    private AbstractExpr leftOperand;
    private AbstractExpr rightOperand;

    public AbstractBinaryExpr(AbstractExpr leftOperand,
            AbstractExpr rightOperand) {
        Validate.notNull(leftOperand, "left operand cannot be null");
        Validate.notNull(rightOperand, "right operand cannot be null");
        Validate.isTrue(leftOperand != rightOperand, "Sharing subtrees is forbidden");
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
        s.print(")");
    }

    abstract protected String getOperatorName();

    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, true);
    }

    @Override
    public void printExprValue(DecacCompiler compiler) {
        DVal result = this.codeGenExpr(compiler);
        compiler.registerHandler.SetFree(result);
        compiler.addInstruction(new LOAD(result, GPRegister.R1));
        if (this.getLeftOperand().getType().isFloat() || this.getRightOperand().getType().isFloat())
            compiler.addInstruction(new WFLOAT());
        else
            compiler.addInstruction(new WINT());
    }


    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {

        // Generation du codes des branches
        DVal leftOperandResult = getLeftOperand().codeGenExpr(compiler);
        DVal rightOperandResult = getRightOperand().codeGenExpr(compiler);


        // On pop de la pile si besoin (et on déplace dans un registre leftOperandResult si besoin)
        DVal op1 = RegisterHandler.popIntoDVal(compiler, rightOperandResult, Register.R1);
        GPRegister op2 = RegisterHandler.popIntoRegister(compiler, leftOperandResult, Register.R0);

        // Generation du code de l'expression (résultat enregistré dans op2)
        codeGenBinaryExpr(compiler, op1, op2);
        compiler.registerHandler.SetFree(op1); //On libère le registre de op1 (car il est maintenant inutile)

        // On push dans la pile si besoin (et on déplace op2 si c'est un registre temporaire dans un nv registre/dans la pile)
        return RegisterHandler.pushFromRegister(compiler, op2);
    }

    public void branchIfZero(DecacCompiler compiler, DVal op) {

        GPRegister cmpReg = RegisterHandler.popIntoRegister(compiler, op, Register.R1);

        if (getRightOperand().getType().isInt()) {
            compiler.addInstruction(new CMP(0, cmpReg));
        } else {
            compiler.addInstruction(new CMP(new ImmediateFloat(0.0f), cmpReg));
        }
        compiler.addInstruction(new BEQ(new Label("division_zero")));
    }



    /*
     Génère l'expression binaire entre op1 et op2
     */
    abstract protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2);

}
