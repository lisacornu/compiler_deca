package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
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
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
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

        // TODO 
        
    }

    @Override
    protected void verifyMethodMembers(DecacCompiler compiler, 
                                       ClassDefinition nameClass) throws ContextualError {
        //Override pas encore fait
        // if (type.verifyType(compiler).isVoid()){
        //     throw new ContextualError("The type of this method is void.", getLocation());
        // }
        Signature sign = parameters.verifyListParamMembers(compiler, nameClass);
        nameClass.incNumberOfMethods();
        MethodDefinition methodDef = new MethodDefinition(type.verifyTypeMethod(compiler), getLocation(), sign, nameClass.getNumberOfMethods());
        System.out.println(nameClass.getSuperClass().getMembers().get(compiler.createSymbol("A"))+ " " + methodName.getName());
        // if (nameClass.getSuperClass().getMembers().get(methodName.getName())==null){
            try{
                nameClass.getSuperClass().getMembers().declare(methodName.getName(), methodDef);
            } catch (DoubleDefException e){
                throw new ContextualError("The method as already been declared before.", getLocation());
            }
        // } else {
        //     throw new ContextualError("You can't do it because env_exp_super(name) is not defined", getLocation());
        // }
        // methodName.verifyExpr(compiler, nameClass.getMembers(), nameClass); // ou mettre en parametre le envExp
        methodName.setDefinition(methodDef);
        

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
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        methodName.iter(f);
    }

    @Override
    protected void codeGenMethod(DecacCompiler compiler, DeclClass declClass) {

        compiler.addComment("---------- Code de la methode " + methodName.getName());
        compiler.addLabel(new Label("code."+declClass.getClassName().getName()+"."+methodName.getName()));

        body.codeGenMethodBody(compiler, declClass);

        //Si il manque un return :
        compiler.addInstruction(new WSTR("Erreur : sortie de la methode "+declClass.getClassName().getName()+"."+methodName.getName()+" sans return"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
        compiler.addLabel(new Label("fin."+declClass.getClassName().getName()+"."+methodName.getName()));
    }

}
