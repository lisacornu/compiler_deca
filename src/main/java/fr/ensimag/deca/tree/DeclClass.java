package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
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

    final private AbstractIdentifier parentClass;
    final private AbstractIdentifier className;
    final private ListDeclField      listField;
    final private ListDeclMethod     listMethod; 
    // TODO : ListeMethodes et ListeField
    

    public DeclClass(AbstractIdentifier parentClass, AbstractIdentifier className,ListDeclField listField,ListDeclMethod listMethod) {
        Validate.notNull(className);
        this.parentClass = parentClass;
        this.className = className;
        this.listField = listField;
        this.listMethod = listMethod;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("class extends " + this.parentClass.getClass().getName());
        s.println("{");
        s.indent();
        listMethod.decompile(s);
        s.println();
        s.indent();
        listField.decompile(s);
        s.println("}");

        // TODO : print pour les methodes et field
        
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        // throw new UnsupportedOperationException("not yet implemented");
        if (parentClass.verifyType(compiler).isClass()){
            try{
                compiler.environmentType.addOfTypeClass(compiler, className.getName().getName());
                ClassType classType = new ClassType(className.getName(), getLocation(), parentClass.getClassDefinition());
                // if (parentClass.getName().equals("Object")){
                //     classType = new ClassType(className, getLocation(), (fr.ensimag.deca.context.ClassDefinition) compiler.environmentType.defOfType(compiler.createSymbol("Object")));
                // }
                fr.ensimag.deca.context.ClassDefinition classDef = classType.getDefinition();
                className.setDefinition(classDef);
                className.setType(classType);
            } catch (DoubleDefException e){
                throw new ContextualError("This class as already been defined "+compiler.getClass().getName(), getLocation());
            }
        } throw new ContextualError("The superClass is not a class", getLocation());

    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        className.getClassDefinition().setNumberOfFields(listField.size());
        listField.verifyListFieldMembers(compiler, className.getClassDefinition());
        className.getClassDefinition().setNumberOfMethods(listMethod.size());
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
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iter(f);
        parentClass.iter(f);
    }

    public AbstractIdentifier getClassName() {
        return this.className;
    }

    void codeGenDeclClass(DecacCompiler compiler) {
        compiler.addComment("--------------------------------------------------");
        compiler.addComment("\t\tClasse "+className);
        compiler.addComment("--------------------------------------------------");

        //Initialisation des champs
        compiler.addLabel(new Label("init."+className));

        //Methodes
        for(AbstractDeclMethod abstractMethod : listMethod.getList()) {
            abstractMethod.codeGenMethod(compiler, this);
        }
    }

    @Override
    protected void codeGenVTable(DecacCompiler compiler) {
        compiler.addComment("Code de la table des méthode de : " + this.className.getName().getName());

        // on stocke dans la classDefinition de cette classe l'@ de départ de sa table des méthodes
        ClassDefinition def = (ClassDefinition) this.className.getDefinition();
        def.setDefinitionAdress(compiler.headOfGBStack);

        // store @ de la super classe
        if (this.className.getName().getName().equals("Object")) {
            compiler.addInstruction(new LOAD(null, GPRegister.R0));
        } else {
            compiler.addInstruction(new LEA(this.parentClass.getClassDefinition().getDefinitionAdress(), GPRegister.R0));
        }
        compiler.addInstruction(new STORE(GPRegister.R0, new RegisterOffset(compiler.headOfGBStack, GPRegister.GB)));

        // store le pointeur vers chaque méthode de la classe
        for (AbstractDeclMethod m : this.listMethod.getList()) {
            compiler.addInstruction(new LOAD (new LabelOperand ( new Label (
                    "code." + this.className.getName().getName() + "." + m.getMethodName().getName().getName()
            )), GPRegister.R0));

            MethodDefinition metDef = (MethodDefinition) m.getMethodName().getDefinition();
            compiler.addInstruction(new STORE (
                    GPRegister.R0, new RegisterOffset(compiler.headOfGBStack + metDef.getIndex(), GPRegister.GB)
            ));
        }

        compiler.headOfGBStack += this.listMethod.size() + 1;
    }

}
