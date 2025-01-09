package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

public class RegisterHandler {

    private boolean[] freeRegisters;

    public RegisterHandler(int NbRegisterAvailable) {
        freeRegisters = new boolean[NbRegisterAvailable];
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
        int index = ((GPRegister)addr).getIndex();
        if (index == -1) return;
        freeRegisters[index] = true;
    }


}
