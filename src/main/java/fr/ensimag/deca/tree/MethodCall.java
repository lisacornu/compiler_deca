package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.rmi.UnexpectedException;
import java.util.ArrayList;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

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
        ClassDefinition classDef;
        if(expr==null && currentClass!=null){
            classDef = currentClass;
        }else if(expr==null && currentClass==null){
            throw new ContextualError("You cant call the method on nothing while you are not in the class", getLocation());
        } else{
            Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);
            classDef = (ClassDefinition) compiler.environmentType.defOfType(type2.getName());
        }
        
        if(classDef.getMembers().get(methodIdent.getName())!=null){
            MethodDefinition methodDef = (MethodDefinition)classDef.getMembers().get(methodIdent.getName());
            Type returnType = methodDef.getType();
            Signature signMeth = methodDef.getSignature();
            int i = 0;
            if(rvalueStar.getList().size()!=signMeth.size()){
                throw new ContextualError("They are not the same number of param", getLocation());
            }
            for (AbstractExpr rval : rvalueStar.getList()){
                Type typeParam = rval.verifyExpr(compiler, localEnv, currentClass);
                if(!typeParam.sameType(signMeth.paramNumber(i))){
                    throw new ContextualError("ParamType is different than type you passed as argument", getLocation());
                }
                i++;
            }
            methodIdent.setDefinition(methodDef);
            setType(returnType);
            return returnType;
        } else{
            throw new ContextualError("The method was not defined before " + methodIdent.getName(), getLocation());
        }
        
    }
        @Override
    public Type verifyExpr_opti(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
        ClassDefinition classDef;
        if(expr==null && currentClass!=null){
            classDef = currentClass;
        }else if(expr==null && currentClass==null){
            throw new ContextualError("You cant call the method on nothing while you are not in the class", getLocation());
        } else{
            Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);
            classDef = (ClassDefinition) compiler.environmentType.defOfType(type2.getName());
        }
        
        if(classDef.getMembers().get(methodIdent.getName())!=null){
            MethodDefinition methodDef = (MethodDefinition)classDef.getMembers().get(methodIdent.getName());
            Type returnType = methodDef.getType();
            Signature signMeth = methodDef.getSignature();
            int i = 0;
            if(rvalueStar.getList().size()!=signMeth.size()){
                throw new ContextualError("They are not the same number of param", getLocation());
            }
            for (AbstractExpr rval : rvalueStar.getList()){
                Type typeParam = rval.verifyExpr_opti(compiler, localEnv, currentClass);
                if(!typeParam.sameType(signMeth.paramNumber(i))){
                    throw new ContextualError("ParamType is different than type you passed as argument", getLocation());
                }
                i++;
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
        DVal locationResult = this.codeGenExpr(compiler);
        GPRegister reg = RegisterHandler.popIntoRegister(compiler, locationResult, Register.R1);
        compiler.addInstruction(new LOAD(reg, Register.R1));

        if (this.methodIdent.getMethodDefinition().getType().isFloat()) {
            compiler.addInstruction(new WFLOAT());
        } else if (this.methodIdent.getMethodDefinition().getType().isInt()) {
            compiler.addInstruction(new WINT());
        }
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler){
        // on réserve la place pour les paramètres + le paramètre implicite (l'objet appelant)
        ArrayList<GPRegister> savedRegs = compiler.registerHandler.saveFullRegs(compiler);
        compiler.addInstruction(new ADDSP(this.rvalueStar.getList().size() + 1));

        // empilement du paramètre implicite (objet sur qui on appelle la méthode)
        GPRegister exprReg;
        if (expr == null) { //this implicite
            exprReg = Register.R0;
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), exprReg));
        } else {
            DVal exprAddr = expr.codeGenExpr(compiler);
            exprReg = RegisterHandler.popIntoRegister(compiler, exprAddr, Register.R0);
        }
        compiler.addInstruction(new STORE(exprReg, new RegisterOffset(0, Register.SP)));
        compiler.registerHandler.SetFree(exprReg);

        // empilement des paramètres de la méthode
        int offset = -1;
        for (AbstractExpr param : this.rvalueStar.getList()) {

            DVal locationResult = param.codeGenExpr(compiler);
            GPRegister locationResultReg = RegisterHandler.popIntoRegister(compiler, locationResult, Register.R1);
            compiler.addInstruction(new STORE(locationResultReg, new RegisterOffset(offset, Register.SP)));
            compiler.registerHandler.SetFree(locationResultReg);

            offset--;
        }

        // vérification que la paramètre implicite n'est pas null
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.R0));
        compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
        compiler.addInstruction(new BEQ(new Label("dereferencement.null")));

        // Récupération table des méthodes, sauvegarde des registres puis appel de la méthode en question
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), Register.R0));


        compiler.addInstruction(new BSR(new RegisterOffset(this.methodIdent.getMethodDefinition().getIndex(), Register.R0)));


        compiler.addInstruction(new SUBSP(this.rvalueStar.getList().size() + 1));
        compiler.registerHandler.restoreRegs(compiler, savedRegs);
        return RegisterHandler.pushFromRegister(compiler, Register.R0);
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

        if (expr != null) expr.iter(f);
        methodIdent.iter(f);
        rvalueStar.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {

        //expr.prettyPrint(s, prefix, false);

        if (expr != null) expr.prettyPrint(s, prefix, false);
        methodIdent.prettyPrint(s,prefix,false);
        rvalueStar.prettyPrint(s,prefix,true);
    }
}
