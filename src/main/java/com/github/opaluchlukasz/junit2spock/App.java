package com.github.opaluchlukasz.junit2spock;

import com.github.opaluchlukasz.junit2spock.core.Spocker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.find;
import static java.nio.file.Files.readAllLines;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String... args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Source and output directory should be passed as arguments");
        }
        List<Path> paths = find(Paths.get(args[0]),
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.getFileName().toString().matches(".*\\.java"))
                .collect(toList());

        Spocker spocker = new Spocker();

        paths.stream().map(path -> parse(spocker, path)).filter(Optional::isPresent).map(Optional::get).forEach(pair -> save(pair, args[1]));
    }

    private static void save(Pair<Path, String> toBeSaved, String outputPath) {
        try {
            File outputFile = new File(format("%s/%s.groovy", outputPath, toBeSaved.getLeft().getFileName().toString().split(Pattern.quote("."))[0]));
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            FileUtils.writeStringToFile(outputFile, toBeSaved.getRight(), UTF_8);
        } catch (IOException ex) {
            LOG.error("Unable to save output file", ex);
        }
    }

    private static Optional<Pair<Path, String>> parse(Spocker spocker, Path path) {
        try {
            return Optional.of(Pair.of(path, spocker.parse(readAllLines(path, UTF_8).stream().collect(joining(SEPARATOR)))));
        } catch (IOException ex) {
            LOG.error("Unable to read path", ex);
        } catch (Exception ex) {
            LOG.error("Unable to parse parse", ex);
        }
        return Optional.empty();
    }
}
