package org.nlpa.iu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;
import org.bdp4j.util.InstanceListUtils;
import org.nlpa.Main;
import org.nlpa.pipe.impl.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AppCore {

    private static final Logger logger = LogManager.getLogger(Main.class);
    //Lista de instancias que se están procesando
    private static List<Instance> instances = new ArrayList<>();

    //Declaración estática del AbstractPipe que contendrá todos los Pipes que se van a ejecutar
    static AbstractPipe p = new SerialPipes(new AbstractPipe[]{
            new TargetAssigningFromPathPipe(),
            new StoreFileExtensionPipe(),
            new GuessDateFromFilePipe(),
            new File2StringBufferPipe(),
            new StripHTMLFromStringBufferPipe(),
            new GuessLanguageFromStringBufferPipe(),
            new NewNERFromStringBufferPipe(GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY,true, true, true, true),
    });

    //Comprueba que las dependencias se cumplen, sino sale del programa
    private static void checkDependencies() {
        if (!p.checkDependencies()) {
            System.out.println("Pipe dependencies are not satisfied");
            System.exit(1);
        } else {
            System.out.println("Pipe dependencies are satisfied");
        }
    }

    //Ejecuta los pipes del AbstractPipe creando una instancia nueva con el StringBuffer que se le pasa por parámetro y
    //devolviendo una colección de instancias
    public static Collection<Instance> findEntitiesInString(StringBuffer str) {
        checkDependencies();
        List<Instance> newInstances = new ArrayList<Instance>();
        Instance ins = new Instance(str, "entity", "NER", str);
        newInstances.add(ins);

        //Crea el directorio output en el caso de que no exista
        File outputDirectory = new File("./output");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }
        return p.pipeAll(newInstances);
    }

    //Ejecuta los pipes del AbstractPipe creando una nueva lista de instancias válidas que se han generado y
    //devolviendo una colección de instancias
    public static Collection<Instance> findEntitiesInFiles() {
        checkDependencies();
        List<Instance> newInstances = new ArrayList<>(InstanceListUtils.dropInvalid(instances));
        instances.clear();

        //Crea el directorio output en el caso de que no exista
        File outputDirectory = new File("./output");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }

        return p.pipeAll(newInstances);
    }

    //Genera instancias a partir de los archivos del directorio que se le pasa por parámetro
    public static void generateInstances(String testDir) {
        try {
            Files.walk(Paths.get(testDir))
                    .filter(Files::isRegularFile)
                    .forEach(FileMng::visit);
        } catch (IOException e) {
            logger.error("IOException found " + e.getMessage());
            System.exit(0);
        }
    }

    //Añade una nueva instancia a la lista de instancias cuando un nuevo archivo es detectado
    static class FileMng {

        static void visit(Path path) {
            File data = path.toFile();
            String target = null;
            String name = data.getPath();
            File source = data;

            instances.add(new Instance(data, target, name, source));
        }
    }
}
