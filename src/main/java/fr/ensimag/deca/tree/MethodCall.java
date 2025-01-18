package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.rmi.UnexpectedException;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class MethodCall extends AbstractExpr{
    private final AbstractExpr expr;                //objet
    private final AbstractIdentifier methodIdent;   //méthode
    private final ListExpr rvalueStar;              //param

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
        ClassDefinition classDef = (ClassDefinition) compiler.environmentType.defOfType(type2.getName());
        if(classDef.getMembers().get(methodIdent.getName())!=null){
            MethodDefinition methodDef = (MethodDefinition)classDef.getMembers().get(methodIdent.getName());
            Type returnType = methodDef.getType();
            Signature signMeth = methodDef.getSignature();
            int i = 0;
            for (AbstractExpr rval : rvalueStar.getList()){
                Type typeParam = rval.verifyExpr(compiler, localEnv, currentClass);
                if(!typeParam.sameType(signMeth.paramNumber(i))){
                    throw new ContextualError("ParamType is different than type you passed as argument", getLocation());
                }
            }
            methodIdent.setDefinition(methodDef);
            setType(returnType);
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
        // on réserve la place pour les paramètres + le paramètre implicite (l'objet appelant)
        compiler.addInstruction(new ADDSP(this.rvalueStar.getList().size() + 1));

        //  TODO : demander à matéo pour l'endroit ou trouver l'adresse de l'objet
        // compiler.addInstruction(new LOAD(adresse(GB), GPRegister.R0));

        // empile param implicite (objet sur qui on appelle la méthode
        compiler.addInstruction(new STORE(GPRegister.R0, new RegisterOffset(0, Register.SP)));

        // ordre de parcours ?
        for (AbstractExpr param : this.rvalueStar.getList()) {
            System.out.println(param);
//            DVal locationResult = param.codeGenExpr(compiler);
//            compiler.addInstruction(new STORE(locationResult, new RegisterOffset(-1, Register.SP)));
        }

        return Register.R0;
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
        rvalueStar.prettyPrint(s,prefix,true);
    }
}
