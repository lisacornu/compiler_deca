package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.commons.lang.Validate;

/**
 * Single precision, Cast
 *
 * @author Fabien Galzi
 * @date 15/01/2025
 */
public class Cast extends AbstractExpr {
    private AbstractIdentifier expr;
    private AbstractIdentifier type;
    public Cast(AbstractIdentifier expr, AbstractIdentifier type) {
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeExpr = expr.verifyExpr(compiler, localEnv, currentClass);
        Type typeType = type.verifyType(compiler);
        
        if(typeExpr.sameType(typeType)|| (typeExpr.isInt() && typeType.isFloat()) || (typeExpr.isFloat() && typeType.isInt())){
            setType(typeType);
        } else if(typeExpr.asClassType(null, getLocation()).isSubClassOf(type.getClassDefinition().getType())){
            setType(type.getClassDefinition().getType());
        } else if(type.getClassDefinition().getType().isSubClassOf(typeExpr.asClassType(null, getLocation()))){
            setType(type.getClassDefinition().getType());
        }else{
            throw new ContextualError("You cant convert "+ typeExpr + " into " + typeType, getLocation());
        }
        
        return getType();
    }

    @Override
    public void printExprValue(DecacCompiler compiler) {
        // TODO
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(") ");
        expr.decompile(s);
    }


    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, false);
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

}
