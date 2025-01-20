package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import java.util.ArrayList;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.context.ClassDefinition;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl31
 * @date 01/01/2025
 */
public class DeclClass extends AbstractDeclClass {

    final private AbstractIdentifier className;
    final private AbstractIdentifier parentClass;
    final private ListDeclField      listField;
    final private ListDeclMethod     listMethod; 
    // TODO : ListeMethodes et ListeField

    public void setParentClassAdress (int i) {
        ClassDefinition def = this.parentClass.getClassDefinition();
        def.setDefinitionAdress(i);
    }

    public DeclClass(AbstractIdentifier parentClass, AbstractIdentifier className,ListDeclField listField,ListDeclMethod listMethod) {
        Validate.notNull(className);
        this.parentClass = parentClass;
        this.className = className;
        this.listField = listField;
        this.listMethod = listMethod;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (parentClass!=null || parentClass.getName().getName()!="Object"){
            s.println("class  "+ className.getName().getName() + "  extends " + parentClass.getName().getName());
        } else {
            s.println("class " + className.getName().getName());
        }
        s.println("{");
        s.indent();
        listMethod.decompile(s);
        s.println();
        s.indent();
        listField.decompile(s);
        s.unindent();
        s.println("}");

        
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        // throw new UnsupportedOperationException("not yet implemented");
        
        // TODO : à vérifier : le verifyType s'en occupe deja car on ne peut définir que des classes avant une autre classe donc nécessairement pas défini
        if (!parentClass.verifyType(compiler).isClass()){
            throw new ContextualError("The superClass is not a class", getLocation());
        }

        ClassDefinition parentClassDef = (ClassDefinition) compiler.environmentType.defOfType(parentClass.getName());
        ClassType classType = new ClassType(className.getName(), getLocation(), parentClassDef);
        ClassDefinition classDef = new ClassDefinition(classType, getLocation(), parentClassDef);
        className.setDefinition(classDef);
        className.setType(classType);
        
        try{
            compiler.environmentType.addOfTypeClass(compiler,className.getName().getName(), classDef);
            
        } catch (DoubleDefException e){
            throw new ContextualError("This class has already been defined "+compiler.getClass().getName(), getLocation());
        }



    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        // className.getClassDefinition().setNumberOfFields(listField.size());
        className.getClassDefinition().setNumberOfFields(parentClass.getClassDefinition().getNumberOfFields());
        listField.verifyListFieldMembers(compiler, className.getClassDefinition());
        // className.getClassDefinition().setNumberOfMethods(listMethod.size());
        className.getClassDefinition().setNumberOfMethods(parentClass.getClassDefinition().getNumberOfMethods());
        listMethod.verifyListMethodMembers(compiler, className.getClassDefinition());

    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        // TODO : apres tests ajouter la condition 3.5
        listField.verifyListFieldBody(compiler, className.getClassDefinition());
        listMethod.verifyListMethodBody(compiler, className.getClassDefinition());
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s,prefix, false);
        parentClass.prettyPrint(s, prefix,false);
        listField.prettyPrint(s,prefix,false);
        listMethod.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iter(f);
        parentClass.iter(f);
        listField.iter(f);
        listMethod.iter(f);
    }

    public AbstractIdentifier getClassName() {
        return this.className;
    }


    protected void codeGenDeclClass(DecacCompiler compiler) {


        compiler.addComment("--------------------------------------------------");
        compiler.addComment("\t\tClasse "+className.getName().getName());
        compiler.addComment("--------------------------------------------------");


        //Initialisation des champs
        compiler.addComment("---------- Initialisation des champs de "+className.getName().getName());
        compiler.addLabel(new Label("init."+className.getName().getName()));

        listField.codeGenDeclField(compiler, parentClass.getClassDefinition().getNumberOfFields(), parentClass);
        compiler.addInstruction(new RTS());

        //Methodes
        for(AbstractDeclMethod abstractMethod : listMethod.getList()) {
            abstractMethod.codeGenMethod(compiler, this);
        }

        compiler.addInstruction(new RTS());
    }


    private void buildMethodArray () {
        ArrayList<String> methodArray = this.className.getClassDefinition().getMethodArray();

        if (this.parentClass.getName().getName().equals("Object")) {
            methodArray.add("code.Object.equals");
        } else {
            methodArray.addAll(this.parentClass.getClassDefinition().getMethodArray());
        }

        for (AbstractDeclMethod m : this.listMethod.getList()) {
            int index = m.getMethodName().getMethodDefinition().getIndex();
            String methodName = "code." + this.className.getName().getName() + "." + m.getMethodName().getName().getName();

            if (index < methodArray.size()) {
                methodArray.set(index, methodName);
            } else {
                methodArray.add(methodName);
            }
        }
    }


    @Override
    protected int codeGenVTable(DecacCompiler compiler) {
        int methodTableSize = 0;
        compiler.addComment("Code de la table des méthode de : " + this.className.getName().getName());

        // on stocke dans la classDefinition de cette classe l'@ de départ de sa table des méthodes
        this.className.getClassDefinition().setDefinitionAdress(compiler.headOfGBStack);

        // Génération de l'adresse de la super classe
        compiler.addInstruction(new LEA(this.parentClass.getClassDefinition().getDefinitionAdress(), GPRegister.R0));
        compiler.addInstruction(new STORE(GPRegister.R0, new RegisterOffset(compiler.headOfGBStack, GPRegister.GB)));
        methodTableSize++;

        // construction du tableau des méthodes de cette classe
        this.buildMethodArray();

        // génération de la partie méthode de la table des méthodes
        methodTableSize += this.codeGenMethodsVTable(compiler);

        compiler.headOfGBStack++;
        return methodTableSize;
    }


    private int codeGenMethodsVTable (DecacCompiler compiler) {
        int methodTableSize = 0;
        for (String methodName : this.className.getClassDefinition().getMethodArray()) {
            compiler.headOfGBStack++;
            compiler.addInstruction(new LOAD(new LabelOperand(new Label(methodName)), GPRegister.R0));
            compiler.addInstruction(new STORE(GPRegister.R0, new RegisterOffset(compiler.headOfGBStack, Register.GB)));
            methodTableSize++;
        }
        return methodTableSize;
    }

}
