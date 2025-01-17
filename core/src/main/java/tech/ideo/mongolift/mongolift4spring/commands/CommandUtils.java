package tech.ideo.mongolift.mongolift4spring.commands;

import java.nio.file.Path;
import java.util.function.Function;

public class CommandUtils {

    static String resolveCollectionName(Path path) {
        return path.getFileName().toString().transform(withoutExtension());
    }

    static Function<String, String> withoutExtension() {
        return fileName -> fileName.contains(".")
            ? fileName.substring(0, fileName.lastIndexOf('.'))
            : fileName;
    }
}