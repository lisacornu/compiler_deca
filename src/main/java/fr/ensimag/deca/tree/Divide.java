package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**;
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {

        branchIfZero(compiler, op1);

        if (getLeftOperand().getType().isFloat()) {
            compiler.addInstruction(new DIV(op1, op2)); //avec des flottants
        } else {
            compiler.addInstruction(new QUO(op1, op2)); // avec des entiers
        }
    }

    @Override
    protected double evalExprValue(DecacCompiler compiler) {
        AbstractExpr leftOperand = getLeftOperand();
        AbstractExpr rightOperand = getRightOperand();

        double leftValue = 0;
        double rightValue = 0;

        //évalue l'opérande de gauche
        if (leftOperand instanceof FloatLiteral) {
            leftValue = ((FloatLiteral) leftOperand).getValue();
        }
        else if (leftOperand instanceof IntLiteral) {
            leftValue = ((IntLiteral) leftOperand).getValue();
        }
        else if(leftOperand instanceof ConvFloat){
            leftValue = ((ConvFloat) leftOperand).evalExprValue(compiler);
        }
        else if(leftOperand instanceof Identifier){
            //récupère le nom de l'identificateur
            String identName = ((Identifier) leftOperand).getName().getName();
            if(compiler.variablePropa.get(identName)!=null){
                leftValue = compiler.variablePropa.get(identName);
            }
            else{
                leftValue = compiler.variablePropa_float.get(identName);
            }
        }
        else {
            leftValue = ((AbstractOpArith) leftOperand).evalExprValue(compiler);
        }

        //évalue l'opérande de droite
        if (rightOperand instanceof FloatLiteral) {
            rightValue = ((FloatLiteral) rightOperand).getValue();
        }
        else if (rightOperand instanceof IntLiteral) {
            rightValue = ((IntLiteral) rightOperand).getValue();
        }
        else if(rightOperand instanceof ConvFloat){
            rightValue = ((ConvFloat) rightOperand).evalExprValue(compiler);
        }
        else if(rightOperand instanceof Identifier){
            //récupère le nom de l'identificateur
            String identName = ((Identifier) rightOperand).getName().getName();
            if(compiler.variablePropa.get(identName)!=null){
                rightValue = compiler.variablePropa.get(identName);
            }
            else if (compiler.variablePropa_float.get(identName)!=null){
                rightValue = compiler.variablePropa_float.get(identName);
            }
            else{
                return Double.MAX_VALUE;
            }
            
        }
        else {
            rightValue = ((AbstractOpArith) rightOperand).evalExprValue(compiler);
        }

        // Division par 0
        if (rightValue == 0) {
            throw new ArithmeticException("Division by zero");
        }

        if(leftOperand.getType().isFloat()){
            return (double) leftValue / (double) rightValue;
        }
        else{
            return (int) leftValue / (int) rightValue;
        }
    }
}

