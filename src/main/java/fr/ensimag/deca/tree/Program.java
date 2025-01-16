package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
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
        classes.verifyListClassMembers(compiler);
        classes.verifyListClassBody(compiler);
        LOG.debug("verify Classes: end");

        main.verifyMain(compiler);
        
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenVTable(DecacCompiler compiler) {
        this.classes.getList().get(0).setParentClassAdress(1);

        compiler.addInstruction(new LOAD(new NullOperand(), GPRegister.R0));
        compiler.addInstruction(new STORE(GPRegister.R0, new RegisterOffset(compiler.headOfGBStack, GPRegister.GB)));

        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), GPRegister.R0));
        compiler.addInstruction(new STORE(GPRegister.R0, new RegisterOffset(compiler.headOfGBStack+1, GPRegister.GB)));

        compiler.headOfGBStack+=2;


        for (AbstractDeclClass c : this.classes.getList()) {
            c.codeGenVTable(compiler);
        }
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {

        compiler.addComment("--------------------------------------------------");
        compiler.addComment("\t\tCode du programme principal");
        compiler.addComment("--------------------------------------------------");
        main.codeGenMain(compiler);

        classes.codeGenListDeclClass(compiler);
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
