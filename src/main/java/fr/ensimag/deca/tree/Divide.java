package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type righType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        
        if (righType.isFloat()){
            FloatLiteral righValue = (FloatLiteral) getRightOperand();
            if (righValue.getValue() == 0){
                throw new ContextualError("Divison par 0", getLocation());
            }
        }else {
            IntLiteral righValue = (IntLiteral) getRightOperand();
            if (righValue.getValue() == 0){
                throw new ContextualError("Divison par 0", getLocation());
            }
        }

        

        if ((lefType.isInt() || lefType.isFloat()) && (righType.isFloat()||righType.isInt())){
            if (lefType.isFloat() && righType.isInt()){
                AbstractExpr intTypeExpr = getRightOperand();
                setRightOperand(new ConvFloat(intTypeExpr)); 
                this.setType(lefType);
                return lefType;

            }else if (righType.isFloat() && lefType.isInt()){
                AbstractExpr intTypeExpr = getLeftOperand();
                setLeftOperand(new ConvFloat(intTypeExpr));
                this.setType(righType);
                return righType;

            }else if (righType.isFloat()){
                this.setType(righType);
                return righType;
            }
            this.setType(righType);
            return righType;
        }else{
            throw new ContextualError("Both are not float or int",getLocation());
        }
    }

    @Override
    protected String getOperatorName() {
        return "/";
    }

}
