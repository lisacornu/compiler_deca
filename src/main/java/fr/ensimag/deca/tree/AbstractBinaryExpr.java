package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
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
    public String getExprValue(DecacCompiler compiler) {
        return "temp";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {


        getLeftOperand().codeGenInst(compiler); // POP exp1
        compiler.addInstruction(new POP(Register.R0)); //R0 <- exp1

        getRightOperand().codeGenInst(compiler); // POP exp2
        compiler.addInstruction(new POP(Register.getR(2))); //R0 <- exp1

        codeGenBinaryExpr(compiler);

        compiler.addInstruction(new PUSH(Register.getR(2))); // PUSH R2

    }

    /*
     * Première operand est dans R0, deuxième operand est dans R2
     * Doit return dans R2
     * De la forme (OP R0 R2)
     */
    abstract protected void codeGenBinaryExpr(DecacCompiler compiler);

}
