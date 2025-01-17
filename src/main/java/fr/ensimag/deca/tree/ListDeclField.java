package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.lang.instrument.ClassDefinition;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import org.apache.log4j.Logger;

/**
 *
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public class ListDeclField extends TreeList<AbstractDeclField> {
    private static final Logger LOG = Logger.getLogger(ListDeclField.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField f : getList()) {
            f.decompile(s);
            s.println();
        }
    }


    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListFieldMembers(DecacCompiler compiler, fr.ensimag.deca.context.ClassDefinition nameClass) throws ContextualError {
        LOG.debug("verify listFieldMembers: start");
        // throw new UnsupportedOperationException("not yet implemented");
        for (AbstractDeclField f : getList()){
            f.verifyFieldMembers(compiler, nameClass, nameClass.getMembers());
        }
        LOG.debug("verify listFieldMembers: end");
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListFieldBody(DecacCompiler compiler, fr.ensimag.deca.context.ClassDefinition nameClass) throws ContextualError {
        LOG.debug("verify listFieldBody: start");
        // throw new UnsupportedOperationException("not yet implemented");
        for (AbstractDeclField f : getList()){
            f.verifyFieldBody(compiler, nameClass);
        }
        LOG.debug("verify listFieldBody: end");
    }

    protected void codeGenDeclField(DecacCompiler compiler, int superOffset, AbstractIdentifier parentClass) {


        //Si la classe n'a que Object en parent
        if (superOffset == 0) {

            for (AbstractDeclField abstractDeclField : getList())
                abstractDeclField.codeGenObjectDirectChildDeclField(compiler, superOffset);

            return;
        }

        //On charge la valeur par défault pour chaque field
        for (AbstractDeclField abstractDeclField : getList()) {
            abstractDeclField.codeGenDefaultDeclField(compiler, superOffset);
        }

        //On récupère l'adresse de l'objet qu'on push dans la pile
        RegisterOffset objectAddress = new RegisterOffset(-2, Register.LB);
        compiler.addInstruction(new LOAD(objectAddress, GPRegister.R1));
        compiler.addInstruction(new PUSH(GPRegister.R1)); //(Rien à compris à pourquoi faut empiler)

        //On initialise la parent class
        compiler.addInstruction(new BSR(new Label("init."+parentClass.getName().getName())));
        compiler.addInstruction(new SUBSP(1)); //On reset la pile

        //Initialisation explicite des champs de la classe
        for (AbstractDeclField abstractDeclField : getList()) {
            abstractDeclField.codeGenDeclField(compiler, superOffset);
        }

    }


}
