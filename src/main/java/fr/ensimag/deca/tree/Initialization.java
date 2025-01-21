package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import org.apache.commons.lang.Validate;

/**
 * @author gl31
 * @date 01/01/2025
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        getExpression().verifyRValue(compiler, localEnv, currentClass, t);
        if (getExpression().getType().isInt() && t.isFloat()){
            setExpression(new ConvFloat(getExpression()));
        }
        expression.setType(t);
    }
    @Override
    protected void verifyInitialization_opti(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        getExpression().verifyRValue_opti(compiler, localEnv, currentClass, t);
        if (getExpression().getType().isInt() && t.isFloat()){
            setExpression(new ConvFloat(getExpression()));
        }
        expression.setType(t);
    }
    public DVal codeGenInit(DecacCompiler compiler){
        return expression.codeGenExpr(compiler);
    }

    public DVal codeGenInit_opti(DecacCompiler compiler){
        return expression.codeGenExpr_opti(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" = ");
        expression.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }
}
