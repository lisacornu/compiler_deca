package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BLE;
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
public class LowerOrEqual extends AbstractOpIneq {
    public LowerOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    private static int i =0;

    @Override
    protected String getOperatorName() {
        return "<=";
    }

     @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
            //j etais sensé utilisé BLE mais s a pas marché donc j utilise BGE ici et dans sup ou egal BLE jsp pk les 2opertation sont inversé
            compiler.addInstruction(new CMP(op1, op2));
            Label case1=new Label ("lower_or_equal"+i);
            Label case2=new Label("not_lower_or_equal"+i);
            i++;
            compiler.addInstruction(new BLE(case1));    //si la comparaison est vrai(cad l'op est sup ) --> on va a case2
            compiler.addInstruction(new LOAD(new ImmediateInteger(0),op2));
            compiler.addInstruction(new BRA(case2));
            compiler.addLabel(case1);
            compiler.addInstruction(new LOAD(new ImmediateInteger(1),op2));
            compiler.addLabel(case2);
            //compiler.addInstruction(new PUSH(Register.getR(2)));




    }
}
