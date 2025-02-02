package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Single precision, InstanceOf
 *
 * @author Fabien Galzi
 * @date 15/01/2025
 */
public class InstanceOf extends AbstractExpr {
    private AbstractExpr expr;
    private AbstractIdentifier type;
    public InstanceOf(AbstractExpr expr, AbstractIdentifier type) {
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeExpr = expr.verifyExpr(compiler, localEnv, currentClass);
        Type typeType = type.verifyType(compiler);
        if(!((typeExpr.isClass()|| typeExpr.isNull())||typeType.isClass())){
            throw new ContextualError("You cant do Instanceof " + typeExpr, getLocation());
        }
        setType(compiler.environmentType.defOfType(compiler.createSymbol("boolean")).getType());
        return getType();
    }

    @Override
    public void printExprValue(DecacCompiler compiler) {
        // TODO
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        expr.decompile(s);
        s.print(" instanceOf ");
        type.decompile(s);
        s.print(")");
    }


    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        type.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, true);
    }

    static private int cpt_instanceof = 0;

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Instanceof n'a pas pu être implémenter");

//        if (!this.expr.getType().isClass()) {
//            compiler.addInstruction(new LOAD(0, GPRegister.R0));
//            return RegisterHandler.pushFromRegister(compiler, GPRegister.R0);
//        }
//
//
//        compiler.addComment("; -------------- instanceof : ");
//
//        compiler.addInstruction(new TSTO(1));
//        compiler.addInstruction(new BOV(new Label("pile_pleine")));
//        compiler.addInstruction(new PUSH(GPRegister.getR(2)));
//        //compiler.addInstruction(new ADDSP(1));
//
//        ClassDefinition def = ((ClassDefinition) compiler.environmentType.defOfType(this.expr.getType().getName()));
//        compiler.addInstruction(new LOAD(def.getDefinitionAdress(), GPRegister.R0));
//        compiler.addInstruction(new LOAD(this.type.getClassDefinition().getDefinitionAdress(), GPRegister.R1));
//
//        // test si expr null
//        compiler.addInstruction(new CMP(new NullOperand(), GPRegister.R0));
//        compiler.addInstruction(new BEQ(new Label("instanceof_false_"+cpt_instanceof)));
//
//        // boucle pour remonter l'arborescence de classe de expr
//        compiler.addLabel(new Label("instanceof_loop_" + cpt_instanceof));
//
//        // test l'égalité des instances
//        //compiler.addInstruction(new LOAD(new RegisterOffset(0, GPRegister.R0), GPRegister.getR(2)));
//        compiler.addInstruction(new CMP(GPRegister.R0, GPRegister.R1));
//        compiler.addInstruction(new BEQ(new Label("instanceof_true_"+cpt_instanceof)));
//
//        //charge super classe
//        compiler.addInstruction(new LEA(new RegisterOffset(0, GPRegister.R0), GPRegister.R0));
//
//        compiler.addInstruction(new CMP(new RegisterOffset(1, Register.GB), GPRegister.R0));
//        compiler.addInstruction(new BEQ(new Label("instanceof_false_"+cpt_instanceof)));
//        compiler.addInstruction(new CMP(GPRegister.R1, GPRegister.R0));
//        compiler.addInstruction(new BEQ(new Label("instanceof_true_"+cpt_instanceof)));
//        compiler.addInstruction(new BRA(new Label("instanceof_loop_"+cpt_instanceof)));
//
//        // si on est la : pas trouvé d'instance commune
//        compiler.addLabel(new Label("instanceof_false_"+cpt_instanceof));
//        compiler.addInstruction(new LOAD(0, GPRegister.R0));
//        compiler.addInstruction(new BRA(new Label("instanceof_end_"+cpt_instanceof)));
//
//        // si on est la : instanceof est vrai !!
//        compiler.addLabel(new Label("instanceof_true_"+cpt_instanceof));
//        compiler.addInstruction(new LOAD(1, GPRegister.R0));
//
//        // fin de instanceof
//        compiler.addLabel(new Label("instanceof_end_"+cpt_instanceof));
//        compiler.addInstruction(new POP(GPRegister.getR(2)));
//        //compiler.addInstruction(new SUBSP(1));
//
//        cpt_instanceof++;
//        return RegisterHandler.pushFromRegister(compiler, GPRegister.R0);
    }

}
