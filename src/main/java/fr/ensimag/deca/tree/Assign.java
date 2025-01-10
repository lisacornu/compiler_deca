package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
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
    protected DVal codeGenExpr(DecacCompiler compiler) {

        // Generation du codes des branches
        DVal exp1Addr = getLeftOperand().codeGenExpr(compiler);
        DVal exp2Addr = getRightOperand().codeGenExpr(compiler);

        // Selection des bonnes adresses en fonction de leur emplacement mémoire
        GPRegister op2 = (exp2Addr == null) ? loadFromStack(compiler, Register.R1)
                : loadIntoRegister(compiler, exp2Addr, Register.R1);

        DVal op1 = (exp1Addr == null) ? loadFromStack(compiler, Register.R0)
                : exp1Addr;

        // Generation du code de l'expression (résultat enregistré dans op2)
        codeGenBinaryExpr(compiler, op1, op2);
        compiler.registerHandler.SetFree(op1); //On libère op1

        //Renvoi du résultat
        if (exp2Addr == null) { //Dans la pile si les registres sont plein
            compiler.addInstruction(new POP(op2));
            return null;
        } else {
            return op2;
        }
    }


    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        DAddr varAddress = ((AbstractIdentifier)getLeftOperand()).getExpDefinition().getOperand();
        compiler.addInstruction(new STORE(op2,varAddress));
    }

}
