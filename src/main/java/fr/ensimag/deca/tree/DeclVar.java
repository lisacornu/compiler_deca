package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.VariableDefinition;

import static org.mockito.ArgumentMatchers.isNotNull;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

/**
 * @author gl31
 * @date 01/01/2025
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Type typeType = type.verifyType(compiler);
        
        if (typeType.isVoid()){
            throw new ContextualError("type is void", getLocation());
        }
        type.setType(typeType);
        try{

            varName.setDefinition(new VariableDefinition(typeType, getLocation()));;
            localEnv.declare(varName.getName(),(ExpDefinition) varName.getExpDefinition());

        }catch(DoubleDefException e){
            throw new ContextualError("The type as already been define " + varName, getLocation());
        }
        
        initialization.verifyInitialization(compiler, typeType, localEnv, currentClass);
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.print(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenDeclVar(DecacCompiler compiler) {

        //Ajout de l'operand à GB
        RegisterOffset GB_Stack = new RegisterOffset(compiler.headOfGBStack, Register.GB);
        varName.getExpDefinition().setOperand(GB_Stack);
        compiler.headOfGBStack++;

        if (initialization instanceof NoInitialization) return;

        Initialization initExpression = (Initialization) initialization;

        DVal addrInit = initExpression.codeGenInit(compiler);
        GPRegister regInit;

        //Renvoi du résultat
        if (addrInit == null) { //Dans la pile si les registres sont plein
            compiler.addInstruction(new POP(Register.R0));
            regInit = Register.R0;
        } else {
            regInit = (GPRegister) addrInit;
        }

        //STORE R2 k(GB)
        compiler.addInstruction(new STORE(regInit, GB_Stack));

        compiler.registerHandler.SetFree(regInit);
        compiler.headOfGBStack++;


    }
}
