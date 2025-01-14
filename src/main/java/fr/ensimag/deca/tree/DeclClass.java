package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import java.lang.instrument.ClassDefinition;

import org.apache.commons.lang.Validate;

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

}
