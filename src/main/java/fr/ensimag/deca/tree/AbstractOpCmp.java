package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        // TODO : Probablement problèmes de int et float à traiter
        Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type righType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if ((lefType.isInt() || lefType.isFloat()) && (righType.isInt() || righType.isFloat())){
            Type booleanType = new BooleanType(compiler.createSymbol("boolean"));
            this.setType(booleanType);
            return booleanType;
        }else if (lefType.isBoolean() && righType.isBoolean()){
            setType(righType);
            return righType;
        }else{
            throw new ContextualError("Both are not same type : " + lefType + " and " + righType,getLocation());
        }
    }


}
