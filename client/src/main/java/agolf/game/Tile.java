package agolf.game;

import java.util.UUID;

class Tile {
    private int code;
    private int special;
    private int shape;
    private int background;
    private int foreground;
    private int shapeReduced;
    public UUID uuid;

    public int getSpecialsettingCode(boolean[] trackSettings) {
        int currentCode = code;

        if (special == 2) {
            // 16777216 == blank tile with grass
            // 34144256 == teleport blue exit with grass
            // 34078720 == teleport start with grass

            // 0:false => mines invisible 0:true => mines visible
            if (!trackSettings[0] && (special == 28 || shape == 30)) {
                currentCode = 16777216 + background * 256;
            }

            // 1:false => magnets invisible 1:true => magnets visible
            if (!trackSettings[1] && (shape == 44 || shape == 45)) {
                currentCode = 16777216 + background * 256;
            }

            // 2:false => teleport colorless 2:true => normal colors
            if (!trackSettings[2]) {
                if (shape == 34 || shape == 36 || shape == 38) {
                    currentCode = 34078720 + background * 256;
                }

                if (shape == 35 || shape == 37 || shape == 39) {
                    currentCode = 34144256 + background * 256;
                }
            }
        }
        return currentCode;
    }

    public int getSpecial() {
        return this.special;
    }

    public int getCode() {
        return this.code;
    }

    public int getShape() {
        return this.shape;
    }

    public int getForeground() {
        return this.foreground;
    }

    public int getBackground() {
        return this.background;
    }

    public int getShapeReduced() {
        return this.shapeReduced;
    }

    public int getYPixelsFromSpecialId() {
        switch (this.shape) {
            case 24:
                return 16777215; // Starting point common
            case 48:
                return 11579647; // Start blue
            case 49:
                return 16752800; // Start red
            case 50:
                return 16777088; // Start yellow
            case 51:
                return 9502608; // Start green
            default:
                return -1;
        }
    }

    public Tile(int code) {
        // this.update(code);
        this.code = code;
        this.special = code >> 24;
        this.shape = (code >> 16) % 256 + 24;
        this.shapeReduced = shape - 24;
        this.background = (code >> 8) % 256;
        this.foreground = code % 256;
        this.uuid = UUID.randomUUID();
    }

    public void printDebug() {
        System.out.println("special: " + special);
        System.out.println("shape: " + shape);
        System.out.println("back: " + background);
        System.out.println("uuid: " + uuid);
    }

    public void update(int code) {
        this.code = code;
        this.special = code >> 24;
        this.shape = (code >> 16) % 256 + 24;
        this.shapeReduced = shape - 24;
        this.background = (code >> 8) % 256;
        this.foreground = code % 256;
    }

    public boolean equals(Tile other) {
        if (other == null) {
            return false;
        }
        return this.code == other.getCode();
    }

    public static double calculateFriction(int value, double speed) {
        double friction = getFriction(value);
        double speedModifier = 0.75D * speed / 6.5D;
        double frictionModifier = 1.0D - friction;
        return friction + (frictionModifier * speedModifier);
    }

    private static double getFriction(int var1) {
        return var1 != 0 && (var1 < 4 || var1 > 11) && var1 != 19 && var1 != 47
                ? (var1 == 1
                        ? 0.92D
                        : (var1 == 2
                                ? 0.8D
                                : (var1 != 3 && var1 != 32 && var1 != 34 && var1 != 36 && var1 != 38
                                        ? (var1 != 12 && var1 != 13
                                                ? (var1 != 14 && var1 != 15
                                                        ? (var1 >= 20 && var1 <= 23
                                                                ? 0.995D
                                                                : (var1 == 25
                                                                        ? 0.96D
                                                                        : (var1 != 28 && var1 != 30
                                                                                ? (var1 != 29 && var1 != 31
                                                                                        ? (var1 == 44 ? 0.9D : 1.0D)
                                                                                        : 0.9D)
                                                                                : 1.0D)))
                                                        : 0.95D)
                                                : 0.0D)
                                        : 0.9975D)))
                : 0.9935D;
    }
}
