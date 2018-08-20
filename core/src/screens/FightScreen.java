package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import entities.Player;
import lost.scroll.of.aspic.RpgGame;

public class FightScreen implements Screen {

	private RpgGame game;
	private Viewport gamePort;
	
	private ShapeRenderer shapeRenderer;
	
	private Texture documentText;
	private Texture optionBox;
	private Texture arrow;
	
	private TextureRegion playerTexture;
	private TextureRegion devilTexture;
	
	private Texture sword;
	private Texture swordRotated;
	
	private Animation<TextureRegion> swordAnim;
	private Animation<TextureRegion> walkingLeftAnim;
	
	private float walkingTimer;
	
	private String dialogueText;
	
	private String[] menuOptions;
	private String[] menuText;
	
	private int arrowPositionInMenu;
	private int monsterX;
	private int monsterVel;
	
	private boolean playerAttacking;
	private boolean playerTurn;
	private boolean monsterAttacking;
	
	private float attackPlayerSequenceTimer;
	
	private int playerHealth = 100;
	private int monsterHealth = 25;
	
	private float overworldX;
	private float overworldY;
	
	private float playerX;
	private float fightingPlayerX;
	private float playerVel;
	
	private boolean playerWon;
	private boolean monsterWon;
	private boolean playerRunning;
	
	public FightScreen(RpgGame game, float overworldX, float overworldY) {
		this.game = game;
		
		this.overworldX = overworldX;
		this.overworldY = overworldY;
		
		gamePort = new FitViewport(RpgGame.V_WIDTH, RpgGame.V_HEIGHT, new OrthographicCamera());
		shapeRenderer = new ShapeRenderer();
				
		documentText = new Texture(Gdx.files.internal("data/misc/dialoguebox.png"));
		optionBox = new Texture(Gdx.files.internal("data/misc/menuDocument2.png"));
		arrow = new Texture(Gdx.files.internal("data/misc/fancyarrow.png"));
		
		playerTexture = new TextureRegion(Player.spriteSheet, 102, 32, 20, 32);
		devilTexture = new TextureRegion(new Texture(Gdx.files.internal("data/sprites/devil.png")), 64, 0, 32, 32);
		
		sword = new Texture(Gdx.files.internal("data/misc/sword2.png"));
		swordRotated = new Texture(Gdx.files.internal("data/misc/sword.png"));
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		
		for(int i = 0; i < 6; i++) {
			frames.add(new TextureRegion(sword, 3, 0, 18, 8));
			frames.add(new TextureRegion(swordRotated, 0, 1, 8, 18));
		}
		
		swordAnim = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 0; i < 5; i++) {
			frames.add(new TextureRegion(Player.spriteSheet, 67, 32, 25, 32));
			frames.add(new TextureRegion(Player.spriteSheet, 102, 32, 20, 32));
		}
		
		walkingLeftAnim = new Animation<TextureRegion>(0.125f, frames);
		frames.clear();
		
		dialogueText = "";
		
		menuOptions = new String[3];
		menuOptions[0] = "Fight";
		menuOptions[1] = "Items";
		menuOptions[2] = "Run";
		
		menuText = new String[3];
		menuText[0] = "You have no items...";
		menuText[1] = "Fight the monster";
		menuText[2] = "Run away";
		
		arrowPositionInMenu = 36;
		monsterX = 300;
		monsterVel = 2;
		
		playerAttacking = false;
		playerTurn = true;
		
		monsterAttacking = false;
		
		attackPlayerSequenceTimer = 0f;
		
		playerX = 0;
		fightingPlayerX = RpgGame.MENU_SPRITE_X + 30;
		playerVel = 5;
		
		playerWon = false;
		monsterWon = false;
		playerRunning = false;
		
		walkingTimer = 0f;
		
		game.font10.setColor(Color.BLACK);
	}
	
	@Override
	public void show() {
	}
	
	public void handleInput() {
		if(playerTurn) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
				if(arrowPositionInMenu > 16) {
					arrowPositionInMenu -= 20;
				}
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
				if(arrowPositionInMenu < 56) {
					arrowPositionInMenu += 20;
				}
			}
			
			if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				if(arrowPositionInMenu == 56 && !playerAttacking) {
					playerTurn = false;
					playerAttacking = true;
					attackPlayerSequenceTimer = 0f;
					dialogueText = "Your sword dealt 5 damage";
				}
				
				else if(arrowPositionInMenu == 16 && !playerAttacking) {
					playerTurn = false;
					playerRunning = true;
					playerTexture.flip(true, false); 
				}
			}
			
			if(Gdx.input.isKeyPressed(Input.Keys.P)) {
				monsterAttacking = true;
				playerTurn = false;
				playerAttacking = false;
			}
		}	
	}

	@Override
	public void render(float delta) {
		// Not checked if player won or lost
		if(!playerWon && !monsterWon) {
			handleInput();
			checkHealth();
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.batch.begin();
		
		
		// not drawn if player won or lost
		if(!playerWon && !monsterWon) {
			game.batch.draw(optionBox, RpgGame.MENU_DOC_1_X, RpgGame.MENU_DOC_1_Y, RpgGame.MENU_DOC_1_WIDTH, RpgGame.MENU_DOC_1_HEIGHT);
			game.batch.draw(arrow, RpgGame.MENU_ARROW_X, arrowPositionInMenu, RpgGame.MENU_ARROW_WIDTH, RpgGame.MENU_ARROW_HEIGHT);

			game.font10.draw(game.batch, menuOptions[0], RpgGame.MENU_OPTION_X, RpgGame.MENU_OPTION_1_Y);
			game.font10.draw(game.batch, menuOptions[1], RpgGame.MENU_OPTION_X, RpgGame.MENU_OPTION_2_Y);
			game.font10.draw(game.batch, menuOptions[2], RpgGame.MENU_OPTION_X, RpgGame.MENU_OPTION_3_Y);
			
		}
		
		// always drawed
		game.batch.draw(documentText, RpgGame.MENU_DOC_2_X, RpgGame.MENU_DOC_2_Y, 230, RpgGame.MENU_DOC_1_HEIGHT);
		
		if(!monsterAttacking && !playerWon && !monsterWon) {
			game.batch.draw(devilTexture, monsterX, 100);
		}
		
		if(!monsterWon && !playerWon && playerTurn) {
			switch(arrowPositionInMenu) {
				case 36:
					dialogueText = menuText[0];
					break;
					
				case 56:
					dialogueText = menuText[1];
					break;
				
				case 16:
					dialogueText = menuText[2];
			}
		}
		
		if(playerAttacking && !playerWon && !monsterWon) {
			game.batch.draw(swordAnim.getKeyFrame(attackPlayerSequenceTimer), 80, 120);
			attackPlayerSequenceTimer += delta;
			
			if(swordAnim.isAnimationFinished(attackPlayerSequenceTimer)) {
				playerAttacking = false;
				playerTurn = false;
				monsterHealth -= 5;
				monsterAttacking = true;
			}
		}
		
		
		if(monsterAttacking && !playerWon && !monsterWon) {
			monsterX -= monsterVel;
			game.batch.draw(devilTexture, monsterX, 100);
			if(monsterX < 250) {
				playerTurn = true;
				playerAttacking = false;
				monsterAttacking = false;
				playerHealth -= 5;
				dialogueText = "The monster dealt 5 damage";
			}
		}
		
		// Movements
		if(monsterX < 300 && !monsterAttacking) {
			monsterX += 2 * monsterVel;
		}
		
		if(playerX < fightingPlayerX) {
			playerX += playerVel;
			game.batch.draw(walkingLeftAnim.getKeyFrame(walkingTimer), playerX, RpgGame.MENU_SPRITE_Y, 75/2, 100/2);
			walkingTimer += delta;
		}
		
		if(playerWon) {
			playerX += playerVel;
			game.batch.draw(walkingLeftAnim.getKeyFrame(walkingTimer), playerX, RpgGame.MENU_SPRITE_Y, 75/2, 100/2);
			walkingTimer += delta;
		}
		
		else if(playerX == fightingPlayerX) {
			game.batch.draw(playerTexture, playerX, RpgGame.MENU_SPRITE_Y, 75/2, 100/2);
			walkingTimer = 0f;
		}
		
		if(playerRunning) {
			playerX -= 2*playerVel;
			if(playerX < 0) {
				game.setScreen(new Overworld(game, overworldX, overworldY));
			}
		}
		
		// Drawing the dialogue message
		game.font10.draw(game.batch, dialogueText, 170, 40);
		game.batch.end();
			
		
		if(!playerWon && !monsterWon) {
			shapeRenderer.setProjectionMatrix(gamePort.getCamera().combined);
			
			shapeRenderer.begin(ShapeType.Filled);
			
			shapeRenderer.setColor(0, 200, 0, 0);
			shapeRenderer.rect(-155, 45, playerHealth/4, 2);
			
			shapeRenderer.setColor(200, 0, 0, 0);
			shapeRenderer.rect(105, 35, monsterHealth, 2);
			shapeRenderer.end();
		}
		
		if(playerX > RpgGame.V_WIDTH) {
			game.setScreen(new Overworld(game, overworldX, overworldY));
		}
		
	}
	
	private void checkHealth() {
		if(monsterHealth <= 0) {
			if(monsterAttacking) {
				playerWon = true;
				monsterWon = true;
				dialogueText = "You slayed the beast!";
			}
		}
		
		else if(playerHealth <= 0) {
			if(playerTurn) {
				game.setScreen(new Village1(game, 8*16, 0*16, "north"));
			}
		}
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
		
	}

}
