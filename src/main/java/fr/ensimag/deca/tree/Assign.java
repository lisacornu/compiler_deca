package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        // AbstractLValue leftt=getLeftOperand();
        // System.out.println("mais euh " + leftt.getType() + " " +getLocation());
        
        AbstractExpr rightExpDefinition = getRightOperand().verifyRValue(compiler, localEnv, currentClass, lefType);
        this.setType(lefType);
        setRightOperand(rightExpDefinition);
        return lefType;
        
    }


    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler) {
        DAddr varAddress = ((AbstractIdentifier)getLeftOperand()).getExpDefinition().getOperand();
        compiler.addInstruction(new STORE(Register.getR(2),varAddress));
    }

}
