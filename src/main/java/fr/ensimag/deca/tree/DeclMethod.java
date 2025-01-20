package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.context.Signature;

import java.io.PrintStream;
import fr.ensimag.deca.context.ClassDefinition;
import java.lang.reflect.Parameter;
import java.rmi.UnexpectedException;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Declaration of a method
 * 
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public class DeclMethod extends AbstractDeclMethod {

    // final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier methodName;
    final private ListDeclParam parameters;
    final private AbstractMethodBody body;

    public AbstractIdentifier getMethodName() {
        return methodName;
    }

    public AbstractIdentifier getTypeMethod(){
        return type;
    }

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier methodName, ListDeclParam parameters, AbstractMethodBody body) { // Visibility visibility,
        // this.visibility = visibility;
        this.type = type;
        this.methodName = methodName;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        methodName.decompile(s);
        s.print("(");
        parameters.decompile(s);
        s.print("){");
        body.decompile(s);
        s.print("}");
        // TODO 
        
    }


    @Override
    protected void verifyMethodMembers(DecacCompiler compiler, 
                                       ClassDefinition nameClass) throws ContextualError {

       
        Signature sign = parameters.verifyListParamMembers(compiler, nameClass);
        if (nameClass.getSuperClass().getMembers().get(methodName.getName())==null){
            try{
                nameClass.incNumberOfMethods();
                MethodDefinition methodDef = new MethodDefinition(type.verifyTypeMethod(compiler), getLocation(), sign, nameClass.getNumberOfMethods());
                nameClass.getMembers().declare(methodName.getName(), methodDef);
                methodName.setDefinition(methodDef);
                methodName.setType(type.verifyTypeMethod(compiler)); 
            } catch (DoubleDefException e){
                throw new ContextualError("The method as already been declared before.", getLocation());
            }
        } else { // Pour le override
            ExpDefinition parentDef = nameClass.getSuperClass().getMembers().get(methodName.getName());
            MethodDefinition parentMethodDef = (MethodDefinition) parentDef;
            if(!parentDef.getType().sameType(type.verifyTypeMethod(compiler))){
                throw new ContextualError("You overwrite a method without good type", getLocation());
            } else {
                Signature parentSign = parentMethodDef.getSignature();
                if (parentSign.size()!=sign.size()){
                    throw new ContextualError("They are not the same number of parameters : " + parentSign.size() + " for parent class "+ sign.size() + " for class.", getLocation());
                }
                for (int i=0;i<parentSign.size();i++){
                    if (!sign.paramNumber(i).sameType(parentSign.paramNumber(i))){
                        throw new ContextualError("The signatures are not the same : "+ sign.paramNumber(i) + " should be " + parentSign.paramNumber(i), getLocation());
                    }
                }
                try{
                nameClass.incNumberOfMethods();
                
                MethodDefinition methodDef = new MethodDefinition(type.verifyTypeMethod(compiler), getLocation(), sign, parentMethodDef.getIndex());
                methodName.setDefinition(methodDef);
                methodName.setType(type.verifyTypeMethod(compiler));
                nameClass.getMembers().declare(methodName.getName(), methodDef);

                } catch (DoubleDefException e){
                    throw new ContextualError("The method as already been declared before.", getLocation());
                }
            }
        }

        

    }

    
    @Override
    protected void verifyMethodBody(DecacCompiler compiler, ClassDefinition nameClass) throws ContextualError {
        EnvironmentExp envExp = new EnvironmentExp(nameClass.getMembers());
        parameters.verifyListParamBody(compiler, envExp);
        body.verifyMethodBody(compiler, envExp, nameClass, type.verifyTypeMethod(compiler));

    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // visibility.prettyPrint(s,prefix,false);
        type.prettyPrint(s, prefix,false);
        methodName.prettyPrint(s,prefix,false);
        parameters.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        methodName.iter(f);
        parameters.iter(f);
        body.iter(f);
    }

    private void setParamAdress () {
        int index = -3;
        for (AbstractDeclParam p : this.parameters.getList()) {
            ((DeclParam)p).getNameParam().getExpDefinition().setOperand(new RegisterOffset(index, Register.LB));
            index--;
        }
    }

    @Override
    protected void codeGenMethod(DecacCompiler compiler, DeclClass declClass) {
        this.setParamAdress();
        compiler.addComment("---------- Code de la methode " + methodName.getName());
        compiler.addLabel(new Label("code."+declClass.getClassName().getName()+"."+methodName.getName()));

        body.codeGenMethodBody(compiler, declClass, methodName.getName().getName());

        if (this.type.getType().isVoid()) {
            compiler.addInstruction(new BRA(new Label("fin."+ declClass.getClassName().getName().getName()+"."+this.methodName.getName().getName())));
        }

        //Si il manque un return :
        compiler.addInstruction(new WSTR("Erreur : sortie de la methode "+declClass.getClassName().getName()+"."+methodName.getName()+" sans return"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("fin."+declClass.getClassName().getName().getName()+"."+methodName.getName()));
        compiler.addInstruction(new RTS());
    }

}
