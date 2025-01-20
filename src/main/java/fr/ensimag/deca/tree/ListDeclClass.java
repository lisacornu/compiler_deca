package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c : getList()){
            c.verifyClass(compiler);
        }
        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClassMembers: start");
        for (AbstractDeclClass c : getList()){
            c.verifyClassMembers(compiler);
        }
        LOG.debug("verify listClassMembers: end");
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClassBody: start");
        for (AbstractDeclClass c : getList()){
            c.verifyClassBody(compiler);
        }
        LOG.debug("verify listClassBody: end");
    }

    protected void codeGenListDeclClass(DecacCompiler compiler) {
        this.codeGenDeclObject(compiler);
        for(AbstractDeclClass abstractDeclClass : this.getList()) {
            abstractDeclClass.codeGenDeclClass(compiler);
        }
    }

    private void codeGenDeclObject (DecacCompiler compiler) {
        compiler.addLabel(new Label("code.Object.equals"));

        compiler.addInstruction(new TSTO(2));
        compiler.addInstruction(new BOV(new Label("pile_pleine")));

        compiler.addInstruction(new PUSH(GPRegister.getR(2)));

        // corps de la méthode
        // other
        compiler.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), GPRegister.getR(2)));

        compiler.addInstruction(new CMP(new RegisterOffset(-2, Register.LB), GPRegister.getR(2)));
        compiler.addInstruction(new SEQ(GPRegister.R0));

        compiler.addInstruction(new POP(GPRegister.getR(2)));
        compiler.addInstruction(new RTS());
    }
}
