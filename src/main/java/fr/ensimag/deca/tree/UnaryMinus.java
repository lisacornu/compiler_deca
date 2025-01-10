package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DVal;

/**
 * @author gl31
 * @date 01/01/2025
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type type = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if(type.isFloat() || type.isInt()){
            setType(type);
            return type;
        } 
        throw new ContextualError("You are trying to make something negative but it's not legal on this type", getLocation());
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

}
