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
        //pour l'optim 
        // si a droite c de la variable est un identificateur
        Identifier var_left=compiler.varaible_current;
        String varNameStr = var_left.getName().getName();
        if(expression instanceof Identifier){
            String varNameStr_r = ((Identifier)expression).getName().getName();
            if(compiler.variablePropa.get(varNameStr_r)!=null){//si jamais l attribut de droite a une valeur entiere connus
                ((Identifier)var_left).literal = new IntLiteral(compiler.variablePropa.get(varNameStr_r));//mettre un attribut intlitt pr la varaible en cours 
                compiler.variablePropa.put(varNameStr,compiler.variablePropa.get(varNameStr_r));//la mettre dans la table de hashage pr servire les autres
            }
            if (compiler.variablePropa_float.get(varNameStr_r)!=null){//si jamais l attribut de droite a une valeur entiere connus
                ((Identifier)var_left).float_ = new FloatLiteral(compiler.variablePropa_float.get(varNameStr_r));//mettre un attribut intlitt pr la varaible en cours 
                compiler.variablePropa_float.put(varNameStr,compiler.variablePropa_float.get(varNameStr_r));//la mettre dans la table de hashage pr servire les autres
            }
        }
        else if(expression instanceof IntLiteral){
            ((Identifier)var_left).literal=new IntLiteral(((IntLiteral)(expression)).getValue());
            compiler.variablePropa.put(varNameStr,((IntLiteral)(expression)).getValue());
        }
        //cas ou l'opérande de droite est un calcul
        else if(expression instanceof AbstractOpArith && t.isInt()){
            //résultat du calcul
            int result = (int)((AbstractOpArith) expression).evalExprValue(compiler);
            ((Identifier)var_left).literal = new IntLiteral(result);
            compiler.variablePropa.put(varNameStr, result);
        }
        else if(expression instanceof FloatLiteral){
            ((Identifier)var_left).float_=new FloatLiteral(((FloatLiteral)(expression)).getValue());
            compiler.variablePropa_float.put(varNameStr,((FloatLiteral)(expression)).getValue());
        }
        else if(expression instanceof AbstractOpArith && t.isFloat()){
            //résultat du calcul
            float result = (float)((AbstractOpArith) expression).evalExprValue(compiler);
            ((Identifier)var_left).float_ = new FloatLiteral(result);
            compiler.variablePropa_float.put(varNameStr, result);
        }
        else{
            compiler.variablePropa.put(varNameStr,null);
            compiler.variablePropa_float.put(varNameStr,null);
        }
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
