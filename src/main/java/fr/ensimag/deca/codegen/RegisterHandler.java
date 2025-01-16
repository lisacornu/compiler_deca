package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

public class RegisterHandler {

    private boolean[] freeRegisters;
    // copie de freeRegister pour pouvoir retrouver quels registres étaient utilisé avant l'appel en resortant d'une méthode
    // vaut null si on est pas en cours de génération de code d'une méthode
    private boolean[] savedStateOfFreeRegisters = null;
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


    // Appellée au début d'une méthode pour sauvegarder l'état de tout les registres
    // A optimiser plus tard pour ne sauvegarder que les registres utiles à la méthode
    public void saveAllReg (DecacCompiler compiler) {
        this.savedStateOfFreeRegisters = this.freeRegisters;
        for (int i=2; i < this.nbRegisterAvailable; i++) {
            compiler.addInstruction(new PUSH(GPRegister.getR(i)));
            this.freeRegisters[i] = true;
        }
    }

    // Restaure l'état des registres à la fin de l'éxecution d'une méthode
    public void restoreAllReg (DecacCompiler compiler) {
        this.freeRegisters = this.savedStateOfFreeRegisters;
        for (int i=nbRegisterAvailable; i >= 2; i--) {
            compiler.addInstruction(new POP(GPRegister.getR(i)));
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
