package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
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
        AbstractExpr rightExpDefinition = getRightOperand().verifyRValue(compiler, localEnv, currentClass, lefType);
        this.setType(lefType);
        if (lefType.isFloat() && rightExpDefinition.getType().isInt()){
            ConvFloat conversionFloat = new ConvFloat(rightExpDefinition);
            conversionFloat.verifyExpr(compiler, localEnv, currentClass);
            setRightOperand(conversionFloat);
        }else{
            setRightOperand(rightExpDefinition);
        }
        return lefType;
        
    }


    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {

        // Generation du codes des branches
        DVal leftOperandResult = getLeftOperand().codeGenExpr(compiler);
        DVal rightOperandResult = getRightOperand().codeGenExpr(compiler);

        // Selection des bonnes adresses en fonction de leur emplacement mémoire
        GPRegister op2 =  RegisterHandler.popIntoRegister(compiler, rightOperandResult, Register.R1);
        DVal op1 = RegisterHandler.popIntoDVal(compiler, leftOperandResult, Register.R0);;

        // Generation du code de l'expression (résultat enregistré dans op1)
        codeGenBinaryExpr(compiler, op1, op2);
        compiler.registerHandler.SetFree(op2);

        //Renvoi du résultat (op1 est ne peut pas être un registre temporaire)
        return op1;
    }


    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        DAddr varAddress = ((AbstractIdentifier)getLeftOperand()).getExpDefinition().getOperand();
        compiler.addInstruction(new STORE(op2,varAddress));
    }

}
