package org.moparforia.shared.tracks.parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class for parsing files in format of
 *
 * <p><Character> <String> <Character> <String> ... e.g. V 1 T tzetete
 */
public class GenericTrackParser {

    public Map<String, Object> parse(Map<Character, LineParser> parser, Path path) throws IOException {
        return Files.lines(path)
                .map(line -> parseLine(parser, line))
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> parseFromString(Map<Character, LineParser> parser, String data) throws IOException {
        return data.lines()
                .map(line -> parseLine(parser, line))
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Object> parseLine(Map<Character, LineParser> parsers, String line) {
        Character character = line.charAt(0);
        if (parsers.containsKey(character)) {
            return parsers.get(character).apply(line.substring(2));
        }
        return Collections.emptyMap();
    }
}
