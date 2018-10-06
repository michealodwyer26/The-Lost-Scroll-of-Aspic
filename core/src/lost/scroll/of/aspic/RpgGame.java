package lost.scroll.of.aspic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import screens.MainMenuScreen;

public class RpgGame extends Game {
	public SpriteBatch batch;
	
	public int stage;
	
	public static final int TILE_SIZE = 16;
	
	public static final int screenWidthInTiles = 25;
	public static final int screenHeightInTiles = 13;
	
	
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	
	public BitmapFont font15;
	public BitmapFont font10;
	public BitmapFont menuFont;
	
	private FreeTypeFontGenerator generator;
	private FreeTypeFontParameter parameter;
	
	public static final float SCREEN_TRANSITION = 1.5f; // Delay between screen transitions
	public static final float FAST_SCREEN_TRANSITION = 0.7f;
	public static final float LOCATION_DOC_TIME = 5f; // Amount of time to show the location document on the screen
	
	public static final int LOCATION_DOC_X = 5;
	public static final int LOCATION_DOC_Y = V_HEIGHT - 30;
	
	private static final int locationDocMargin = 5;
	
	public static final int LOCATION_TEXT_X = LOCATION_DOC_X + locationDocMargin; // Margin is added from the x coord of texture
	public static final int LOCATION_TEXT_Y = V_HEIGHT - locationDocMargin * 2; // Location is got from the top of the screen minus 10 pixels
	
	public static final int MENU_DOC_1_X = 5;
	public static final int MENU_DOC_1_Y = 0;
	public static final int MENU_DOC_1_WIDTH = 135;
	public static final int MENU_DOC_1_HEIGHT = 75;
	
	public static final int MENU_OPTION_X = 50;
	public static final int MENU_OPTION_1_Y = 65;
	public static final int MENU_OPTION_2_Y = 45;
	public static final int MENU_OPTION_3_Y = 25; // used in the fighting screen
	
	public static final int MENU_ARROW_X = 5;
	public static final int MENU_ARROW_WIDTH = 40;
	public static final int MENU_ARROW_HEIGHT = 10;
	
	public static final int MENU_DOC_2_X = 150;
	public static final int MENU_DOC_2_Y = 0;
	public static final int MENU_DOC_2_WIDTH = 240;
	public static final int MENU_DOC_2_HEIGHT = 200;
	
	public static final int MENU_TEXT_X = 165;
	public static final int MENU_TEXT_Y = 150;
	
	public static final int MENU_SPRITE_X = 10;
	public static final int MENU_SPRITE_Y = V_HEIGHT/2 - 10;
	public static final int MENU_SPRITE_WIDTH = 75;
	public static final int MENU_SPRITE_HEIGHT = 100;
	
	private static int offset = 5;
	
	public static final int DIALOGUE_BOX_HEIGHT = 50;
	public static final int DIALOGUE_BOX_X = 25;
	public static final int DIALOGUE_BOX_WIDTH = V_WIDTH - DIALOGUE_BOX_X;
	public static final int DIALOGUE_BOX_Y = V_HEIGHT - DIALOGUE_BOX_HEIGHT - offset;
	
	public static final int DIALOGUE_TEXT_X = DIALOGUE_BOX_X + offset * 2;
	public static final int DIALOGUE_TEXT_Y = DIALOGUE_BOX_Y + offset + DIALOGUE_BOX_HEIGHT/2;
	
	
	public static final int DIALOGUE_TEXT_Y_UPPER = DIALOGUE_BOX_Y + (int)(DIALOGUE_BOX_HEIGHT*0.75); // used when there are two lines in the dialogue
	
	public static final int DIALOGUE_ARROW_X = V_WIDTH - 85;
	public static final int DIALOGUE_ARROW_Y = DIALOGUE_BOX_Y + 2;
	public static final int DIALOGUE_ARROW_WIDTH = 75;
	public static final int DIALOGUE_ARROW_HEIGHT = 15;
	
	public static final float TRANSITION_RADIUS_INCREMENT = 4f;
	public static final float TRANSITION_RADIUS_FAST_INCREMENT = 6f;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		stage = 1;
		
		generator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/LSANS.TTF"));
		parameter = new FreeTypeFontParameter();
		parameter.size = 13;
		
		font10 = generator.generateFont(parameter);
		
		parameter.size = 18;
		font15 = generator.generateFont(parameter);
		generator.dispose();
		
		generator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/MoriaCitadel.TTF"));
		parameter.size = 15;
		menuFont = generator.generateFont(parameter);
		generator.dispose();
		
		font10.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		font15.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		menuFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font15.dispose();
		font10.dispose();
	}
}

