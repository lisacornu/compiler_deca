package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

/**
 * Integer literal
 *
 * @author gl31
 * @date 01/01/2025
 */
public class IntLiteral extends AbstractExpr {
    public int getValue() {
        return value;
    }

    private int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type intType = new IntType(compiler.createSymbol("int"));
        setType(intType);
        return intType;
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {

        GPRegister freeReg = compiler.registerHandler.Get();

        //Pas de registres disponibles
        if (freeReg == null) {
            compiler.addInstruction(new LOAD(new ImmediateInteger(value), GPRegister.R0)); //LOAD dans R0
            compiler.addInstruction(new PUSH(GPRegister.R0)); //PUSH R0
            return null; // On indique qu'on a push dans la pile
        }

        //Registre disponibles
        compiler.addInstruction(new LOAD(new ImmediateInteger(value), freeReg));
        return freeReg;
    }


    @Override
    public void printExprValue(DecacCompiler compiler) {
        compiler.addInstruction(new WSTR(String.valueOf(value)));
    }


    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
}
