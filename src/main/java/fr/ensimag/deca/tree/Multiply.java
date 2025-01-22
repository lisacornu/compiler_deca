package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.MUL;

/**
 * @author gl31
 * @date 01/01/2025
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected double evalExprValue(DecacCompiler compiler){
        AbstractExpr leftOperand = getLeftOperand();
        AbstractExpr rightOperand = getRightOperand();
        AbstractExpr operands[] = {leftOperand, rightOperand};

        double result = 1;

        //somme des valeurs des 2 opérandes
        for(AbstractExpr operand : operands){
            if(operand instanceof FloatLiteral){
                result *= ((FloatLiteral) operand).getValue();
            }
            else if(operand instanceof IntLiteral){
                result *= ((IntLiteral) operand).getValue();
            }
            else if(operand instanceof Identifier){
                //récupère le nom de l'identificateur
                String identName = ((Identifier) operand).getName().getName();
                result += compiler.variablePropa.get(identName);
            }
            else{
                result *= ((AbstractOpArith) operand).evalExprValue(compiler);
            }
        }

        return result;
    }

    @Override
    protected String getOperatorName() {
        return "*";
    }

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        //if (this.getLeftOperand().getType().isInt() && this.getRightOperand().getType().isInt())
        compiler.addInstruction(new MUL(op1, op2));
    }
}
