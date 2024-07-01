package org.moparforia.shared.tracks.filesystem;

import org.moparforia.shared.Tools;
import org.moparforia.shared.tracks.*;
import org.moparforia.shared.tracks.parsers.TrackParser;
import org.moparforia.shared.tracks.parsers.VersionedTrackFileParser;
import org.moparforia.shared.utils.CollectorUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileSystemTrackManager implements TrackManager {
    private static FileSystemTrackManager instance;
    private static final TrackParser parser = new VersionedTrackFileParser();
    private final Logger logger =  Logger.getLogger(FileSystemTrackManager.class.getName());

    private List<Track> tracks;
    private List<TrackSet> trackSets;
    private boolean hasLoaded;

    public static FileSystemTrackManager getInstance() {
        if (instance == null) {
            instance = new FileSystemTrackManager();
        }
        return instance;
    }

    @Override
    public void load(TracksLocation tracksLocation) throws TrackLoadException {
        try {
            tracks = loadTracks(tracksLocation);
            logger.info("Loaded " + tracks.size() + " tracks");
            trackSets = loadTrackSets(tracksLocation);
            logger.info("Loaded " + trackSets.size() + " track sets");
        } catch (IOException e) {
            throw new TrackLoadException("Unable to load tracks and tracksets", e);
        }

        hasLoaded = true;
    }

    @Override
    public boolean isLoaded() {
        return hasLoaded;
    }


    public void save(Track track) {
        // No-op, this implementation is only read-only
    }

    public static String convertTrack(Track track) {
        return Tools.tabularize(
                "V 1",
                "A " + track.getAuthor(),
                "N " + track.getName(),
                "T " + track.getMap());
    }

    private List<Track> loadTracks(TracksLocation tracksLocation) throws IOException {
        List<Track> tracks = new ArrayList<>();
        Path tracksPath = tracksLocation.fileSystem().getPath(tracksLocation.path(), "tracks");

        if (!Files.exists(tracksPath)) {
            logger.warning("Tracks directory (" + tracksLocation.path() + "/tracks) was not found, ignoring.");
            return Collections.emptyList();
        }
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(tracksPath,
                entry -> entry.toString().endsWith(".track"));
        for (Path filePath : directoryStream) {
            try {
                tracks.add(parser.parseTrack(filePath));
            } catch (IOException e) {
                logger.warning("Unable to parse file " + filePath);
                logger.info(e.toString());
            }
        }
        return tracks;
    }

    private List<TrackSet> loadTrackSets(TracksLocation tracksLocation) throws IOException {
        List<TrackSet> trackSets = new ArrayList<>();
        Path sets = tracksLocation.fileSystem().getPath(tracksLocation.path(), "sets");
        if (!Files.exists(sets)) {
            logger.warning("Can't load tracksets, directory " + tracksLocation.path() + "/sets does not exist, ignoring.");
            return trackSets;
        }

        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sets,
                entry -> entry.getFileName().toString().endsWith(".trackset"));
        for (Path filePath : directoryStream) {
            Scanner scanner = new Scanner(filePath);
            String setName = scanner.nextLine();
            TrackSetDifficulty trackSetDifficulty = TrackSetDifficulty.valueOf(scanner.nextLine());
            List<String> trackNames = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    trackNames.add(line);
                }
            }

            // Convert fileNames to already loaded tracks
            List<Track> tracks = getTracks().stream()
                    .filter(track -> trackNames.contains(track.getName()))
                    .sorted(Comparator.comparing((track) -> trackNames.indexOf(track.getName())))
                    .collect(Collectors.toList());
            if (tracks.isEmpty()) {
                logger.warning("TrackSet " + setName + " has no valid tracks associated with it, ignoring");
                continue;
            }
            // This is not 100% correct since the tracks contain lot of duplicates
            if (tracks.size() < trackNames.size()) {
                List<String> found_tracks = tracks.stream().map(Track::getName).toList();
                trackNames.removeAll(found_tracks);
                logger.warning("TrackSet " + setName + " contains not existing tracks (" + Arrays.toString(trackNames.toArray()) + ")");
            }
            trackSets.add(new TrackSet(setName, trackSetDifficulty, tracks));
        }
        trackSets.sort(Comparator.comparing(TrackSet::getName));
        return trackSets;
    }


    public List<Track> getRandomTracks(int limit, TrackCategory type) {
        if (limit < 1) {
            throw new IllegalArgumentException("Number of tracks must be at least 1");
        }

        return getTracks().stream()
                .filter(track -> type == TrackCategory.ALL || track.getCategories().contains(type))
                .collect(CollectorUtils.toShuffledStream())
                .limit(limit)
                .collect(Collectors.toList());

    }

    @Override
    public List<Track> getTracks() {
        return tracks;
    }

    @Override
    public TrackSet getTrackSet(String name) {
        return getTrackSets()
                .stream()
                .filter(trackset -> trackset.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Track> getTrackSet(int number) {
        return trackSets.get(number).getTracks();
    }

    public List<TrackSet> getTrackSets() {
        return trackSets;
    }

    @Override
    public Track findTrackByName(String name) {
        return getTracks().stream()
                .filter(track -> track.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Track> findAllByCategory(TrackCategory category) {
        if (category.equals(TrackCategory.ALL)) {
            return getTracks();
        }
        return getTracks().stream()
                .filter(track -> track.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    public boolean hasLoaded() {
        return hasLoaded;
    }
}
