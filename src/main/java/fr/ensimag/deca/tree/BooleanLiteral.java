package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class BooleanLiteral extends AbstractExpr {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type bool = new BooleanType(compiler.createSymbol("boolean"));
        setType(bool);
        return bool;
    }
    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
        int boolValue = value ? 1 : 0;//1 si true 0 sinon 
        //grace a l'arbre on gartentit le fait de ne pas comparer 1 et true

        GPRegister freeReg = compiler.registerHandler.Get();

        //Pas de registres disponibles
        if (freeReg == null) {
            compiler.addInstruction(new LOAD(new ImmediateInteger(boolValue), GPRegister.R0)); //LOAD dans R0
            compiler.addInstruction(new PUSH(GPRegister.R0)); //PUSH R0
            return null; // On indique qu'on a push dans la pile
        }

        //Registre disponibles
        compiler.addInstruction(new LOAD(new ImmediateInteger(boolValue), freeReg));
        return freeReg;
    }
    @Override
    public String getExprValue(DecacCompiler compiler) {
        return String.valueOf(this.value);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

}
