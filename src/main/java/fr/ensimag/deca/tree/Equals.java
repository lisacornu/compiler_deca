package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Equals extends AbstractOpExactCmp {
    private static int i=0;
    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }

   /* @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
            compiler.addInstruction(new CMP(op1, op2));
            Label case1=new Label ("equal_"+i);
            Label case2=new Label("not_equal"+i);
            i++;
            compiler.addInstruction(new BNE(case2));    //si la comparaison est fausse --> on va a case2
            compiler.addInstruction(new LOAD(1,op2));
            compiler.addInstruction(new BRA(case1));
            compiler.addLabel(case2);
            compiler.addInstruction(new LOAD(0,op2));
            compiler.addLabel(case1);
    }*/

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
            compiler.addInstruction(new CMP(op1, op2));
            Label case1=new Label ("equal_"+i);
            Label case2=new Label("not_equal"+i);
            i++;
            compiler.addInstruction(new BNE(case2));    //si la comparaison est fausse --> on va a case2
            compiler.addInstruction(new LOAD(1,op2));
            compiler.addInstruction(new BRA(case1));
            compiler.addLabel(case2);
            compiler.addInstruction(new LOAD(0,op2));
            compiler.addLabel(case1);
    }
}
