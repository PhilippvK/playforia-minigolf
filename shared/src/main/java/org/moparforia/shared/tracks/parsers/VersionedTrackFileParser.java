package org.moparforia.shared.tracks.parsers;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.moparforia.shared.tracks.Track;
import org.moparforia.shared.tracks.TrackCategory;
import org.moparforia.shared.tracks.filesystem.FileSystemTrackStats;
import org.moparforia.shared.tracks.filesystem.lineparser.*;
import org.moparforia.shared.tracks.stats.TrackStats;

/**
 * Class for parsing Track V2 file which has this format. It differs from V1 file format in that it
 * include list of categories There is intentional code duplication from TrackFileParser because I
 * want to make them independent of each other as TrackFileParser is deprecated
 *
 * <p>V >=2 A {AUTHOR OF TRACK} N {NAME OF TRACK} T data C {CategoryId}, {CategoryId}, ... I {NUMBER
 * OF PLAYERS TO COMPLETE},{NUMBER OF STROKES},{BEST NUMBER OF STROKES},{NUMBER OF PEOPLE THAT GOT
 * BEST STROKE} B {FIRST BEST PAR PLAYER},{UNIX TIMESTAMP OF FIRST BEST PAR}000 L {LAST BEST PAR
 * PLAYER},{UNIX TIMESTAMP OF LAST BEST PAR}000 R {RATING: 0},{RATING: 1},{RATING: 2},{RATING:
 * 3},{RATING: 4},{RATING: 5},{RATING: 6},{RATING: 7},{RATING: 8},{RATING: 9},{RATING: 10}
 */
public class VersionedTrackFileParser extends GenericTrackParser implements TrackParser {
    public static final int DEFAULT_ALLOWED_FILE_VERSION = 2;
    protected static final Map<Character, LineParser> BASE_PARSERS;
    protected static final Map<Character, LineParser> STATS_PARSERS;

    // Initialize parser with all LineParsers
    static {
        HashMap<Character, LineParser> tmp_map = new HashMap<>();
        tmp_map.put('V', new SingleArgumentLineParser<>("version", Integer::parseInt));
        tmp_map.put('A', new SimpleLineParser("author"));
        tmp_map.put('N', new SimpleLineParser("name"));
        tmp_map.put('T', new SimpleLineParser("data"));
        tmp_map.put('C', new CategoriesLineParser());
        tmp_map.put('S', new SimpleLineParser("settings"));
        BASE_PARSERS = Collections.unmodifiableMap(tmp_map);
        tmp_map.put('R', new RatingsLineParser());
        tmp_map.put('I', new ScoreInfoLineParser());
        tmp_map.put('B', new BestTimeLineParser("bestTime", "bestPlayer"));
        //      Uncomment if you want to also parse lastTime and lastPlayer
        tmp_map.put('L', new BestTimeLineParser("lastTime", "lastPlayer"));
        STATS_PARSERS = Collections.unmodifiableMap(tmp_map);
    }

    private final int allowed_version;

    public VersionedTrackFileParser(int allowed_version) {
        this.allowed_version = allowed_version;
    }

    public VersionedTrackFileParser() {
        this(DEFAULT_ALLOWED_FILE_VERSION);
    }

    public Track parseTrack(Path path) throws IOException {
        Map<String, Object> parsed = parse(BASE_PARSERS, path);
        int version = (int) parsed.getOrDefault("version", 0);
        if (version < allowed_version) {
            throw new InvalidTrackVersion("Track in file "
                    + path
                    + " has unsupported version "
                    + version
                    + ", while this parser requires "
                    + allowed_version);
        }
        return constructTrack(parsed);
    }

    private Track constructTrack(Map<String, Object> parsed) {
        String name = (String) parsed.get("name");
        String author = (String) parsed.get("author");
        String data = (String) parsed.get("data");
        Set<TrackCategory> categories = (Set<TrackCategory>) parsed.get("categories");
        String settings = (String) parsed.get("settings");
        boolean[] trackSpecialSettings = new boolean[4];
        if (settings != null && settings.length() != 6) {
            for (int i = 0; i < 4; i++) {
                trackSpecialSettings[i] = settings.charAt(i) == 't';
            }
        } else {
            // should throw error
        }
        return new Track(name, author, data, categories, trackSpecialSettings, settings);
    }

    @Override
    public TrackStats parseStats(Path path) throws IOException {
        Map<String, Object> parsed = parse(STATS_PARSERS, path);
        int version = (int) parsed.getOrDefault("version", 0);
        if (version < allowed_version) {
            throw new InvalidTrackVersion("Track in file "
                    + path
                    + " has unsupported version "
                    + version
                    + ", while this parser requires "
                    + allowed_version);
        }
        Track track = constructTrack(parsed);
        int[] ratings = (int[]) parsed.getOrDefault("ratings", new int[10]);
        int attempts = (int) parsed.getOrDefault("attempts", 0);
        int strokes = (int) parsed.getOrDefault("strokes", 0);
        int bestPar = (int) parsed.getOrDefault("bestPar", -1);
        int numberOfBestPar = (Integer) parsed.getOrDefault("numberOfBestPar", 0);

        // Widen int primitive type, to support integer division resulting in double
        LocalDate bestTime = (LocalDate) parsed.getOrDefault("bestTime", LocalDate.now());
        String bestPlayer = (String) parsed.getOrDefault("bestPlayer", "");
        LocalDate lastBestTime = (LocalDate) parsed.getOrDefault("lastBestTime", LocalDate.now());
        String lastBestPlayer = (String) parsed.getOrDefault("lastBestPlayer", "");

        double bestParPercentage = (double) numberOfBestPar / attempts;

        return new FileSystemTrackStats(
                attempts,
                strokes,
                bestPar,
                bestParPercentage,
                numberOfBestPar,
                bestPlayer,
                bestTime,
                lastBestPlayer,
                lastBestTime,
                ratings,
                track);
    }

    public Track parseTrackFromString(String data) throws IOException {
        Map<String, Object> parsed = parseFromString(BASE_PARSERS, data);
        int version = (int) parsed.getOrDefault("version", 0);
        if (version < allowed_version) {
            throw new InvalidTrackVersion("Given track has unsupported version "
                    + version
                    + ", while this parser requires "
                    + allowed_version);
        }
        return constructTrack(parsed);
    }

    public TrackStats parseStatsFromString(String data) throws IOException {
        Map<String, Object> parsed = parseFromString(STATS_PARSERS, data);
        int version = (int) parsed.getOrDefault("version", 0);
        if (version < allowed_version) {
            throw new InvalidTrackVersion("Given track has unsupported version "
                    + version
                    + ", while this parser requires "
                    + allowed_version);
        }
        Track track = constructTrack(parsed);
        int[] ratings = (int[]) parsed.getOrDefault("ratings", new int[10]);
        int attempts = (int) parsed.getOrDefault("attempts", 0);
        int strokes = (int) parsed.getOrDefault("strokes", 0);
        int bestPar = (int) parsed.getOrDefault("bestPar", -1);
        int numberOfBestPar = (Integer) parsed.getOrDefault("numberOfBestPar", 0);

        // Widen int primitive type, to support integer division resulting in double
        LocalDate bestTime = (LocalDate) parsed.getOrDefault("bestTime", LocalDate.now());
        String bestPlayer = (String) parsed.getOrDefault("bestPlayer", "");
        LocalDate lastBestTime = (LocalDate) parsed.getOrDefault("lastBestTime", LocalDate.now());
        String lastBestPlayer = (String) parsed.getOrDefault("lastBestPlayer", "");

        double bestParPercentage = (double) numberOfBestPar / attempts;

        return new FileSystemTrackStats(
                attempts,
                strokes,
                bestPar,
                bestParPercentage,
                numberOfBestPar,
                bestPlayer,
                bestTime,
                lastBestPlayer,
                lastBestTime,
                ratings,
                track);
    }
}
