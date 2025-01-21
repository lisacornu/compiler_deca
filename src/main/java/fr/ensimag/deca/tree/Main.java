package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;


/**
 * @author gl31
 * @date 01/01/2025
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables, ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        
        EnvironmentExp localEnv = new EnvironmentExp(null); // nécessité environnement pour démarrer inititalisé à null
        LOG.debug("verify Variable : start");
        this.declVariables.verifyListDeclVariable(compiler, localEnv, null); // on verifie en premier les variable 
        LOG.debug("verify Variable : end");
        LOG.debug("verify Inst : start");
        this.insts.verifyListInst_opti(compiler, localEnv, null, null); //on vérifie les inst
        LOG.debug("verify Inst : end");
        LOG.debug("verify Main: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        compiler.opti=1;
        compiler.addComment("---------- Déclaration des variables");
        declVariables.codeGenListDeclVar(compiler);
        compiler.addComment("---------- Instructions");
        insts.codeGenListInst(compiler, null);
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
