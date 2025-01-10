package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
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


    protected GPRegister loadFromStack(DecacCompiler compiler, GPRegister tempRegister) {
        compiler.addInstruction(new POP(tempRegister));
        return tempRegister;
    }

    protected GPRegister loadIntoRegister(DecacCompiler compiler, DVal addr, GPRegister tempRegister) {
        if (addr instanceof GPRegister) { // addr est un registre
            return (GPRegister)addr;
        } else { // addr est dans GB
            compiler.addInstruction(new LOAD(addr, tempRegister));
            return tempRegister;
        }
    }


    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {

        // Generation du codes des branches
        DVal exp1Addr = getLeftOperand().codeGenExpr(compiler);
        DVal exp2Addr = getRightOperand().codeGenExpr(compiler); // POP exp2

        // Selection des bonnes adresses en fonction de leur emplacement mémoire
        DVal op1 = (exp2Addr == null) ? loadFromStack(compiler, Register.R1)
                : exp2Addr;

        GPRegister op2= (exp1Addr == null) ? loadFromStack(compiler, Register.R0)
                : loadIntoRegister(compiler, exp1Addr, Register.R0);

        // Generation du code de l'expression (résultat enregistré dans op2)
        codeGenBinaryExpr(compiler, op1, op2);
        compiler.registerHandler.SetFree(op1); //On libère op1

        //Renvoi du résultat
        if (exp1Addr == null) { //Dans la pile si les registres sont plein
            compiler.addInstruction(new POP(op2));
            return null;
        } else {
            return op2;
        }
    }



    /*
     Génère l'expression binaire entre op1 et op2
     */
    abstract protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2);

}
