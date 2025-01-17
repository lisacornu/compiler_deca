package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterHandler;
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
import java.util.ArrayList;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

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
            // Ajouter le suivi de l'utilisation de la variable dans la table de hachage
            String varNameStr = varName.getName().getName(); // Récupérer le nom de la variable
            compiler.variableUsageCount.putIfAbsent(varNameStr, 0); // Initialiser si nécessaire

        }catch(DoubleDefException e){
            throw new ContextualError("The type as already been define for the variable " + varName.getName(), getLocation());
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
            // Vérifier l'utilisation de la variable dans la table de hachage
        String varNameStr = varName.getName().getName();
        int usageCount = compiler.variableUsageCount.getOrDefault(varNameStr, 0); // Récupérer le compteur d'usage de la variable

        // Si la variable n'a pas été utilisée, ne pas générer de code
        if (usageCount == 0) {
            compiler.addComment("Variable " + varNameStr + " non utilisée, pas de code généré.");
            return;  // Ne pas générer la déclaration de la variable
        }
        //Ajout de l'operand à GB
        RegisterOffset GB_Stack = new RegisterOffset(compiler.headOfGBStack, Register.GB);
        varName.getExpDefinition().setOperand(GB_Stack);
        compiler.headOfGBStack++;
        compiler.stackUsageWatcher.nbVariables++;

        if (initialization instanceof NoInitialization) return;
        Initialization initExpression = (Initialization) initialization;

        DVal addrInit = initExpression.codeGenInit(compiler);
        GPRegister regInit = RegisterHandler.popIntoRegister(compiler, addrInit, Register.R0);

        compiler.addInstruction(new STORE(regInit, GB_Stack));
        compiler.registerHandler.SetFree(regInit);
    }

    @Override
    protected void codeGenDeclVarMethod(DecacCompiler compiler) {
        //Ajout de l'operand à GB
        RegisterOffset GB_Stack = new RegisterOffset(compiler.headOfGBStack, Register.GB);
        varName.getExpDefinition().setOperand(GB_Stack);
        compiler.headOfLBStack++;
        compiler.stackUsageWatcher.nbVariables++;

        if (initialization instanceof NoInitialization) return;
        Initialization initExpression = (Initialization) initialization;

        DVal addrInit = initExpression.codeGenInit(compiler);
        GPRegister regInit = RegisterHandler.popIntoRegister(compiler, addrInit, Register.R0);

        compiler.addInstruction(new STORE(regInit, GB_Stack));
        compiler.registerHandler.SetFree(regInit);
    }

  /*   @Override
    protected void codeGenDeclVar(DecacCompiler compiler) {
        // Assigner un registre unique à chaque variable déclarée
        GPRegister newRegister = compiler.registerHandler.Get();
        if (newRegister == null) {
            throw new RuntimeException("No free registers available for SSA");
        }
        varName.setRegistre_ssa(newRegister);

        if (!(initialization instanceof NoInitialization)) {
            DVal addrInit = initialization.codeGenInit(compiler);
            compiler.addInstruction(new LOAD(addrInit, newRegister));
        }
    }*/

}
