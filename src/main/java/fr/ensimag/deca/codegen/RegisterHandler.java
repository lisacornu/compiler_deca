package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

import java.util.ArrayList;

public class RegisterHandler {

    private boolean[] freeRegisters;
    private int nbRegisterAvailable;

    public RegisterHandler(int NbRegisterAvailable) {
        freeRegisters = new boolean[NbRegisterAvailable];
        this.nbRegisterAvailable = NbRegisterAvailable;
        for (int i = 0; i < NbRegisterAvailable; i++) freeRegisters[i] = true;
    }

    private int GetFirstFreeRegisterIndex (int from) {
        for (int i = from; i < freeRegisters.length; i++)
            if (freeRegisters[i]) return i;

        return -1; //Pas de registres dispos
    }

    public GPRegister Get() {

        //Registre
        int firstFreeRegisterIndex = GetFirstFreeRegisterIndex(2);

        if (firstFreeRegisterIndex != -1) {
            GPRegister firstFreeRegister = Register.getR(firstFreeRegisterIndex);
            freeRegisters[firstFreeRegisterIndex] = false;
            return firstFreeRegister;
        }

        //SI aucun registre n'est disponible, on return null;
        return null;
    }

    public void SetFree(DVal addr) {
        if (!(addr instanceof GPRegister)) return;
        int index = ((GPRegister)addr).getNumber();
        if (index == -1) return;
        freeRegisters[index] = true;
    }


    // Appellé au début d'une méthode pour sauvegarder tout les registres occupé
    // Renvoi tout les registres occupés
    public ArrayList<GPRegister> saveFullRegs (DecacCompiler compiler) {

        ArrayList<GPRegister> savedRegs = new ArrayList<>();

        for (int i=2; i < this.nbRegisterAvailable; i++) {

            if (this.freeRegisters[i]) continue;
            savedRegs.add(GPRegister.getR(i));

            compiler.addInstruction(new PUSH(GPRegister.getR(i)));
            this.freeRegisters[i] = true;
        }
        return savedRegs;
    }

    // Restaure l'état des registres en arguments à la fin de l'éxecution d'une méthode
    public void restoreRegs (DecacCompiler compiler, ArrayList<GPRegister> savedRegs) {
        for(GPRegister reg : savedRegs) {

            compiler.addInstruction(new POP(reg));
            this.freeRegisters[reg.getNumber()] = false;
        }
    }


    // Vérifie que addr (renvoyé par un codegen) n'est pas dans la pile
    // sinon pop le résultat dans un registre temporaire
    // Renvoi l'adresse/le registre concerné
    public static DVal popIntoDVal(DecacCompiler compiler, DVal addr, GPRegister tempRegister) {

        if (addr != null) return addr;
        //Si la pile est pleine (= addr est null) on pop dans un registre temporaire
        compiler.addInstruction(new POP(tempRegister));
        return tempRegister;
    }

    // Vérifie que addr n'est pas dans la pile et le place dans un registre si nécessaire
    // sinon pop le résultat dans un registre temporaire
    // Renvoi le registre concerné
    public static GPRegister popIntoRegister(DecacCompiler compiler, DVal addr, GPRegister tempRegister) {

        if (addr == null) { // addr est dans la pile
            compiler.addInstruction(new POP(tempRegister));
            return tempRegister;
        }

        if (addr instanceof GPRegister) // addr est dans un registre
            return (GPRegister)addr;

        //addr est dans GB
        compiler.addInstruction(new LOAD(addr, tempRegister));
        return tempRegister;

    }

    // Vérifie si reg n'est pas temporaire
    // Sinon place le résultat dans un registre ou le push dans la pile
    // Renvoi le registre concerné (ou null si le résultat est push dans la pile)
    public static GPRegister pushFromRegister(DecacCompiler compiler, GPRegister reg) {

        if (reg.getNumber() == 0 || reg.getNumber() == 1) { // reg est temporaire
            GPRegister saveReg = compiler.registerHandler.Get();

            if (saveReg == null) { // Les registres sont pleins
                compiler.addInstruction(new PUSH(reg));
                return null;
            }
            compiler.addInstruction(new LOAD(reg, saveReg)); //Un registre est disponible
            return saveReg;
        }
        return reg; //reg n'est pas temporaire
    }


    //Cherche un registre pour stocker addr
    //Si aucun registre n'est disponible, stock addr dans tempRegister, et push le résultat dans la pile
    // Renvoi le registre concerné (ou null si le résultat est push dans la pile)
    public static GPRegister pushFromDVal(DecacCompiler compiler, DVal addr, GPRegister tempRegister) {

        GPRegister saveReg = compiler.registerHandler.Get();

        if (saveReg == null) { // Les registres sont pleins
            compiler.addInstruction(new LOAD(addr, tempRegister));
            compiler.addInstruction(new PUSH(tempRegister));
            return null;
        }
        compiler.addInstruction(new LOAD(addr, saveReg)); //Un registre est disponible
        return saveReg;
    }






}
