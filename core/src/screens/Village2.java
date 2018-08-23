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

import entities.Door;
import entities.Player;
import lost.scroll.of.aspic.RpgGame;
import tools.TiledMapRendererWithSprites;

public class Village2 implements Screen {

	private RpgGame game;
	private TiledMap map;
	private OrthographicCamera gamecam;
	private TiledMapRendererWithSprites mapRenderer;
	private Viewport gamePort;
	private Player player;
	private ShapeRenderer shapeRenderer;
	
	public Door door1;
	public Door door2;
	public Door door3;
	
	private Texture documentText;
	private Texture menuDocument1;
	private Texture menuDocument2;
	private Texture arrow;
	private Texture dialogueBox;
	private TextureRegion playerTexture;
	
	private ArrayList<Rectangle> mapObjectsRects;
	private ArrayList<Rectangle> specialMapObjectsRects;
	
	private final float maxCamX = RpgGame.TILE_SIZE * 27.5f;
	private final float maxCamY = RpgGame.TILE_SIZE * 44.5f;
	
	private boolean debugLines;
	private float documentDisplayTime;
	private boolean displayTownDocument;
	private int arrowPositionInMenu;
	
	private boolean displayMenu;
	private boolean displayDialogueBox;
	
	private String dialogueText;
	private String location;
	private String[] menuOptions;
	private String[] menuText;
	
	private float transitionCircleRadius;
	
	public Village2(RpgGame game, float startX, float startY, String direction) {
		this.game = game;
		
		map = new TmxMapLoader().load("data/maps/village2.tmx");
		gamecam = new OrthographicCamera();
		gamePort = new FitViewport(RpgGame.V_WIDTH, RpgGame.V_HEIGHT, gamecam);
		player = new Player(game, startX, startY, direction);
		
		// door1 = x: 80, y: 112 (brown)
		// door2 = x: 112, y: 512 (red) 
		// door3 = x: 448, y: 144 (red)
		
		door1 = new Door(game, 80, 128, "brown");
		door2 = new Door(game, 112, 528, "red");
		door3 = new Door(game, 448, 160, "red");
		
		mapRenderer = new TiledMapRendererWithSprites(map);
		mapRenderer.addSprite(player);
		mapRenderer.addSprite(door1);
		mapRenderer.addSprite(door2);
		mapRenderer.addSprite(door3);
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
		
		documentText = new Texture(Gdx.files.internal("data/misc/document.png"));
		menuDocument1 = new Texture(Gdx.files.internal("data/misc/menuDocument2.png"));
		menuDocument2 = new Texture(Gdx.files.internal("data/misc/menuDocument1.png"));
		arrow = new Texture(Gdx.files.internal("data/misc/fancyarrow.png"));
		dialogueBox = new Texture(Gdx.files.internal("data/misc/dialoguebox.png"));
		
		documentDisplayTime = 0f;
		
		displayTownDocument = true;
		debugLines = false;
		displayMenu = false;
		arrowPositionInMenu = 0;
		
		location = "Village of Vladi";
		
		menuOptions = new String[2];
		menuOptions[0] = "Inventory";
		menuOptions[1] = "Back";
		
		menuText = new String[2];
		menuText[0] = "You do not have any items.";

		playerTexture = new TextureRegion(player.getTexture(), 102, 32, 20, 32);
		
		game.font15.setColor(Color.BLACK);
		
		transitionCircleRadius = 150f;
		
		if(startX == 80f) {
			door1.close();
		} else if(startX == 112f) {
			door2.close();
		} else if(startX == 448f) {
			door3.close();
		}
	}
	
	public void handleInput() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {
			displayMenu = displayMenu ? false : true;			
			game.font10.setColor(Color.BLACK);
			arrowPositionInMenu = 56;
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
			door1.close();
		}
		
		if(!displayMenu && !player.isWalkingToDestination) {
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
			
			if(Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
				displayTownDocument = displayTownDocument ? false : true;
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
				displayDialogueBox = false;
			}
		}
	}
	
	public void checkCamBounds() {
		if(gamecam.position.x < gamecam.viewportWidth / 2) {
			gamecam.position.x = gamecam.viewportWidth / 2;
		}
		
		if(gamecam.position.y < gamecam.viewportHeight / 2) {
			gamecam.position.y = gamecam.viewportHeight / 2;
		}
		
		if(gamecam.position.x > maxCamX) {
			gamecam.position.x = maxCamX;
		}
		
		if(gamecam.position.y > maxCamY) {
			gamecam.position.y = maxCamY;
		}
	}
	
	
	
	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		handleInput();
		
		player.update(delta, mapObjectsRects);
		door1.update(delta);
		door2.update(delta);
		door3.update(delta);
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(!player.isWalkingToDestination) {
			player.checkCollisions(mapObjectsRects, specialMapObjectsRects, null);
		}
		
		else {
			if(player.getWalkingToDestinationTimer() > RpgGame.SCREEN_TRANSITION) {	
				if(player.destination.equals("Overworld")) {
					game.setScreen(new Overworld(game, 592f, 640f));
				}
				
				else if(player.destination.equals("Sultan")) {
					game.setScreen(new Village2SultanHouse(game));
				}
			}
			else if(player.getWalkingToDestinationTimer() > RpgGame.FAST_SCREEN_TRANSITION) {
				if(player.destination.equals("House 1")) {
					game.setScreen(new Village2House1(game));
				}
				
				else if(player.destination.equals("House 2")) {
					game.setScreen(new Village2House2(game));
				}
				
				else if(player.destination.equals("House 4")) {
					game.setScreen(new Village2House3(game));
				}
			}
		}
		
		gamecam.position.x = player.getX();
		gamecam.position.y = player.getY();
		
		checkCamBounds();
		
		gamecam.update();
		
		mapRenderer.setView(gamecam);
		mapRenderer.render();		
		
		if(displayDialogueBox) {
			displayTownDocument = false;
		}
		
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
			
			shapeRenderer.end();
		}
		
		if(displayTownDocument) {
			game.batch.begin();
			game.batch.draw(documentText, RpgGame.LOCATION_DOC_X, RpgGame.LOCATION_DOC_Y, documentText.getWidth() + 90, documentText.getHeight());
			
			game.font15.draw(game.batch, location, RpgGame.LOCATION_TEXT_X, RpgGame.LOCATION_TEXT_Y);
			
			game.batch.end();
			
			if(documentDisplayTime < RpgGame.LOCATION_DOC_TIME) {
								
				documentDisplayTime += delta;
				
				if(documentDisplayTime > RpgGame.LOCATION_DOC_TIME) {
					displayTownDocument = false;
				}
			}
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
			game.font10.draw(game.batch, dialogueText, RpgGame.DIALOGUE_TEXT_X, RpgGame.DIALOGUE_TEXT_Y);
			game.batch.draw(arrow, RpgGame.DIALOGUE_ARROW_X, RpgGame.DIALOGUE_ARROW_Y, RpgGame.DIALOGUE_ARROW_WIDTH, RpgGame.DIALOGUE_ARROW_HEIGHT);
			
			game.batch.end();
		}
		
		if(transitionCircleRadius > 0 && !player.isWalkingToDestination) {
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setProjectionMatrix(gamecam.combined);
			
			shapeRenderer.setColor(0, 0, 0, 0);
			shapeRenderer.circle(player.getX() + 8, player.getY() + 8, transitionCircleRadius);
			
			transitionCircleRadius -= RpgGame.TRANSITION_RADIUS_FAST_INCREMENT;
			
			shapeRenderer.end();
		}
		if(player.isWalkingToDestination) {
			if(player.destination.equals("Sultan")) {
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setProjectionMatrix(gamecam.combined);
				
				shapeRenderer.setColor(0, 0, 0, 0);
				shapeRenderer.circle(player.getX() + 8, player.getY() + 8, transitionCircleRadius);
				
				transitionCircleRadius += RpgGame.TRANSITION_RADIUS_INCREMENT;
				
				shapeRenderer.end();
			}
		}
	}
	
	public void displayDialogue(String text) {
		displayDialogueBox = true;
		dialogueText = text;
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
		
		documentText.dispose();
		menuDocument1.dispose();
		menuDocument2.dispose();
		arrow.dispose();
		dialogueBox.dispose();
	}

}
