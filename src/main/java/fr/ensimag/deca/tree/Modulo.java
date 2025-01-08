package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

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
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type righType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if (lefType.isInt() && righType.isInt()){
            IntLiteral rightValue = (IntLiteral) getRightOperand();
            if(rightValue.getValue() == 0){
                throw new ContextualError("Divison par 0", getLocation());
            }
            setType(righType);
            return righType;
        }
        throw new ContextualError("You cant divide things that are not int",getLocation());
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }

}
