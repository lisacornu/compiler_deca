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
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type righType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (lefType.isBoolean() && righType.isBoolean()){
            this.setType(righType);
            return righType;
        }else{
            throw new ContextualError("Both are not boolean : "+lefType +" and "+righType,getLocation());
        }
    }


    @Override
    public Type verifyExpr_opti(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type lefType = getLeftOperand().verifyExpr_opti(compiler, localEnv, currentClass);
        Type righType = getRightOperand().verifyExpr_opti(compiler, localEnv, currentClass);
        if(getRightOperand() instanceof Identifier){
            ((Identifier)getRightOperand()).usage(compiler);
        }
        if(getLeftOperand() instanceof Identifier){
            ((Identifier)getLeftOperand()).usage(compiler);
        }
        if (lefType.isBoolean() && righType.isBoolean()){
            this.setType(righType);
            return righType;
        }else{
            throw new ContextualError("Both are not boolean : "+lefType +" and "+righType,getLocation());
        }
    }

}

