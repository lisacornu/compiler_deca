package fr.ensimag.deca.tree;
import java.util.ArrayList;
import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.util.HashMap;
/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    
    public Type verifyExpr_opti(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);   
        AbstractExpr rightExpDefinition = getRightOperand().verifyRValue(compiler, localEnv, currentClass, lefType);
        this.setType(lefType);
        if (lefType.isFloat() && rightExpDefinition.getType().isInt()){
            ConvFloat conversionFloat = new ConvFloat(rightExpDefinition);
            conversionFloat.verifyExpr(compiler, localEnv, currentClass);
            setRightOperand(conversionFloat);
        }else{
            setRightOperand(rightExpDefinition);
        }
        if(getRightOperand() instanceof Identifier){
            ((Identifier)getRightOperand()).usage(compiler);
            String varNameStr_r = ((Identifier) getRightOperand()).getName().toString();
            if(compiler.variablePropa.get(varNameStr_r)!=null){
                String varNameStr = ((Identifier) getLeftOperand()).getName().toString();
                ((Identifier)getLeftOperand()).literal=new IntLiteral(compiler.variablePropa.get(varNameStr_r));
                compiler.variablePropa.put(varNameStr,compiler.variablePropa.get(varNameStr_r));
            }
        }
        if (getLeftOperand() instanceof Identifier) {
            // Récupérer le nom de la variable
            String varNameStr = ((Identifier) getLeftOperand()).getName().toString();
            int usageCount = compiler.variableUsageCount.getOrDefault(varNameStr, 0);
            if (compiler.variableUsageCountdyna.containsKey(varNameStr)) {
                ArrayList<Integer> dynamicInfo = compiler.variableUsageCountdyna.get(varNameStr);
                dynamicInfo.add(0);
                dynamicInfo.set(dynamicInfo.size() - 1, usageCount > 0 ? 1 : 0);
           //     compiler.variableUsageCountdyna.put(varNameStr, dynamicInfo);
            }

            // Mettre à jour l'indice pour la variable
            int previousIndice = compiler.variableLast.getOrDefault(varNameStr, 0);
            int newIndice = previousIndice + 1;
            ((Identifier) getLeftOperand()).indice = newIndice;
            compiler.variableLast.put(varNameStr, newIndice);
            compiler.variableUsageCount.put(varNameStr, 0);
        }
        if(getRightOperand() instanceof IntLiteral){
            String varNameStr = ((Identifier) getLeftOperand()).getName().toString();
            ((Identifier)getLeftOperand()).literal=new IntLiteral(((IntLiteral)(getRightOperand())).getValue());
            compiler.variablePropa.put(varNameStr,((IntLiteral)(getRightOperand())).getValue());
        }
        else{
            String varNameStr = ((Identifier) getLeftOperand()).getName().toString();
            compiler.variablePropa.put(varNameStr,null);
        }
        
        // Incrémenter l'usage de la variable dans la table de hachage
       // String varNameStr = getName().toString();
       // compiler.variableUsageCount.put(varNameStr, compiler.variableUsageCount.get(varNameStr) + 1);  // Incrémenter l'usage
        return lefType;
        
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);   
        AbstractExpr rightExpDefinition = getRightOperand().verifyRValue(compiler, localEnv, currentClass, lefType);
        this.setType(lefType);
        if (lefType.isFloat() && rightExpDefinition.getType().isInt()){
            ConvFloat conversionFloat = new ConvFloat(rightExpDefinition);
            conversionFloat.verifyExpr(compiler, localEnv, currentClass);
            setRightOperand(conversionFloat);
        }else{
            setRightOperand(rightExpDefinition);
        }
        return lefType;
        
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
           // Vérifier l'utilisation de la variable dans la table de hachage
        String varNameStr = ((Identifier)getLeftOperand()).getName().getName();
        if (compiler.variableUsageCountdyna.containsKey(varNameStr)) {
            ArrayList<Integer> dynamicInfo = compiler.variableUsageCountdyna.get(varNameStr);

            // Vérifier le premier élément du tableau
            if (dynamicInfo.get(((Identifier)getLeftOperand()).indice) == 0  && compiler.opti==1) {
                compiler.addComment("Variable " + varNameStr + ((Identifier)getLeftOperand()).indice +" n'a pas besoin d'etre assigné");
                return null; // Ne pas générer la déclaration de la variable
            }
        }
        // Generation du codes des branches
        DVal leftOperandResult = getLeftOperand().codeGenExpr(compiler);
        DVal rightOperandResult;
        if(getRightOperand() instanceof Identifier){
                
              if(((Identifier)getLeftOperand()).literal!=null){
                compiler.addComment("jspquoidire");
                rightOperandResult =(( Identifier)getLeftOperand()).literal.codeGenExpr(compiler);
              }
              else{
                rightOperandResult = getRightOperand().codeGenExpr(compiler);
              }
        }
        else{
            rightOperandResult = getRightOperand().codeGenExpr(compiler);
        }
        // Selection des bonnes adresses en fonction de leur emplacement mémoire
        GPRegister op2 =  RegisterHandler.popIntoRegister(compiler, rightOperandResult, Register.R1);
        DVal op1 = RegisterHandler.popIntoDVal(compiler, leftOperandResult, Register.R0);

        // Generation du code de l'expression (résultat enregistré dans op1)
        codeGenBinaryExpr(compiler, op1, op2);
        compiler.registerHandler.SetFree(op2);

        //Renvoi du résultat (op1 est ne peut pas être un registre temporaire)
        return op1;
    }


    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        DAddr varAddress = ((AbstractIdentifier)getLeftOperand()).getExpDefinition().getOperand();
        compiler.addInstruction(new STORE(op2,varAddress));
    }
/*
    @Override 
    protected Type verifyExpr_ifthen(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass,HashMap<String, Integer> assignments) throws ContextualError {
                Type lefType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);   
                AbstractExpr rightExpDefinition = getRightOperand().verifyRValue(compiler, localEnv, currentClass, lefType);
                this.setType(lefType);
                if (lefType.isFloat() && rightExpDefinition.getType().isInt()){
                    ConvFloat conversionFloat = new ConvFloat(rightExpDefinition);
                    conversionFloat.verifyExpr(compiler, localEnv, currentClass);
                    setRightOperand(conversionFloat);
                }else{
                    setRightOperand(rightExpDefinition);
                }
                if(getRightOperand() instanceof Identifier){
                    ((Identifier)getRightOperand()).usage(compiler);
                }
                if (getLeftOperand() instanceof Identifier) {
                    // Récupérer le nom de la variable
                    String varNameStr = ((Identifier) getLeftOperand()).getName().toString();
                                // Récupérer ou initialiser l'utilisation de la variable
                    int usageCount_ifthen = assignments.getOrDefault(varNameStr, 0);
                    assignments.put(varNameStr, usageCount_ifthen + 1); // Incrémenter dans "assignments"
                    int usageCount = compiler.variableUsageCount.getOrDefault(varNameStr, 0);
                    if (compiler.variableUsageCountdyna.containsKey(varNameStr)) {
                        ArrayList<Integer> dynamicInfo = compiler.variableUsageCountdyna.get(varNameStr);
                        dynamicInfo.add(0);
                        dynamicInfo.set(dynamicInfo.size() - 1, usageCount > 0 ? 1 : 0);
                //     compiler.variableUsageCountdyna.put(varNameStr, dynamicInfo);
                    }

                    // Mettre à jour l'indice pour la variable
                    int previousIndice = compiler.variableLast.getOrDefault(varNameStr, 0);
                    int newIndice = previousIndice + 1;
                    ((Identifier) getLeftOperand()).indice = newIndice;
                    compiler.variableLast.put(varNameStr, newIndice);
                    compiler.variableUsageCount.put(varNameStr, 0);
                }

                // Incrémenter l'usage de la variable dans la table de hachage
            // String varNameStr = getName().toString();
            // compiler.variableUsageCount.put(varNameStr, compiler.variableUsageCount.get(varNameStr) + 1);  // Incrémenter l'usage
                return lefType;
        }*/



}
