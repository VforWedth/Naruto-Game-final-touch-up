package entities;

import java.awt.image.BufferedImage;
import java.util.Map;

public class Character {
    private String name;
    private String spriteSheet;
    private Map<Integer, Integer> actionSpriteCounts;

    public Character(String name, String spriteSheet, Map<Integer, Integer> actionSpriteCounts) {
        this.name = name;
        this.spriteSheet = spriteSheet;
        this.actionSpriteCounts = actionSpriteCounts;
    }

    public String getName() {
        return name;
    }

    public String getSpriteSheet() {
        return spriteSheet;
    }

    public Map<Integer, Integer> getActionSpriteCounts() {
        return actionSpriteCounts;
    }
}
