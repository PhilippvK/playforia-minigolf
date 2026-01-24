package org.moparforia.shared.tracks.stats;

import java.time.LocalDate;
import org.moparforia.shared.tracks.Track;

/**
 * Interface that represent all the statistics for specific track, class is interface so it supports
 * lazy loading
 */
public interface TrackStats {

    /**
     * @return Track that these stats are for
     */
    Track getTrack();

    /**
     * @return Total number of attempts across all the games
     */
    int getNumCompletions();

    /**
     * @return Total number of strobes across all the games
     */
    int getTotalStrokes();

    /**
     * @return Get best par achieved on this track
     */
    int getBestPar();

    /**
     * @return Percentage of best par among all the completions
     */
    double getPercentageOfBestPar();

    /**
     * @return Name of the player who achieved best score first
     */
    String getBestPlayer();

    /**
     * @return Date of the first best par achieved
     */
    LocalDate getBestParTime();

    /**
     * Returns ratings of this track. Index is representing the actual rating and the value is
     * number of ratings of that value
     *
     * @return Array of all the ratings of size 10
     */
    int[] getRatings();

    /**
     * @return Average rating of this track
     */
    double getAverageRating();

    /**
     * @return Total number of ratings fo this track
     */
    int getNumberOfRatings();

    /**
     * Converts TrackStats into serializable string
     *
     * @return Serialized string, separated by splitter
     */
    String serialize(String splitter);

    /** Converts to format supported by Client */
    String networkSerialize();

    /** Bundles information from Track and TrackStats into array */
    public String[] generateTrackInformation();

    /** Bundles information about track statistics */
    public int[][] generateTrackStatistics();
}
