package entities;


import java.util.HashMap;
import java.util.Map;

import utilz.constants.PlayerConstants;



public class CharacterManager {
    private Map<Integer, Map<Integer, Integer>> spriteAmounts;
    public static final int CHARACTER_NARUTO = 1;
    public static final int CHARACTER_SASUKE = 2;
    public static final int CHARACTER_SAKURA = 3;
    public static final int CHARACTER_KAKASHI = 4;


    public CharacterManager() {
        spriteAmounts = new HashMap<>();
        // Define sprite amounts for each character
        initSpriteAmounts();
    }

    private void initSpriteAmounts() {
        // Define sprite amounts for each character and action
    	
        // for Naruto
        Map<Integer, Integer> narutoSpriteAmounts = new HashMap<>();
        narutoSpriteAmounts.put(PlayerConstants.IDLE, 6);
        narutoSpriteAmounts.put(PlayerConstants.RUNNING, 6
        		);
        narutoSpriteAmounts.put(PlayerConstants.HIT, 4);
        narutoSpriteAmounts.put(PlayerConstants.ATTACK_1, 4);
        narutoSpriteAmounts.put(PlayerConstants.JUMP,4);
        narutoSpriteAmounts.put(PlayerConstants.ATTACK_JUMP_1, 4);
        narutoSpriteAmounts.put(PlayerConstants.ATTACK_JUMP_2, 5);
        narutoSpriteAmounts.put(PlayerConstants.DEAD, 5);
        
        // for saskuke     
        Map<Integer, Integer> sasukeSpriteAmounts = new HashMap<>();
        sasukeSpriteAmounts.put(PlayerConstants.IDLE, 4);
        sasukeSpriteAmounts.put(PlayerConstants.RUNNING, 6);
        sasukeSpriteAmounts.put(PlayerConstants.HIT,5);
        sasukeSpriteAmounts.put(PlayerConstants.ATTACK_1, 5);
        sasukeSpriteAmounts.put(PlayerConstants.JUMP, 4);
       // sasukeSpriteAmounts.put(PlayerConstants.ATTACK_JUMP_1, 6);
        //sasukeSpriteAmounts.put(PlayerConstants.ATTACK_JUMP_2, 5);
        sasukeSpriteAmounts.put(PlayerConstants.DEAD, 5);
        
        // for sakura   
        Map<Integer, Integer> sakuraSpriteAmounts = new HashMap<>();
        sakuraSpriteAmounts.put(PlayerConstants.IDLE, 5);
        sakuraSpriteAmounts.put(PlayerConstants.RUNNING, 6);
        sakuraSpriteAmounts.put(PlayerConstants.HIT, 4);
        sakuraSpriteAmounts.put(PlayerConstants.ATTACK_1, 5);
        sakuraSpriteAmounts.put(PlayerConstants.JUMP,5);
        sakuraSpriteAmounts.put(PlayerConstants.ATTACK_JUMP_1, 5);
        sakuraSpriteAmounts.put(PlayerConstants.ATTACK_JUMP_2, 5);
        sakuraSpriteAmounts.put(PlayerConstants.DEAD, 5);
        
        //for kakashi
        Map<Integer, Integer> kakashiSpriteAmounts = new HashMap<>();
        kakashiSpriteAmounts.put(PlayerConstants.IDLE, 6);
        kakashiSpriteAmounts.put(PlayerConstants.RUNNING, 6);
        kakashiSpriteAmounts.put(PlayerConstants.HIT, 5);
        kakashiSpriteAmounts.put(PlayerConstants.ATTACK_1, 3);
        kakashiSpriteAmounts.put(PlayerConstants.JUMP,5);
        kakashiSpriteAmounts.put(PlayerConstants.ATTACK_JUMP_1, 4);
        kakashiSpriteAmounts.put(PlayerConstants.ATTACK_JUMP_2, 6);
        kakashiSpriteAmounts.put(PlayerConstants.DEAD, 5);
    
        
        spriteAmounts.put(CHARACTER_NARUTO, narutoSpriteAmounts);
        spriteAmounts.put(CHARACTER_SASUKE, sasukeSpriteAmounts);
        spriteAmounts.put(CHARACTER_SAKURA, sakuraSpriteAmounts);
        spriteAmounts.put(CHARACTER_KAKASHI, kakashiSpriteAmounts);

      
    }

    public int getSpriteAmount(int character, int action) {
        if (spriteAmounts.containsKey(character)) {
            Map<Integer, Integer> characterSpriteAmounts = spriteAmounts.get(character);
            if (characterSpriteAmounts.containsKey(action)) {
                return characterSpriteAmounts.get(action);
            }
        }
        return 1;	 // if character or action not found just return 1
    }
}
