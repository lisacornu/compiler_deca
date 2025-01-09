package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

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
    private static int nbIfElseBranch = 0;//gerer les label pr ne pas le declarer 2fois

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
       // throw new UnsupportedOperationException("not yet implemented");
       //BRA saute peu importe la condition et BNE saute si c pas egale a true donc faut combiner les 2 
        condition.codeGenInst(compiler);//fait la condition sa doit retourner 1 ou 0
        Label fin_if_else=new Label ("end_if_else" + nbIfElseBranch);
       // i++;
        compiler.addInstruction(new POP(Register.getR(2)));
        compiler.addInstruction(new CMP(new ImmediateInteger(1),Register.getR(2)) );//il compare 1 au resultat d'avant je suppose pr l 'instant qu on utilise que R2'
        Label my_label=new Label("else" + nbIfElseBranch);//label doit avoir un nom unique et la regle du nom est decrit dans label
        nbIfElseBranch++;
        compiler.addInstruction(new BNE(my_label));//je cree un jmp quand c faux
        thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(fin_if_else));
        compiler.addLabel(my_label);//a partir de la c bon 
        elseBranch.codeGenListInst(compiler);
        compiler.addLabel(fin_if_else);

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
