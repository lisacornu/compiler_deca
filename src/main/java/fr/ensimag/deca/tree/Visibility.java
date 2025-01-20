package fr.ensimag.deca.tree;

/**
 * Visibility of a field.
 *
 * @author gl31
 * @date 01/01/2025
 */

public enum Visibility {
    PUBLIC,
    PROTECTED;


    @Override
    public String toString() {
        if (this == PUBLIC){
            return "public";
        }

        return "protected";
    }
}
