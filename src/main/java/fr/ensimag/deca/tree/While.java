package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import org.apache.commons.lang.Validate;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class While extends AbstractInst {
    private AbstractExpr condition;
    private ListInst body;
    private static int nbNestedWhiles = 0;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {


        nbNestedWhiles++;
        Label debutWhile = new Label ("while" + nbNestedWhiles);
        Label finWhile = new Label ("whileExit" + nbNestedWhiles);
        compiler.addLabel(debutWhile);

        // On récupère le résultat de la condition (qui était dans la pile/un registre)
        DVal condAddr = condition.codeGenExpr(compiler);
        GPRegister condReg = RegisterHandler.popIntoRegister(compiler, condAddr, Register.R0);

        compiler.addInstruction(new CMP(new ImmediateInteger(1), condReg)); //compare la condition avec vrai
        compiler.registerHandler.SetFree(condReg); //Free du registre de la condition

        compiler.addInstruction(new BNE(finWhile)); //saut si la condition n'est pas vérifiée

        body.codeGenListInst(compiler); //génère le code du corps de la boucle

        compiler.addInstruction(new BRA(debutWhile));   //retour au début du while

        compiler.addLabel(finWhile);
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        condition.verifyExpr(compiler, localEnv, currentClass);
        body.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

}
