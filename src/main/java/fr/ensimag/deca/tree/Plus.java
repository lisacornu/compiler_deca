package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.syntax.NumberOverflow;
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
    protected double evalExprValue(DecacCompiler compiler){
        AbstractExpr leftOperand = getLeftOperand();
        AbstractExpr rightOperand = getRightOperand();
        AbstractExpr operands[] = {leftOperand, rightOperand};

        double result = 0;

        //somme des valeurs des 2 opérandes
        for(AbstractExpr operand : operands){
            if(operand instanceof FloatLiteral){
                //Addition qui détecte les overflow
                result += ((FloatLiteral) operand).getValue();
            }
            else if(operand instanceof IntLiteral){
                result += ((IntLiteral) operand).getValue();
            }
            else if(operand instanceof Identifier){
                //récupère le nom de l'identificateur
                String identName = ((Identifier) operand).getName().getName();
                if(compiler.variablePropa.get(identName)!=null){
                    result += compiler.variablePropa.get(identName);
                }
                else{
                    result += compiler.variablePropa_float.get(identName);
                }
                
            }
            else{
                result += ((AbstractOpArith) operand).evalExprValue(compiler);
            }
        }

        if(isOverflood(result)){
            throw new ArithmeticException("Number overflow");
        }

        return result;
    }
}
