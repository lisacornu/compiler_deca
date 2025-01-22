package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.DVal;

/**
 * @author gl31
 * @date 01/01/2025
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
 

    @Override
    protected String getOperatorName() {
        return "+";
    }

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        compiler.addInstruction(new ADD(op1, op2)); // R2 <- R0 opp R2
    }

    @Override
    protected float evalExprValue(DecacCompiler compiler){
        AbstractExpr leftOperand = getLeftOperand();
        AbstractExpr rightOperand = getRightOperand();
        AbstractExpr operands[] = {leftOperand, rightOperand};

        float result = 0;

        //somme des valeurs des 2 opérandes
        for(AbstractExpr operand : operands){
            if(operand instanceof FloatLiteral){
                result += ((FloatLiteral) operand).getValue();
            }
            else if(operand instanceof IntLiteral){
                result += ((IntLiteral) operand).getValue();
            }
            else if(operand instanceof Identifier){
                //récupère le nom de l'identificateur
                String identfierName = ((Identifier) operand).getName().getName();
                result += compiler.variablePropa.get(identfierName);
            }
            else{
                result += ((AbstractOpArith) operand).evalExprValue(compiler);
            }
        }

        return result;
    }
}
