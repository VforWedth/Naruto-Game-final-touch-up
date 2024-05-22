package utilz;

import entities.Character;
import utilz.constants.PlayerConstants;

import java.util.HashMap;
import java.util.Map;

public class CharacterManager {
    private Map<String, Character> characters;

    public CharacterManager() {
        characters = new HashMap<>();
        initCharacters();
    }

    private void initCharacters() {
       
        characters.put("Naruto", new Character("Uzumaki Naruto", "sprite1.png", Map.of(
                PlayerConstants.IDLE, 6,
                PlayerConstants.RUNNING, 6,
                PlayerConstants.HIT, 4,
               // PlayerConstants.THORW, 4,
                PlayerConstants.JUMP, 4
                
        )));
        
        characters.put("Sasuke", new Character("Sasuke Uchiha", "sprite2.png", Map.of(
                PlayerConstants.IDLE, 4,
                PlayerConstants.RUNNING, 6,
                PlayerConstants.HIT, 6, 
               // PlayerConstants.THORW, 4,
                PlayerConstants.JUMP, 7
                // Add other actions as needed
        )));
        
        characters.put("Kakashi", new Character("Hatake Kakashi", "sprite4.png", Map.of(
                PlayerConstants.IDLE, 6,
                PlayerConstants.RUNNING, 6,
                PlayerConstants.HIT, 5,
               // PlayerConstants.THORW, 4,
                PlayerConstants.JUMP, 4
                // Add other actions as needed
        )));
        characters.put("Sakura", new Character("Sakura Haruno", "sprite3.png", Map.of(
                PlayerConstants.IDLE, 5,
                PlayerConstants.RUNNING, 6,
                PlayerConstants.HIT, 4,
               // PlayerConstants.THORW, 4,
                PlayerConstants.JUMP, 4
                // Add other actions as needed
        )));
        // Add more characters as needed
    }

    public Character getCharacter(String name) {
        return characters.get(name);
    }
}
