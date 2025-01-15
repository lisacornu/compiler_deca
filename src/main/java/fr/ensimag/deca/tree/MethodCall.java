package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.rmi.UnexpectedException;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;

public class MethodCall extends AbstractExpr{
    private final AbstractExpr expr;
    private final AbstractIdentifier methodIdent;
    private final ListExpr rvalueStar;

    public MethodCall(AbstractExpr expr,AbstractIdentifier methoIdent,ListExpr rvalueStar){
        this.rvalueStar = rvalueStar;
        this.expr = expr;
        this.methodIdent = methoIdent;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);
        if(currentClass.getMembers().get(methodIdent.getName())!=null){
            Type typeMethod = methodIdent.verifyExpr(compiler, localEnv, currentClass);
            Type returnType = methodIdent.verifyType(compiler);
            for (AbstractExpr rval : rvalueStar.getList()){
                rval.verifyRValue(compiler, localEnv, currentClass, typeMethod);
            }
            return returnType;
        } else{
            throw new ContextualError("The method was not defined before " + methodIdent.getName(), getLocation());
        }
        
    }
    
    @Override
    public void printExprValue(DecacCompiler compiler){
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler){
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void decompile(IndentPrintStream s) {

        
        expr.decompile(s);
        s.print(".");
        methodIdent.decompile(s);
        s.print("(");
        rvalueStar.decompile(s);
        s.print(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        methodIdent.iter(f);
        rvalueStar.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        methodIdent.prettyPrint(s,prefix,false);
        rvalueStar.prettyPrint(s,prefix,false);
    }
}
