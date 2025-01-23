package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected double evalExprValue(DecacCompiler compiler) {
        AbstractExpr leftOperand = getLeftOperand();
        AbstractExpr rightOperand = getRightOperand();

        double leftValue = 0;
        double rightValue = 0;

        // évalue l'opérande de gauche
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
            leftValue = compiler.variablePropa.get(identName);
        }
        else {
            leftValue = ((AbstractOpArith) leftOperand).evalExprValue(compiler);
        }

        // évalue l'opérande de droite
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
            rightValue = compiler.variablePropa.get(identName);
        }
        else {
            rightValue = ((AbstractOpArith) rightOperand).evalExprValue(compiler);
        }

        // fait le modulo
        return (int)leftValue % (int)rightValue;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type righType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if (lefType.isInt() && righType.isInt()){
            
            setType(righType);
            return righType;
        }
        throw new ContextualError("You cant divide things that are not int",getLocation());
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {

        branchIfZero(compiler, op1);

        compiler.addInstruction(new REM(op1, op2));
    }
}
