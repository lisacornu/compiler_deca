package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;

import java.io.PrintStream;
import java.lang.reflect.Field;

import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Selection
 *
 * @author Fabien Galzi
 * @date 15/01/2025
 */
public class Selection extends AbstractLValue {
    private AbstractExpr expr;
    private AbstractIdentifier fieldIdent;

    public Selection(AbstractExpr expr, AbstractIdentifier fieldIdent){
        this.expr = expr;
        this.fieldIdent = fieldIdent;
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass)
            throws ContextualError {
        Type type = expr.verifyExpr(compiler, localEnv, currentClass);
        if(!type.isClass()){
            throw new ContextualError("The class is not defined for this type : " + type, getLocation());
        }


        FieldDefinition fieldDef;
        if (currentClass==null){
            ClassDefinition classParentDef = (ClassDefinition)compiler.environmentType.defOfType(type.getName()); 
            fieldDef = (FieldDefinition) classParentDef.getMembers().get(fieldIdent.getName());
        }else if(currentClass.getType() !=type){
            ClassDefinition classParentDef = (ClassDefinition)compiler.environmentType.defOfType(type.getName());
            fieldDef = (FieldDefinition) classParentDef.getMembers().get(fieldIdent.getName()); 
        }else{
            fieldDef = (FieldDefinition) currentClass.getMembers().get(fieldIdent.getName());
        }

        if(fieldDef==null){
            throw new ContextualError("You cant get visibility because your types dont correspond", getLocation());
        }
        if (fieldDef.getVisibility()==Visibility.PROTECTED){
            if (currentClass==null){
                throw new ContextualError("You cant get a protected type", getLocation());
            }
            if(!type.sameType(currentClass.getType())){
                throw new ContextualError("Subtype(expr type) is not a subtype of super class(currentClass)", getLocation());
            }
            if(!currentClass.getType().sameType(fieldDef.getContainingClass().getType())){
                throw new ContextualError("Subtype(currentClass) is not a subtype of super class(field Class)", getLocation());
            }
            
        }
        fieldIdent.setDefinition(fieldDef);
        setType(fieldDef.getType());
        return fieldDef.getType();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(".");
        fieldIdent.decompile(s);
        //TODO
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        fieldIdent.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        fieldIdent.prettyPrint(s, prefix, true);

    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {

        //On récupère l'adresse de l'objet
        DVal exprAddr = expr.codeGenExpr(compiler);
        GPRegister exprReg = RegisterHandler.popIntoRegister(compiler, exprAddr, GPRegister.R0);

        //Test de déferencement null
        compiler.addInstruction(new CMP(new NullOperand(), exprReg));
        compiler.addInstruction(new BEQ(new Label("dereferencement.null")));

        //On accede au bon field
        RegisterOffset fieldHeapAddr = new RegisterOffset(fieldIdent.getFieldDefinition().getIndex(),exprReg);

        //On return la valeur du register offset
        return RegisterHandler.pushFromDVal(compiler, fieldHeapAddr, GPRegister.R1);
    }

    //Renvoi un register offset en cas d'assign
    public DAddr codeGenExprAddr(DecacCompiler compiler, GPRegister tempReg) {

        //On récupère l'adresse de l'objet
        DVal exprAddr = expr.codeGenExpr(compiler);
        GPRegister exprReg = RegisterHandler.popIntoRegister(compiler, exprAddr, tempReg);

        //Test de déferencement null
        compiler.addInstruction(new CMP(new NullOperand(), exprReg));
        compiler.addInstruction(new BEQ(new Label("dereferencement.null")));

        //On accede au bon field
        RegisterOffset fieldHeapAddr = new RegisterOffset(fieldIdent.getFieldDefinition().getIndex(),exprReg);

        //On return le register offset
        return fieldHeapAddr;
    }

    @Override
    public void printExprValue(DecacCompiler compiler){


        DVal exprAddr = codeGenExpr(compiler);
        compiler.registerHandler.SetFree(exprAddr);
        GPRegister exprReg =  RegisterHandler.popIntoRegister(compiler, exprAddr, Register.R1);
        compiler.addInstruction(new LOAD(exprReg, GPRegister.R1));

        if (fieldIdent.getExpDefinition().getType().isFloat()) {
            compiler.addInstruction(new WFLOAT());
        } else if (fieldIdent.getExpDefinition().getType().isInt()) {
            compiler.addInstruction(new WINT());
        }
    }
}
