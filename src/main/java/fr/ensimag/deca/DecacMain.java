package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl31
 * @date 01/01/2025
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();

        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }

        if (options.getPrintBanner()) options.displayBanner();

        if (options.getSourceFiles().isEmpty()) options.displayUsage();

        // Compilation parallèle
        if (options.getParallel()) {
            // Crée l'ensemble de thread chargé d'executer la compilation parallèle des fichiers source
            int nbProc = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(nbProc);

            // Liste des résultats futurs de l'éxecution de compile() dans chaque thread
            List<Future<Boolean>> futures = new ArrayList<>();
            // instancie un DecacCompiler par fichier et lance la compilation de ce fichier dans un thread
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                futures.add(executor.submit(compiler));
            }

            // Attend la fin de chaque compilation et vérifie son résultat
            for (Future<Boolean> future : futures) {
                try {
                    if (future.get()) System.out.println("Erreur lors de la compilation parallèle d'un des fichiers");
                } catch (Exception e) {
                    error = true;
                }
            }
        } else {    // Compilation séquentielle
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
