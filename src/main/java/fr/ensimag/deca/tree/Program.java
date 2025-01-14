package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        // On fait les 3 passes
        LOG.debug("verify Classes: start");
        classes.verifyListClass(compiler);
        classes.verifyListClassBody(compiler);
        classes.verifyListClassMembers(compiler);
        LOG.debug("verify Classes: end");

        main.verifyMain(compiler);
        
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenVTable(DecacCompiler compiler) {
        for (AbstractDeclClass c : this.classes.getList()) {
            c.codeGenVTable(compiler);
        }
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {

        // A FAIRE: compléter ce squelette très rudimentaire de code
        compiler.addComment("Main program");
        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());

        // gestion de dépassement de pile
        compiler.addComment("Erreur : dépassement de pile");
        compiler.addLabel(new Label("pile_pleine"));
        compiler.addInstruction(new WSTR("Erreur : dépassement de la taille de pile autorisée"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
