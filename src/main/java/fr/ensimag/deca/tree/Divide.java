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
    protected float evalExprValue() {
        AbstractExpr leftOperand = getLeftOperand();
        AbstractExpr rightOperand = getRightOperand();

        float leftValue = 0;
        float rightValue = 0;

        //évalue l'opérande de gauche
        if (leftOperand instanceof FloatLiteral) {
            leftValue = ((FloatLiteral) leftOperand).getValue();
        }
        else if (leftOperand instanceof IntLiteral) {
            leftValue = ((IntLiteral) leftOperand).getValue();
        }
        else {
            leftValue = ((AbstractOpArith) leftOperand).evalExprValue();
        }

        //évalue l'opérande de droite
        if (rightOperand instanceof FloatLiteral) {
            rightValue = ((FloatLiteral) rightOperand).getValue();
        }
        else if (rightOperand instanceof IntLiteral) {
            rightValue = ((IntLiteral) rightOperand).getValue();
        }
        else {
            rightValue = ((AbstractOpArith) rightOperand).evalExprValue();
        }

        // Division par 0
        if (rightValue == 0) {
            throw new ArithmeticException("Division by zero");
        }

        // fait la division
        return (int)leftValue / (int)rightValue;
    }
}

