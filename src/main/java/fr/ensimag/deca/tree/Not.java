package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type operand = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (operand.isBoolean()){
            this.setType(operand);
            return operand;
        }else{
            throw new ContextualError("Operand is not boolean",getLocation());
        }
    }
     @Override
    public Type verifyExpr_opti(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type operand = getOperand().verifyExpr_opti(compiler, localEnv, currentClass);
        if (operand.isBoolean()){
            this.setType(operand);
            return operand;
        }else{
            throw new ContextualError("Operand is not boolean",getLocation());
        }
    }

    // permet de discerner les labels entre les différents usages de NOT au cours du programme
    static int not_cpt = 0;

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {

        DVal operandAddr = this.getOperand().codeGenExpr(compiler);
        GPRegister op = RegisterHandler.popIntoRegister(compiler,operandAddr , GPRegister.R0);

        compiler.addInstruction(new CMP(0, op));
        compiler.addInstruction(new BEQ(new Label("not_is_true" + not_cpt)));

        // si on est ici, cond == true donc !cond == false
        compiler.addInstruction(new LOAD(0, op));

        compiler.addInstruction(new BRA(new Label("not_end_case" + not_cpt)));

        // cond == false donc !cond == true
        compiler.addLabel(new Label("not_is_true" + not_cpt));
        compiler.addInstruction(new LOAD(1, op));

        compiler.addLabel(new Label("not_end_case" + not_cpt));

        GPRegister resultLocation = RegisterHandler.pushFromRegister(compiler, op);
        not_cpt++;
        return resultLocation;
    }

    @Override
    protected String getOperatorName() {
        return "!";
    }
}
