package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import static org.mockito.Mockito.timeout;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl31
 * @date 01/01/2025
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // TODO : vérifier si int et float, return type int ou float selon si opération sur des floats ou int
        // Type opArith = new ExpDefinition(compiler.createSymbol(getOperatorName())); 
        Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type righType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
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
}
