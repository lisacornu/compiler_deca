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
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type operand = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (operand.isBoolean()){
            this.setType(operand);
            return operand;
        }else{
            throw new ContextualError("Opernd is not boolean",getLocation());
        }
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }
}
