package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl31
 * @date 01/01/2025
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        Type floatType = new FloatType(compiler.createSymbol("float"));
        setType(floatType);
        return floatType; 
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
        DVal result = this.getOperand().codeGenExpr(compiler);
        compiler.addInstruction(new FLOAT(result, GPRegister.R0));
        return RegisterHandler.pushFromRegister(compiler, GPRegister.R0);
    }
}
