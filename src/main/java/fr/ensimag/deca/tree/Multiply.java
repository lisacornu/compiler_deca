package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.Optimiser;
import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BOV;
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
            //cas ou l'opérande est un littéral
            if(operand instanceof FloatLiteral){
                result *= ((FloatLiteral) operand).getValue();
            }
            else if(operand instanceof ConvFloat){
                result *= ((ConvFloat) operand).evalExprValue(compiler);
            }
            else if(operand instanceof IntLiteral){
                result *= ((IntLiteral) operand).getValue();
            }
            else if(operand instanceof Identifier){
                //récupère le nom de l'identificateur
                String identName = ((Identifier) operand).getName().getName();
                if(compiler.variablePropa.get(identName) != null){
                    result *= compiler.variablePropa.get(identName);
                }
                else if (compiler.variablePropa_float.get(identName)!=null){
                   result *= compiler.variablePropa_float.get(identName);
                }
                else{
                    return Double.MAX_VALUE; //cas utile quand il y a if else ou while 
                }
            }
            else{
                result *= ((AbstractOpArith) operand).evalExprValue(compiler);
            }
        }

        if(isOverflood(result)){
            throw new ArithmeticException("Number overflow");
        }

        return result;
    }

    @Override
    protected String getOperatorName() {
        return "*";
    }



    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
        AbstractExpr fastOperand = Optimiser.FastMultiply(getLeftOperand(), getRightOperand());
        if (fastOperand instanceof Multiply)
            return super.codeGenExpr(compiler);
        return fastOperand.codeGenExpr(compiler);
    }



    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        compiler.addInstruction(new MUL(op1, op2));
    }
}
