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
    protected double evalExprValue(DecacCompiler compiler){
        AbstractExpr leftOperand = getLeftOperand();
        AbstractExpr rightOperand = getRightOperand();
        AbstractExpr operands[] = {leftOperand, rightOperand};

        double result = 0;
        double minusIfSecondOp = 1;

        //différence des valeurs des 2 opérandes
        for(AbstractExpr operand : operands){
            if(operand instanceof FloatLiteral){
                result += minusIfSecondOp * ((FloatLiteral) operand).getValue();
            }
            else if(operand instanceof IntLiteral){
                result += minusIfSecondOp * ((IntLiteral) operand).getValue();
            }
            else if(operand instanceof ConvFloat){
                result += minusIfSecondOp * ((ConvFloat) operand).evalExprValue(compiler);
            }
            else if(operand instanceof Identifier){
                //récupère le nom de l'identificateur
                String identName = ((Identifier) operand).getName().getName();
                if(compiler.variablePropa.get(identName)!=null){
                    result += minusIfSecondOp * compiler.variablePropa.get(identName);
                }
                else{
                    result += minusIfSecondOp * compiler.variablePropa_float.get(identName);
                }
                
            }
            else{
                result = result + minusIfSecondOp * ((AbstractOpArith) operand).evalExprValue(compiler);
            }

            minusIfSecondOp = -1;
        }

        return result;
    }
}
