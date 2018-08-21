package screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import entities.Player;
import entities.SultanVladi;
import lost.scroll.of.aspic.RpgGame;
import tools.TiledMapRendererWithSprites;

public class Village2SultanHouse implements Screen{

	private RpgGame game;
	private TiledMap map;
	private OrthographicCamera gamecam;
	private TiledMapRendererWithSprites mapRenderer;
	private Viewport gamePort;
	private Player player;
	private SultanVladi sultanVladi;
	private ShapeRenderer shapeRenderer;
	
	private TextureRegion playerTexture;
	private Texture menuDocument1;
	private Texture menuDocument2;
	private Texture arrow;
	private Texture dialogueBox;
	
	private ArrayList<Rectangle> mapObjectsRects;
	private ArrayList<Rectangle> specialMapObjectsRects;
	
	private boolean debugLines;
	private int arrowPositionInMenu;
	
	private boolean displayMenu;
	private boolean displayDialogueBox;
	
	private float mapSizeX;
	
	private float minCamY = RpgGame.TILE_SIZE * 7f;
	private float maxCamX = RpgGame.TILE_SIZE * 10f;
	
	private String currentDialogueText;
	private String[] menuOptions;
	private String[] menuText;
	
	public String[] allDialogueText;
	
	private boolean inConversation;
	private int currentDialogueLine;
	
	public Village2SultanHouse(RpgGame game) {
		this.game = game;
		
		map = new TmxMapLoader().load("data/maps/sultanHouse2.tmx");
		gamecam = new OrthographicCamera();
		gamePort = new FitViewport(RpgGame.V_WIDTH, RpgGame.V_HEIGHT, gamecam);
		
		player = new Player(game, 8 * RpgGame.TILE_SIZE, 0, "north");
		sultanVladi = new SultanVladi(game);
		
		mapRenderer = new TiledMapRendererWithSprites(map);
		mapRenderer.addSprite(player);
		mapRenderer.addSprite(sultanVladi);
		
		// ADD MORE NPCS HERE IF NEEDED

		mapObjectsRects = new ArrayList<Rectangle>();
		specialMapObjectsRects = new ArrayList<Rectangle>();

		shapeRenderer = new ShapeRenderer();
		
		for(MapObject mapObject : map.getLayers().get("Objects").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) mapObject).getRectangle();
			mapObjectsRects.add(rect);
		}
		
		for(MapObject mapObject : map.getLayers().get("Special Objects").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) mapObject).getRectangle();
			specialMapObjectsRects.add(rect);
			
		}
		
		menuDocument1 = new Texture(Gdx.files.internal("data/misc/menuDocument2.png"));
		menuDocument2 = new Texture(Gdx.files.internal("data/misc/menuDocument1.png"));
		arrow = new Texture(Gdx.files.internal("data/misc/fancyarrow.png"));
		dialogueBox = new Texture(Gdx.files.internal("data/misc/dialoguebox.png"));
	
		debugLines = false;
		displayMenu = false;
		arrowPositionInMenu = 0;
		
		mapSizeX = 16 * RpgGame.TILE_SIZE;
		
		menuOptions = new String[2];
		menuOptions[0] = "Inventory";
		menuOptions[1] = "Back";
		
		menuText = new String[2];
		menuText[0] = "You do not have any items.";
		
		playerTexture = new TextureRegion(player.getTexture(), 102, 32, 20, 32);
		
		game.font15.setColor(Color.BLACK);
		
		allDialogueText = new String[20];
		allDialogueText[0] = "What's this? A Popescan has navigated all the way to my\ndynasty! Be gone!";
		
		allDialogueText[1] = "What's this? A Popescan has navigated all the way to my\ndynasty!";	
		allDialogueText[2] = "You are far from a Popescan. No Popescan can stray\nfrom their Scroll of Aspic.";
		allDialogueText[3] = "Without exhibiting symptoms of accute ignorance\nthat is.";
		allDialogueText[4] = "So, after all these years, Popescu sends you looking\nfor help.";
		allDialogueText[5] = "Take a sword, and be gone!";
		
		allDialogueText[6] = "I have given you enough!\n Take it or leave it!";
		
		inConversation = false;
		if(game.stage == 1) {
			currentDialogueLine = 0;
		} else if(game.stage == 2) {
			currentDialogueLine = 1;
		} else if(game.stage == 3 || game.stage == 4 || game.stage == 5) {
			currentDialogueLine = 6;
		}
	}
	
	public void handleInput() {
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {
			displayMenu = displayMenu ? false : true;			
			game.font10.setColor(Color.BLACK);
			arrowPositionInMenu = 56;
		}
		
		
		if(!displayMenu && !inConversation && !player.isWalkingToDestination) {
			boolean isWalking = (player.walkingSouth || player.walkingNorth || player.walkingEast || player.walkingWest) ? true : false;
			
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && !isWalking) {
				player.walkingSouth = true;
				displayDialogueBox = false;
			}
			
			else if(Gdx.input.isKeyPressed(Input.Keys.UP) && !isWalking) {
				player.walkingNorth = true;
			}
			
			else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !isWalking) {
				player.walkingEast = true;
				displayDialogueBox = false;
				
			}
			
			else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && !isWalking) {
				player.walkingWest = true;
				displayDialogueBox = false;
				
			}
			
			if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
				debugLines = debugLines == true ? false : true;
			}
		}
		
		if(displayMenu) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
				if(arrowPositionInMenu > 36) {
					arrowPositionInMenu -= 20;
				}
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
				if(arrowPositionInMenu < 56) {
					arrowPositionInMenu += 20;
				}
			}
			
			if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				if(arrowPositionInMenu == 36) {
					displayMenu = false;
				}
				
			}
		}
		
		if(displayDialogueBox) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				if(inConversation) {
					currentDialogueLine += 1;
					if(currentDialogueLine < 6) {
						displayDialogue(allDialogueText[currentDialogueLine]);
					}
					else {
						displayDialogueBox = false;
						inConversation = false;
						game.stage += 1;
					}
				}
				else {
					displayDialogueBox = false;
				}
			}
		}
	}

	public void checkCamBounds() {
		if(gamecam.position.y < minCamY) {
			gamecam.position.y = minCamY;
		}
		if(gamecam.position.y > maxCamX) {
			gamecam.position.y = maxCamX;
		}
	}
	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		handleInput();
		
		player.update(delta, mapObjectsRects);
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(!player.isWalkingToDestination) {
			player.checkCollisions(mapObjectsRects, specialMapObjectsRects, sultanVladi.rect);
		}
		
		if(player.isWalkingToDestination) {
			if(player.getWalkingToDestinationTimer() > RpgGame.FAST_SCREEN_TRANSITION) {
				if(player.destination.equals("Village 2")) {
					game.setScreen(new Village2(game, 224f, 640f, "south"));
				}
			}
		}

		// gamecam x position is always the same, the y changes
		gamecam.position.x = 0 + mapSizeX / 2;
		gamecam.position.y = player.getY();
		
		// 112 - 160
		checkCamBounds();
		
		gamecam.update();
		
		mapRenderer.setView(gamecam);
		mapRenderer.render();
		
		if(debugLines) {
			shapeRenderer.setProjectionMatrix(gamecam.combined);
			shapeRenderer.begin(ShapeType.Line);
			
			shapeRenderer.setColor(1, 0, 0, 1);
			
			for(Rectangle rect : mapObjectsRects) {
				shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
			}
			
			
			shapeRenderer.setColor(0, 1, 0, 1);
			
			for(Rectangle rect : specialMapObjectsRects) {
				shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
			}
			
			shapeRenderer.setColor(0, 0, 1, 1);
			
			shapeRenderer.rect(player.rect.x, player.rect.y, player.rect.width, player.rect.height);
			shapeRenderer.rect(sultanVladi.rect.x, sultanVladi.rect.y, sultanVladi.rect.width, sultanVladi.rect.height);
			
			shapeRenderer.end();
		}
		
		if(displayMenu) {
			Gdx.gl.glClearColor(0, 0, 0, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			game.batch.begin();
			
			game.batch.draw(menuDocument1, RpgGame.MENU_DOC_1_X, RpgGame.MENU_DOC_1_Y, RpgGame.MENU_DOC_1_WIDTH, RpgGame.MENU_DOC_1_HEIGHT);
			
			game.font10.draw(game.batch, menuOptions[0], RpgGame.MENU_OPTION_X, RpgGame.MENU_OPTION_1_Y);
			game.font10.draw(game.batch, menuOptions[1], RpgGame.MENU_OPTION_X, RpgGame.MENU_OPTION_2_Y);
			
			game.batch.draw(arrow, RpgGame.MENU_ARROW_X, arrowPositionInMenu, RpgGame.MENU_ARROW_WIDTH, RpgGame.MENU_ARROW_HEIGHT);

			game.batch.draw(menuDocument2, RpgGame.MENU_DOC_2_X, RpgGame.MENU_DOC_2_Y, RpgGame.MENU_DOC_2_WIDTH, RpgGame.MENU_DOC_2_HEIGHT);
			
			switch(arrowPositionInMenu) {
				case 56:
					game.font10.draw(game.batch, menuText[0], RpgGame.MENU_TEXT_X, RpgGame.MENU_TEXT_Y);
					break;
			}
						
			game.batch.draw(playerTexture, RpgGame.MENU_SPRITE_X, RpgGame.MENU_SPRITE_Y, RpgGame.MENU_SPRITE_WIDTH, RpgGame.MENU_SPRITE_HEIGHT);
			game.batch.end();
			
		}
		
		if(displayDialogueBox && !displayMenu) {
			game.batch.begin();
			
			game.batch.draw(dialogueBox, RpgGame.DIALOGUE_BOX_X, RpgGame.DIALOGUE_BOX_Y, RpgGame.DIALOGUE_BOX_WIDTH, RpgGame.DIALOGUE_BOX_HEIGHT);
			
			if(!currentDialogueText.contains("\n")) {
				game.font10.draw(game.batch, currentDialogueText, RpgGame.DIALOGUE_TEXT_X, RpgGame.DIALOGUE_TEXT_Y);
			} else {
				game.font10.draw(game.batch, currentDialogueText, RpgGame.DIALOGUE_TEXT_X, RpgGame.DIALOGUE_TEXT_Y_UPPER);				
			}
			
			game.batch.draw(arrow, RpgGame.DIALOGUE_ARROW_X, RpgGame.DIALOGUE_ARROW_Y, RpgGame.DIALOGUE_ARROW_WIDTH, RpgGame.DIALOGUE_ARROW_HEIGHT);
			
			game.batch.end();
		}
	}
	
	public void displayDialogue(String text) {
		displayDialogueBox = true;
		currentDialogueText = text;
		game.font10.setColor(Color.BLACK);
	}

	@Override
	public void resize(int width, int height) {
		gamePort.update(width, height);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		map.dispose();
		mapRenderer.dispose();
		shapeRenderer.dispose();
		
		menuDocument1.dispose();
		menuDocument2.dispose();
		arrow.dispose();
		dialogueBox.dispose();
	}
	
	public void setInConversation(boolean setter) {
		inConversation = setter;
	}
}
