package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import java.util.ArrayList;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Single precision, this
 *
 * @author Fabien Galzi
 * @date 15/01/2025
 */
public class New extends AbstractExpr {

    private AbstractIdentifier type;
    public New(AbstractIdentifier type) {
        this.type = type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        if(!type.verifyType(compiler).isClass()){
            throw new ContextualError("Type is not a class type", getLocation());
        }
        setType(currentClass.getType());
        return getType();
    }

    @Override
    public void printExprValue(DecacCompiler compiler) {
        // TODO
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new");
        type.decompile(s);
        s.print("()");
    }


    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, true);
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
        int structSize = type.getClassDefinition().getNumberOfFields() + 1;

        GPRegister structHeapAddr = GPRegister.R0;
        GPRegister VTableAddr = GPRegister.R1;

        compiler.addInstruction(new NEW(new ImmediateInteger(structSize), structHeapAddr));
        compiler.addInstruction(new BOV(new Label("tas_plein")));
        compiler.addInstruction(new LEA(type.getClassDefinition().getDefinitionAdress(), VTableAddr)); //l'addresse de la table des methodes

        RegisterOffset structFirstWord = new RegisterOffset(0, structHeapAddr);
        compiler.addInstruction(new STORE(VTableAddr, structFirstWord)); //On stock l'adresse de la Vtable dans le premier mots du tas de l'obj crée

        GPRegister structHeapAddrReg =  RegisterHandler.pushFromRegister(compiler, structHeapAddr);

        ArrayList<GPRegister> savedRegs = compiler.registerHandler.saveFullRegs(compiler);
        compiler.addInstruction(new BSR(new Label("init."+type.getName().getName())));
        compiler.registerHandler.restoreRegs(compiler, savedRegs);

        return structHeapAddrReg;
    }

}
