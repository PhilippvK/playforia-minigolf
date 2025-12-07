package agolf.game;

import java.util.StringTokenizer;

class Track {
    private String trackAuthor;
    private String trackName;
    private String trackComment;
    private String trackSettings;
    private String trackFirstBest;
    private String trackLastBest;
    /*
     * trackStats:
     * 0 = num of completions
     * 1 = num of strokes
     * 2 = best num of strokes
     * 3 = num of players who got best num of strokes
     */
    private int[] trackStats;
    private int[] trackRatings;

    /*
     * this is default value:
     * trackSpecialSettings[0]= false
     * trackSpecialSettings[1]= true
     * trackSpecialSettings[2]= true
     * trackSpecialSettings[3]= true
     */
    private static final String defaultTrackSettings = "fttt14";
    public boolean[] trackSpecialSettings;
    public Map map;

    public Track(Map map) {
        this.map = map;
    }

    boolean parse(String map) {
        this.trackSettings = defaultTrackSettings;
        this.trackFirstBest = null;
        this.trackLastBest = null;
        this.trackComment = null;
        this.trackStats = null;
        this.trackRatings = null;

        StringTokenizer tokenizer = new StringTokenizer(map, "\n");
        int requiredLinesParsed = 0;
        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken();
            if (line.startsWith("V ") && Integer.parseInt(line.substring(2)) == 1) {
                requiredLinesParsed++;
            } else if (line.startsWith("A ")) {
                requiredLinesParsed++;
                this.trackAuthor = line.substring(2).trim();
            } else if (line.startsWith("N ")) {
                requiredLinesParsed++;
                this.trackName = line.substring(2).trim();
            } else if (line.startsWith("C ")) {
                this.trackComment = line.substring(2).trim();
            } else if (line.startsWith("S ")) {
                this.trackSettings = line.substring(2).trim();
                if (trackSettings.length() != 6) {
                    return false;
                }
            } else if (line.startsWith("I ")) {
                StringTokenizer subtknzr = new StringTokenizer(line.substring(2), ",");
                if (subtknzr.countTokens() != 4) {
                    return false;
                }
                this.trackStats = new int[4];
                for (int i = 0; i < 4; i++) {
                    this.trackStats[i] = Integer.parseInt(subtknzr.nextToken());
                }
            } else if (line.startsWith("B ")) {
                this.trackFirstBest = line.substring(2);
            } else if (line.startsWith("L ")) {
                this.trackLastBest = line.substring(2);
            } else if (line.startsWith("R ")) {
                StringTokenizer subTokenizer = new StringTokenizer(line.substring(2), ",");
                if (subTokenizer.countTokens() != 11) {
                    return false;
                }
                this.trackRatings = new int[11];
                for (int i = 0; i < 11; i++) {
                    this.trackRatings[i] = Integer.parseInt(subTokenizer.nextToken());
                }
            } else if (line.startsWith("T ")) {
                requiredLinesParsed++;
                String mapData = line.substring(2);
                this.map.parse(mapData);
            }
        }
        if (requiredLinesParsed != 4) {
            return false;
        }

        this.trackSpecialSettings = new boolean[4];
        for (int i = 0; i < 4; i++) {
            this.trackSpecialSettings[i] = this.trackSettings.charAt(i) == 't';
        }

        return true;
    }

    protected String getTrackComment() {
        return this.trackComment;
    }

    protected String getTrackSettings() {
        return this.trackSettings.equals("fttt14") ? null : this.trackSettings;
    }

    protected String[] generateTrackInformation() {
        return new String[] {this.trackAuthor, this.trackName, this.trackFirstBest, this.trackLastBest};
    }

    protected int[][] generateTrackStatistics() {
        return new int[][] {this.trackStats, this.trackRatings};
    }
}
