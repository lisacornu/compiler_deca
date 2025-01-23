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
import java.util.HashMap;
import static org.mockito.ArgumentMatchers.isNotNull;

import java.io.PrintStream;
import java.util.ArrayList;

import fr.ensimag.ima.pseudocode.*;
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
    protected void verifyDeclVar_opti(DecacCompiler compiler,
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
            compiler.varaible_current=(Identifier)varName;
            compiler.variableUsageCount.putIfAbsent(varNameStr, 0); // Initialiser si nécessaire
            compiler.variableLast.putIfAbsent(varNameStr,0);
            ArrayList<Integer> dynamicInfo = new ArrayList<>();
            dynamicInfo.add(0);
            dynamicInfo.set(0, 0);
            ((Identifier)varName).indice=0;
            compiler.variableUsageCountdyna.putIfAbsent(varNameStr, dynamicInfo);
            compiler.variablePropa.putIfAbsent(varNameStr,null);
            compiler.variablePropa_float.putIfAbsent(varNameStr,null);
        }catch(DoubleDefException e){
            throw new ContextualError("The type as already been define for the variable " + varName.getName(), getLocation());
        }
        
        initialization.verifyInitialization_opti(compiler, typeType, localEnv, currentClass);
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Type typeType = type.verifyType(compiler);

        
        type.setType(typeType);
        try{

            varName.setDefinition(new VariableDefinition(typeType, getLocation()));
            varName.setType(typeType);
            type.setDefinition(varName.getDefinition());

            localEnv.declare(varName.getName(),(ExpDefinition) varName.getExpDefinition());

        }catch(DoubleDefException e){
            throw new ContextualError("The type as already been defined for the variable " + varName.getName(), getLocation());
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
        s.println();
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
    protected void codeGenDeclVar_opti(DecacCompiler compiler) {
           // Vérifier l'utilisation de la variable dans la table de hachage
        String varNameStr = varName.getName().getName();
        //Ajout de l'operand à GB
        RegisterOffset GB_Stack = new RegisterOffset(compiler.headOfGBStack, Register.GB);
        varName.getExpDefinition().setOperand(GB_Stack);
        compiler.headOfGBStack++;
        compiler.stackUsageWatcher.nbVariables++;

        if (initialization instanceof NoInitialization) return;
        Initialization initExpression = (Initialization) initialization;
        if (compiler.variableUsageCountdyna.containsKey(varNameStr)) {
            ArrayList<Integer> dynamicInfo = compiler.variableUsageCountdyna.get(varNameStr);

            // Vérifier le premier élément du tableau ( si la variable est utilisé au moins une fois
            if (dynamicInfo.get(((Identifier)varName).indice) == 0) {
                compiler.addComment("Variable " + varNameStr + " n'a pas besoin de  l'initialisation, pas de code généré.");
                return; // Ne pas générer la déclaration de la variable
            }
        }
        DVal addrInit = initExpression.codeGenInit_opti(compiler);
        GPRegister regInit = RegisterHandler.popIntoRegister(compiler, addrInit, Register.R0);

        compiler.addInstruction(new STORE(regInit, GB_Stack));
        compiler.registerHandler.SetFree(regInit);
    }

       @Override
    protected void codeGenDeclVar(DecacCompiler compiler) {
           // Vérifier l'utilisation de la variable dans la table de hachage
        String varNameStr = varName.getName().getName();
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
        //Ajout de l'operand à LB
        RegisterOffset LB_Stack = new RegisterOffset(compiler.headOfGBStack, Register.GB);
        varName.getExpDefinition().setOperand(LB_Stack);
        compiler.headOfGBStack++;
        compiler.stackUsageWatcher.nbVariables++;

        if (initialization instanceof NoInitialization) return;
        Initialization initExpression = (Initialization) initialization;

        DVal addrInit = initExpression.codeGenInit(compiler);
        GPRegister regInit = RegisterHandler.popIntoRegister(compiler, addrInit, Register.R0);

        compiler.addInstruction(new STORE(regInit, LB_Stack));
        compiler.registerHandler.SetFree(regInit);
    }
}
