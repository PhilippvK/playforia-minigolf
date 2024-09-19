package org.moparforia.shared.tracks;

import org.moparforia.shared.Tools;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Track {

    private final String track;

    private final String author;

    private String map;

    private Set<TrackCategory> categories;

    public Track(String name, String author, String map, Set<TrackCategory> categories) {
        this.track = name;
        this.author = author;
        this.map = map;
        this.categories = categories;
    }

    public Track(String track, String author, String map) {
        this(track, author, map, Collections.emptySet());
    }

    public Track(String track, String author, String map, TrackCategory category) {
        this(track, author, map, Collections.singleton(category));
    }

    /**
     * new Track(1,"Boats and hoes","fc","B3A48DE48DE48DE48DE48DE48DEBAQQ46D3EG13DEG14DEG13D5E13DEE14DEE13D5E13DEE14DEE13D5E13DEE14DEE13D5E13DEE6DBMAQE6DEE13D5ECAAE11DEE6DBAQQE6DEE11DCBA6E13DBOAQE6DEE6DBOAQE13D5E21DEE21D5E21DEE21D5E21DEE21D5E21DEE21D4E46DEE48DE48DE48DE48DE48DE48D",
     * new int[]{2629492,7166639,2,1191141},new String[] {"fc","Tiikoni"}, new int[]{1034197200000,1370170660930},
     * new int[]{1630,567,647,835,1148,3945,3755,3346,2924,2672,21566});
     */

    public String getName() {
        return track;
    }

    public String getAuthor() {
        return author;
    }

    public Set<TrackCategory> getCategories() {
        return categories;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String data) {
        this.map = data;
    }

    public void setCategories(Set<TrackCategory> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;
        Track track1 = (Track) o;
        return track.equals(track1.track) &&
                getAuthor().equals(track1.getAuthor()) &&
                getMap().equals(track1.getMap()) &&
                getCategories().equals(track1.getCategories());
    }

    @Override
    public int hashCode() {
        return Objects.hash(track, getAuthor(), getMap(), getCategories());
    }

    public String serialize(String splitter) {
        String categories = getCategories()
                .stream()
                .map(category -> String.valueOf(category.getId()))
                .collect(Collectors.joining(","));
        return Tools.izer(splitter,
                "V 2",
                "A " + getAuthor(),
                "N " + getName(),
                "T " + getMap(),
                "C " + categories);
    }

}