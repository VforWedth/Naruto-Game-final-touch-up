package utilz;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;
import entities.Player;

public class LoadSave {

	public static final String PLAYER_ATLAS = Player.ChooseCharacter();
	public static final String LEVEL_ATLAS = "NewTiles.png";
//	public static final String LEVEL_ONE_DATA = "level_one_data_2.png";
	public static final String MENU_BUTTONS = "button_atlas.png";
	public static final String MENU_BACKGROUND = "menu_background.png";
	public static final String PAUSE_BACKGROUND = "pause_menu.png";
	public static final String SOUND_BUTTONS = "sound_button.png";
	public static final String URM_BUTTONS = "urm_buttons.png";	
	public static final String VOLUME_BUTTONS = "volume_buttons.png";	
	public static final String MENU_BACKGROUND_IMAGE = "background_menu.png";
	public static final String PLAYING_BACKGROUND_IMAGE = "bg.jpg";
	public static final String KONOHA_HOUSES = "konohaBg.png";
	public static final String SMALL_CLOUDS = "small_clouds.png";
	public static final String BIG_CLOUDS = "big_clouds.png";
	public static final String CRABBY_SPRITE = "crabby_sprite.png";
	public static final String STATUS_BAR = "health_power_bar.png";
	public static final String COMPLETED_IMG = "completed_sprite.png";
	public static final String DEATH_SCREEN = "death_screen.png";
	public static final String OPTIONS_MENU = "options_background.png";
	
	//for water
	public static final String WATER_TOP = "water_atlas_animation.png";
	public static final String WATER_BOTTOM = "water.png";

	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img = null;
		InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
		try {
			img = ImageIO.read(is);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return img;
	}
	
	public static BufferedImage[] GetAllLevels() {
		URL url = LoadSave.class.getResource("/lvls");
		File file = null;
		
		try {
			file = new File(url.toURI());
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
		File[] files = file.listFiles();
		File[] filesSorted = new File[files.length];
		
		for (int i = 0; i < filesSorted.length; i++)
			for (int j = 0; j < files.length; j++) {
				if (files[j].getName().equals((i + 1) + ".png"))
					filesSorted[i] = files[j];

			}
		
		BufferedImage[] imgs = new BufferedImage[filesSorted.length];
		
		for(int i = 0; i < imgs.length; i++)
			try {
				imgs[i] = ImageIO.read(filesSorted[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return imgs;
	}
	
}