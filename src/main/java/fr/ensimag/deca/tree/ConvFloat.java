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

    protected double evalExprValue(DecacCompiler compiler){
        AbstractExpr operand = getOperand();

        if(operand instanceof IntLiteral){
            return ((IntLiteral) operand).getValue();
        }
        else if (operand instanceof Identifier){
            //récupère le nom de l'identificateur
            String identName = ((Identifier) operand).getName().getName();
            return compiler.variablePropa.get(identName);
        }
        //sinon c'est une opération
        else{
            return ((AbstractOpArith) operand).evalExprValue(compiler);
        }
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        Type floatType = new FloatType(compiler.createSymbol("float"));
        setType(floatType);
        return floatType; 
    }

     @Override
    public Type verifyExpr_opti(DecacCompiler compiler, EnvironmentExp localEnv,
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
        GPRegister result = RegisterHandler.popIntoRegister(compiler, this.getOperand().codeGenExpr(compiler),GPRegister.R0);
        compiler.addInstruction(new FLOAT(result, result));
        return RegisterHandler.pushFromRegister(compiler, result);
    }
}
