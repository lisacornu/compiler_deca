package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.*;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 * Full if/else if/else statement.
 *
 * @author gl31
 * @date 01/01/2025
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;
    private static int branchIndex = 0;//Index static

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        condition.verifyExpr(compiler, localEnv, currentClass);
        thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {

       // On récupère le résultat de la condition (qui était dans la pile/un registre)
        DVal condAddr = condition.codeGenExpr(compiler);
        GPRegister condReg = RegisterHandler.popIntoRegister(compiler, condAddr, Register.R0);

        Label startLabel = new Label("elseStart" + branchIndex);
        Label endLabel = new Label ("ifThenElseExit" + branchIndex);

        //On compare la condition dans la pile à 1 (true)

        compiler.addInstruction(new CMP(new ImmediateInteger(1),condReg));
        compiler.registerHandler.SetFree(condReg); //Free du registre de la condition

        compiler.addInstruction(new BNE(startLabel));//On saute à startLabel quand la condition est false (else)
        branchIndex++; //Incrémentation de l'index

        thenBranch.codeGenListInst(compiler); //Sinon on éxécute le then (if)
        compiler.addInstruction(new BRA(endLabel)); //Puis on saute à la fin du if-else

        compiler.addLabel(startLabel);// Début du else
        elseBranch.codeGenListInst(compiler); // Exécution du else
        compiler.addLabel(endLabel); //Fin du if-else
    }

    @Override
    public void decompile(IndentPrintStream s) {

        s.print("if (");
        condition.decompile(s);
        s.print(") {\n");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();

        s.print("} else {\n");
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }

    public void setElseBranch(ListInst elseBranch) {
        this.elseBranch = elseBranch;
    }

    public void setElseBranch(AbstractInst elseBranch){
        this.elseBranch.add(elseBranch);
    }
}
