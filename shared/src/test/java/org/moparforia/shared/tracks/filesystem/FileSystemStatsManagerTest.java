package org.moparforia.shared.tracks.filesystem;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.moparforia.shared.tracks.Track;
import org.moparforia.shared.tracks.TrackCategory;
import org.moparforia.shared.tracks.TracksLocation;
import org.moparforia.shared.tracks.stats.TrackStats;
import org.moparforia.shared.tracks.util.FileSystemExtension;

class FileSystemStatsManagerTest {
    private final double PRECISION = 0.001d;

    @RegisterExtension
    final FileSystemExtension extension = new FileSystemExtension("v2/");

    FileSystemStatsManager statsManager;

    Track single = new Track(
            "4 da Crew", "Aither", "Data", new HashSet<>(Arrays.asList(TrackCategory.MODERN, TrackCategory.BASIC)));
    Track empty_stats = new Track("SprtTrack", "Sprt", "Data", Collections.singleton(TrackCategory.MODERN));

    @BeforeEach
    void beforeEach() {
        statsManager = new FileSystemStatsManager();
    }

    @Test
    void testSimpleLoad() throws IOException, URISyntaxException {
        extension.copyAll();
        TracksLocation tracksLocation = new TracksLocation(this.extension.getFileSystem(), "tracks");
        statsManager.load(tracksLocation);

        TrackStats stats = statsManager.getStats(single);
        assertEquals("Sprt", stats.getBestPlayer());
        assertEquals(537, stats.getNumCompletions());
        assertEquals(11734, stats.getTotalStrokes());
        assertEquals(4, stats.getBestPar());
        assertEquals(0.039, stats.getPercentageOfBestPar(), PRECISION);
        assertEquals(7.752, stats.getAverageRating(), PRECISION);
    }

    @Test
    void testEmptyStats() throws IOException, URISyntaxException {
        extension.copyAll();
        TracksLocation tracksLocation = new TracksLocation(this.extension.getFileSystem(), "tracks");

        statsManager.load(tracksLocation);
        TrackStats stats = statsManager.getStats(empty_stats);
        assertEquals("", stats.getBestPlayer());
        assertEquals(0, stats.getNumCompletions());
        assertEquals(0, stats.getTotalStrokes());
        assertEquals(-1, stats.getBestPar());
        assertEquals(NaN, stats.getPercentageOfBestPar(), PRECISION);
        assertEquals(NaN, stats.getAverageRating(), PRECISION);
    }
}
