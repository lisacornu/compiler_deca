package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BLT;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Lower extends AbstractOpIneq {

    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
     private static int i=0;

    @Override
    protected String getOperatorName() {
        return "<";
    }

     @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
            compiler.addInstruction(new CMP(op1, op2));
            Label case1=new Label ("lower_"+i);
            Label case2=new Label("not_lower"+i);
            i++;
            compiler.addInstruction(new BLT(case2));    //si la comparaison est vrai(cad l'op est sup ) --> on va a case2
            compiler.addInstruction(new LOAD(new ImmediateInteger(0),op2));
            compiler.addInstruction(new BRA(case1));
            compiler.addLabel(case2);
            compiler.addInstruction(new LOAD(new ImmediateInteger(1),op2));
            compiler.addLabel(case1);
            //compiler.addInstruction(new PUSH(Register.getR(2)));




    }
}
