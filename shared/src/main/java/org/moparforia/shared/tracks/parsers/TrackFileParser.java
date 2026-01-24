package org.moparforia.shared.tracks.parsers;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import org.moparforia.shared.tracks.Track;
import org.moparforia.shared.tracks.TrackCategory;
import org.moparforia.shared.tracks.filesystem.FileSystemTrackStats;
import org.moparforia.shared.tracks.filesystem.lineparser.*;
import org.moparforia.shared.tracks.stats.TrackStats;

/**
 * Class for parsing Track V1 file in format like this. Allows for parsing of similar files or
 * adding custom properties.
 *
 * @deprecated "TrackFileParser can only parser V1 tracks without category, use
 *     VersionedTrackFileParser for V2+" V 1 A {AUTHOR OF TRACK} N {NAME OF TRACK} T data I {NUMBER
 *     OF PLAYERS TO COMPLETE},{NUMBER OF STROKES},{BEST NUMBER OF STROKES},{NUMBER OF PEOPLE THAT
 *     GOT BEST STROKE} B {FIRST BEST PAR PLAYER},{UNIX TIMESTAMP OF FIRST BEST PAR}000 L {LAST BEST
 *     PAR PLAYER},{UNIX TIMESTAMP OF LAST BEST PAR}000 R {RATING: 0},{RATING: 1},{RATING:
 *     2},{RATING: 3},{RATING: 4},{RATING: 5},{RATING: 6},{RATING: 7},{RATING: 8},{RATING:
 *     9},{RATING: 10}
 */
@Deprecated
public class TrackFileParser extends GenericTrackParser implements TrackParser {

    private static final Map<Character, LineParser> BASE_PARSERS;
    private static final Map<Character, LineParser> STATS_PARSERS;

    private final Set<TrackCategory> categories;

    // Initialize parser with all LineParsers
    static {
        HashMap<Character, LineParser> tmp_map = new HashMap<>();
        tmp_map.put('A', new SimpleLineParser("author"));
        tmp_map.put('N', new SimpleLineParser("name"));
        tmp_map.put('T', new SimpleLineParser("data"));
        BASE_PARSERS = Collections.unmodifiableMap(tmp_map);
        tmp_map.put('R', new RatingsLineParser());
        tmp_map.put('I', new ScoreInfoLineParser());
        tmp_map.put('B', new BestTimeLineParser("bestTime", "bestPlayer"));
        //      Uncomment if you want to also parse lastTime and lastPlayer
        // tmp_map.put("L", new BestTimeLineParser("lastTime", "lastPlayer"));
        STATS_PARSERS = Collections.unmodifiableMap(tmp_map);
    }

    public TrackFileParser(TrackCategory category) {
        this.categories = Collections.singleton(category);
    }

    public Track parseTrack(Path path) throws IOException {
        Map<String, Object> parsed = parse(BASE_PARSERS, path);
        return constructTrack(parsed);
    }

    private Track constructTrack(Map<String, Object> parsed) {
        String name = (String) parsed.get("name");
        String author = (String) parsed.get("author");
        String data = (String) parsed.get("data");
        return new Track(name, author, data, categories);
    }

    // NOTE! Does not parse lastBestPlayer or lastBestTime, return just bestPlayer
    // and bestTime instead.
    public TrackStats parseStats(Path path) throws IOException {
        Map<String, Object> parsed = parse(STATS_PARSERS, path);
        Track track = constructTrack(parsed);
        int[] ratings = (int[]) parsed.getOrDefault("ratings", new int[10]);
        int attempts = (int) parsed.getOrDefault("attempts", 0);
        int strokes = (int) parsed.getOrDefault("strokes", 0);
        int bestPar = (int) parsed.getOrDefault("bestPar", -1);

        // Widen int primitive type, to support integer division resulting in double
        int numberOfBestPar = (Integer) parsed.getOrDefault("numberOfBestPar", 0);
        LocalDate bestTime = (LocalDate) parsed.getOrDefault("bestTime", LocalDate.now());
        String bestPlayer = (String) parsed.getOrDefault("bestPlayer", "");

        double bestParPercentage = (double) numberOfBestPar / attempts;

        return new FileSystemTrackStats(
                attempts,
                strokes,
                bestPar,
                bestParPercentage,
                numberOfBestPar,
                bestPlayer,
                bestTime,
                bestPlayer,
                bestTime,
                ratings,
                track);
    }
}
