package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;

/**
 * Operator "x >= y"
 * 
 * @author gl31
 * @date 01/01/2025
 */
public class GreaterOrEqual extends AbstractOpIneq {

    public GreaterOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
     private static int i=0;

    @Override
    protected String getOperatorName() {
        return ">=";
    }

     @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
            compiler.addInstruction(new CMP(op1, op2));
            Label case1=new Label ("greater_or_equal"+i);
            Label case2=new Label("not_greater_or_equal"+i);
            i++;
            compiler.addInstruction(new BGE(case1));    //si la comparaison est vrai(cad l'op est sup ou  egal ) --> on va a case2
            compiler.addInstruction(new LOAD(new ImmediateInteger(0),op2));
            compiler.addInstruction(new BRA(case2));
            compiler.addLabel(case1);
            compiler.addInstruction(new LOAD(new ImmediateInteger(1),op2));
            compiler.addLabel(case2);
            //compiler.addInstruction(new PUSH(Register.getR(2)));




    }
}
