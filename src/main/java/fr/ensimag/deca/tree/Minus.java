package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * @author gl31
 * @date 01/01/2025
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        compiler.addInstruction(new SUB(op1, op2));
    }

    @Override
    protected float evalExprValue(){
        AbstractExpr leftOperand = getLeftOperand();
        AbstractExpr rightOperand = getRightOperand();
        AbstractExpr operands[] = {leftOperand, rightOperand};

        float result = 0;
        float minusIfSecondOp = 1;

        //différence des valeurs des 2 opérandes
        for(AbstractExpr operand : operands){
            if(operand instanceof FloatLiteral){
                result = result + minusIfSecondOp * ((FloatLiteral) operand).getValue();
            }
            else if(operand instanceof IntLiteral){
                result = result + minusIfSecondOp * ((IntLiteral) operand).getValue();
            }
            else{
                result = result + minusIfSecondOp * ((AbstractOpArith) operand).evalExprValue();
            }

            minusIfSecondOp = -1;
        }

        return result;
    }
}
