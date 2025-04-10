package org.moparforia.shared.tracks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackSet {

    private int id;
    private String name;
    private TrackSetDifficulty difficulty;
    private List<Track> tracks;

    private int allTimeBestStrokes;

    private String allTimeBestPlayer;
    private int monthBestStrokes;
    private String monthBestPlayer;
    private int weekBestStrokes;
    private String weekBestPlayer;
    private int dayBestStrokes;
    private String dayBestPlayer;

    public TrackSet(int id, String name, TrackSetDifficulty difficulty, List<Track> tracks) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.tracks = tracks;
        this.allTimeBestStrokes = 1;
        this.allTimeBestPlayer = "No one";
        this.monthBestStrokes = 1;
        this.monthBestPlayer = "No one";
        this.weekBestStrokes = 1;
        this.weekBestPlayer = "No one";
        this.dayBestStrokes = 1;
        this.dayBestPlayer = "No one";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TrackSetDifficulty getDifficulty() {
        return difficulty;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public int getAllTimeBestStrokes() {
        return allTimeBestStrokes;
    }

    public String getAllTimeBestPlayer() {
        return allTimeBestPlayer;
    }

    public int getMonthBestStrokes() {
        return monthBestStrokes;
    }

    public String getMonthBestPlayer() {
        return monthBestPlayer;
    }

    public int getWeekBestStrokes() {
        return weekBestStrokes;
    }

    public String getWeekBestPlayer() {
        return weekBestPlayer;
    }

    public int getDayBestStrokes() {
        return dayBestStrokes;
    }

    public String getDayBestPlayer() {
        return dayBestPlayer;
    }

    public boolean equals(Object o) {
        if (!(o instanceof TrackSet t)) {
            return false;
        }
        return difficulty == t.difficulty && name.equals(t.name) && tracks.equals(t.tracks);
    }

    public String serialize() {
        List<String> data = new ArrayList<>();
        data.add(name);
        data.add(String.valueOf(difficulty.getId()));
        data.add(String.valueOf(tracks.size()));
        data.add(allTimeBestPlayer);
        data.add(String.valueOf(allTimeBestStrokes));
        data.add(monthBestPlayer);
        data.add(String.valueOf(monthBestStrokes));
        data.add(weekBestPlayer);
        data.add(String.valueOf(weekBestStrokes));
        data.add(dayBestPlayer);
        data.add(String.valueOf(dayBestStrokes));
        return String.join("\t", data);
    }

    public static TrackSet deserialize(int id, String[] data) {
        int tracksCount = Integer.parseInt(data[2]);
        return new TrackSet(
                id,
                data[0],
                TrackSetDifficulty.fromId(Integer.parseInt(data[1])),
                Collections.nCopies(tracksCount, null));
    }
}
