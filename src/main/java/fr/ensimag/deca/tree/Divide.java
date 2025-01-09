package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
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


    // @Override
    // public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
    //         ClassDefinition currentClass) throws ContextualError {
        
    //     Type righType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
    //     Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
    //     System.out.println("debug divide"+righType+" eaz "+lefType);
    //     if (righType.isFloat()){
    //         System.out.println(((FloatLiteral) getRightOperand()).getExprValue(compiler));
    //         FloatLiteral righValue = (FloatLiteral) getRightOperand();
    //         if (righValue.getValue() == 0){
    //             throw new ContextualError("Divison par 0", getLocation());
    //         }
    //     }else {
    //         IntLiteral righValue = (IntLiteral) getRightOperand();
    //         if (righValue.getValue() == 0){
    //             throw new ContextualError("Divison par 0", getLocation());
    //         }
    //     }

        

    //     if ((lefType.isInt() || lefType.isFloat()) && (righType.isFloat()||righType.isInt())){
    //         if (lefType.isFloat() && righType.isInt()){
    //             AbstractExpr intTypeExpr = getRightOperand();
    //             setRightOperand(new ConvFloat(intTypeExpr));
    //             this.setType(lefType);
    //             return lefType;

    //         }else if (righType.isFloat() && lefType.isInt()){
    //             AbstractExpr intTypeExpr = getLeftOperand();
    //             setLeftOperand(new ConvFloat(intTypeExpr));
    //             this.setType(righType);
    //             return righType;

    //         }else if (righType.isFloat()){
    //             this.setType(righType);
    //             return righType;
    //         }
    //         this.setType(righType);
    //         return righType;
    //     }else{
    //         throw new ContextualError("Both are not float or int",getLocation());
    //     }
    // }

    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        if(getLeftOperand().getType().isFloat()){
            compiler.addInstruction(new DIV(op1, op2)); //avec des flottants
        }
        else{
            compiler.addInstruction(new QUO(op1, op2)); // avec des entiers
        }
    }
}
