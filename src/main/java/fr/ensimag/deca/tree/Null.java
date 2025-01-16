package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.NullType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

/**
 * Integer literal
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Null extends AbstractExpr {
    

    public Null() {
        
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type nullType = new NullType(compiler.createSymbol("null"));
        setType(nullType);
        return nullType;
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }


    @Override
    public void printExprValue(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("null");
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
