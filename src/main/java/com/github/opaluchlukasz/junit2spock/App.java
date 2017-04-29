package com.github.opaluchlukasz.junit2spock;

import com.github.opaluchlukasz.junit2spock.core.Spocker;
import com.github.opaluchlukasz.junit2spock.core.model.TypeModel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.find;
import static java.nio.file.Files.readAllLines;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public final class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private App() {
        //NOOP
    }

    public static void main(String... args) throws IOException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.scan(App.class.getPackage().getName());
        applicationContext.refresh();
        Spocker spocker = applicationContext.getBean(Spocker.class);

        if (args.length != 2) {
            throw new IllegalArgumentException("Source and output directory should be passed as arguments");
        }
        List<Path> paths = find(Paths.get(args[0]),
                MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.getFileName().toString().matches(".*\\.java"))
                .collect(toList());

        paths.stream().map(App::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(spocker::toGroovyTypeModel)
                .forEach(typeModel -> save(typeModel, args[1]));
    }

    private static void save(TypeModel typeModel, String outputPath) {
        try {
            File outputFile = new File(format("%s/%s", outputPath, typeModel.outputFilePath()));
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            FileUtils.writeStringToFile(outputFile, typeModel.asGroovyClass(0), UTF_8);
        } catch (IOException ex) {
            LOG.error(format("Unable to save output file: %s", typeModel.outputFilePath()), ex);
        }
    }

    private static Optional<String> parse(Path path) {
        try {
            return Optional.of(readAllLines(path, UTF_8).stream().collect(joining(SEPARATOR)));
        } catch (IOException ex) {
            LOG.error(format("Unable to read path: %s", path.toString()), ex);
        } catch (Exception ex) {
            LOG.error(format("Unable to parse file: %s", path.toString()), ex);
        }
        return Optional.empty();
    }
}
