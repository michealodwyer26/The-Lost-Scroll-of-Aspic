package entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import screens.Overworld;
import screens.Village1;
import screens.Village1House1;
import screens.Village1House2;
import screens.Village1SultanHouse;
import screens.Village2;
import screens.Village2House1;
import screens.Village2House2;
import screens.Village2House3;
import screens.Village2SultanHouse;
import lost.scroll.of.aspic.RpgGame;

public class Player extends Sprite {
	public enum State { EAST, WEST, NORTH, SOUTH, 
						STANDING_NORTH,  STANDING_SOUTH, 
						STANDING_EAST, STANDING_WEST };
	
	private RpgGame game;
	
	private State currentState, previousState;
	private float stateTimer, walkingToDestinationTimer;
	
	public static Texture spriteSheet = new Texture("data/sprites/player.png");
	private TextureRegion region;
	private Animation<TextureRegion> walkEast, walkWest, walkNorth, walkSouth;
	private TextureRegion standEast, standWest, standNorth, standSouth;
	
	private float prevPosX, prevPosY;
	
	public boolean walkingSouth, walkingNorth, walkingEast, walkingWest, isWalkingToDestination;
	public String destination;
	public Rectangle rect;
	
	private float slowFrameDuration, fastFrameDuration;
	private int tilesPerSecond;
	
	public Player(RpgGame game, float startX, float startY, String direction) {
		super(spriteSheet);
		
		this.game = game;
		
		if(direction.equals("north")) {
			currentState = State.STANDING_NORTH;
		} else if(direction.equals("south")) {
			currentState = State.STANDING_SOUTH;
		}
		
		previousState = currentState;
		
		standWest = new TextureRegion(getTexture(), 32, 32, 32, 32);
		standEast = new TextureRegion(getTexture(), 96, 32, 32, 32);
		standNorth = new TextureRegion(getTexture(), 0, 0, 32, 32);
		standSouth = new TextureRegion(getTexture(), 64, 0, 32, 32);
		
		slowFrameDuration = 0.25f;
		fastFrameDuration = 0.25f / 2;
		
		tilesPerSecond = 2;
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		
		frames.add(new TextureRegion(getTexture(), 32, 0, 32, 32));
		frames.add(standNorth);
		
		walkNorth = new Animation<TextureRegion>(slowFrameDuration, frames);
		frames.clear();
		
		frames.add(new TextureRegion(getTexture(), 96, 0, 32, 32));
		frames.add(standSouth);
		
		walkSouth = new Animation<TextureRegion>(slowFrameDuration, frames);
		frames.clear();
		

		frames.add(new TextureRegion(getTexture(), 0, 32, 32, 32));
		frames.add(standWest);

		walkWest = new Animation<TextureRegion>(slowFrameDuration, frames);
		frames.clear();
		
		frames.add(new TextureRegion(getTexture(), 64, 32, 32, 32));
		frames.add(standEast);
		
		walkEast = new Animation<TextureRegion>(slowFrameDuration, frames);
		frames.clear();
		
		float width = (float) RpgGame.TILE_SIZE;
		float height = (float) RpgGame.TILE_SIZE;
		
		setBounds(startX, startY, width, height);
		
		rect = new Rectangle(getX(), getY(), width, height);
		
		prevPosX = startX;
		prevPosY = startY;
		
		isWalkingToDestination = false;
	}
	
	public void update(float dt, ArrayList<Rectangle> mapObjects) {
		region = getFrame(dt);
		setRegion(region);
		
		// Handle all the walking variables and the walking system
	
		boolean isWalking = (walkingSouth || walkingNorth || walkingEast || walkingWest) ? true : false;

		if(isWalking) {
			// North
			if(getY() > prevPosY + RpgGame.TILE_SIZE) {
				setPosition(prevPosX, prevPosY + RpgGame.TILE_SIZE);
				
				walkingNorth = false;
				prevPosY += RpgGame.TILE_SIZE;
				
				boolean walkAnotherTile = Gdx.input.isKeyPressed(Input.Keys.UP);
				
				if(walkAnotherTile) {
					walkingNorth = true;
					stateTimer = 0;
				}
			}
			// South
			if(getY() < prevPosY - RpgGame.TILE_SIZE) {
				setPosition(prevPosX, prevPosY - RpgGame.TILE_SIZE);
				
				walkingSouth = false;
				prevPosY -= RpgGame.TILE_SIZE;
				
				boolean walkAnotherTile = Gdx.input.isKeyPressed(Input.Keys.DOWN);
				
				if(walkAnotherTile) {
					walkingSouth = true;
					stateTimer = 0;
				}
			}
			// East
			if(getX() > prevPosX + RpgGame.TILE_SIZE) {
				setPosition(prevPosX + RpgGame.TILE_SIZE, prevPosY);
				
				walkingEast = false;
				prevPosX += RpgGame.TILE_SIZE;
				
				boolean walkAnotherTile = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
				
				if(walkAnotherTile) {
					walkingEast = true;
					stateTimer = 0;
				}
			}
			// West
			if(getX() < prevPosX - RpgGame.TILE_SIZE) {
				setPosition(prevPosX - RpgGame.TILE_SIZE, prevPosY);
				
				walkingWest = false;
				prevPosX -= RpgGame.TILE_SIZE;
				
				boolean walkAnotherTile = Gdx.input.isKeyPressed(Input.Keys.LEFT);
				
				if(walkAnotherTile) {
					walkingWest = true;
					stateTimer = 0;
				}
			}
		}
		
		rect.x = getX();
		rect.y = getY();
	}
	
	private TextureRegion getFrame(float dt) {
		currentState = getState();
		
		float fastIncrement = dt * RpgGame.TILE_SIZE * tilesPerSecond * 2;
		float slowIncrement = dt * RpgGame.TILE_SIZE * tilesPerSecond;
		
		float increment = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? fastIncrement : slowIncrement;
		
		if(isWalkingToDestination) {
			
			if(game.getScreen() instanceof Village1) {
				if(destination.equals("House 1") || destination.equals("House 2") || destination.equals("Sultan")) {
					currentState = State.NORTH;
				}
				else {
					currentState = State.SOUTH;
				}
			}
			else if(game.getScreen() instanceof Village1House1 || game.getScreen() instanceof Village1House2 || game.getScreen() instanceof Village1SultanHouse) {
				currentState = State.SOUTH;
			}
			
			else if(game.getScreen() instanceof Overworld) {
				currentState = State.NORTH;
			}
			
			else if(game.getScreen() instanceof Village2) {
				if(destination.equals("House 1") || destination.equals("House 2") || destination.equals("House 4") || destination.equals("Sultan")) {
					currentState = State.NORTH;
				}
				else {
					currentState = State.SOUTH;
				}
			}
			
			else if(game.getScreen() instanceof Village2House1 || game.getScreen() instanceof Village2House2 || game.getScreen() instanceof Village2House3 ||game.getScreen() instanceof Village2SultanHouse) {
				currentState = State.SOUTH;
			}
			
			
			increment = dt * 32;
		}
		
		// End block
		
		
		switch(currentState) {
		
			case NORTH:
				
				if(increment == fastIncrement && walkNorth.getFrameDuration() == slowFrameDuration)
					walkNorth.setFrameDuration(fastFrameDuration);
				else if(increment == slowIncrement && walkNorth.getFrameDuration() == fastFrameDuration)
					walkNorth.setFrameDuration(slowFrameDuration);
				
				region = (TextureRegion) walkNorth.getKeyFrame(stateTimer);
				translate(0, increment);
				break;
				
			case SOUTH:
				
				if(increment == fastIncrement && walkSouth.getFrameDuration() == slowFrameDuration)
					walkSouth.setFrameDuration(fastFrameDuration);
				else if(increment == slowIncrement && walkSouth.getFrameDuration() == fastFrameDuration)
					walkSouth.setFrameDuration(slowFrameDuration);
				
				region = (TextureRegion) walkSouth.getKeyFrame(stateTimer);
				translate(0, -increment);
				break;
				
			case EAST:
				
				if(increment == fastIncrement && walkEast.getFrameDuration() == slowFrameDuration)
					walkEast.setFrameDuration(fastFrameDuration);
				else if(increment == slowIncrement && walkEast.getFrameDuration() == fastFrameDuration)
					walkEast.setFrameDuration(slowFrameDuration);

				region = (TextureRegion) walkEast.getKeyFrame(stateTimer);
				translate(increment, 0);
				break;
			
			case WEST:
				
				if(increment == fastIncrement && walkWest.getFrameDuration() == slowFrameDuration)
					walkWest.setFrameDuration(fastFrameDuration);
				else if(increment == slowIncrement && walkWest.getFrameDuration() == fastFrameDuration)
					walkWest.setFrameDuration(slowFrameDuration);
				
				region = (TextureRegion) walkWest.getKeyFrame(stateTimer);
				translate(-increment, 0);
				break;
				
			case STANDING_SOUTH:
				region = standSouth;
				break;
			
			case STANDING_NORTH:
				region = standNorth;
				break;
				
			case STANDING_EAST:
				region = standEast;
				break;
				
			case STANDING_WEST:
				region = standWest;
				break;
				
				
			default:
				region = standSouth;
				break;
		}

		stateTimer = currentState == previousState ? stateTimer + dt : 0;
		
		if(isWalkingToDestination) {
			walkingToDestinationTimer += dt;
		}
		
		previousState = currentState;
		
		return region;
	}
	
	private State getState() {
		if(walkingSouth) return State.SOUTH;
		else if(walkingNorth) return State.NORTH;
		else if(walkingEast) return State.EAST;
		else if(walkingWest) return State.WEST;
			
			
		else if(previousState == State.SOUTH || previousState == State.STANDING_SOUTH)
			return State.STANDING_SOUTH;
		else if(previousState == State.NORTH || previousState == State.STANDING_NORTH)
			return State.STANDING_NORTH;
		else if(previousState == State.EAST || previousState == State.STANDING_EAST)
			return State.STANDING_EAST;
		else if(previousState == State.WEST || previousState == State.STANDING_WEST)
			return State.STANDING_WEST;
		else 
			return State.STANDING_SOUTH;
	}
	
	public void checkCollisions(ArrayList<Rectangle> objectRects, ArrayList<Rectangle> specialMapObjectsRects, Rectangle npcRect) {
		for(Rectangle rect : objectRects) {
			if(Intersector.overlaps(rect, this.rect)) {
				reposition();
			}
		}
		
		if(npcRect != null) {
			if(game.getScreen() instanceof Village1House1) {
				if(Intersector.overlaps(npcRect, this.rect)) {
					reposition();
						
					Village1House1 house1 = (Village1House1) game.getScreen();
					house1.displayDialogue("Who Are The Brain Police?");
				}
			}
			
			if(game.getScreen() instanceof Village1House2) {
				if(Intersector.overlaps(npcRect, this.rect)) {
					reposition();
						
					Village1House2 house2 = (Village1House2) game.getScreen();
					house2.displayDialogue("Help, I'm a Rock!");
				}
			}
		
			
			if(game.getScreen() instanceof Village1SultanHouse) {
				if(Intersector.overlaps(npcRect, this.rect)) {
					reposition();
					
					Village1SultanHouse sultanHouse = (Village1SultanHouse) game.getScreen();
					
					if(game.stage == 1) {
						sultanHouse.displayDialogue(sultanHouse.allDialogueText[0]);
						sultanHouse.setInConversation(true);
					}
					
					if(game.stage == 2) {
						sultanHouse.displayDialogue(sultanHouse.allDialogueText[6]);
					}
					
					if(game.stage == 3) {
						sultanHouse.displayDialogue(sultanHouse.allDialogueText[7]);
						sultanHouse.setInConversation(true);
					}
					
					if(game.stage == 4) {
						sultanHouse.displayDialogue(sultanHouse.allDialogueText[11]);
					}
				}
			}
			
			if(game.getScreen() instanceof Village2House1) {
				if(Intersector.overlaps(npcRect, this.rect)) {
					reposition();
					
					Village2House1 house = (Village2House1) game.getScreen();
					house.displayDialogue("The Sultan hates your kind! Get away from me, you\nPopescan!");
				}
			}
			
			if(game.getScreen() instanceof Village2House2) {
				if(Intersector.overlaps(npcRect, this.rect)) {
					reposition();
					
					Village2House2 house = (Village2House2) game.getScreen();
					house.displayDialogue("No Popescan has a mind of their own! How did you\nventure all the way here? Be damned mere fool!");
					
				}
			}
			
			if(game.getScreen() instanceof Village2House3) {
				if(Intersector.overlaps(npcRect, this.rect)) {
					reposition();
					
					Village2House3 house = (Village2House3) game.getScreen();
					house.displayDialogue("I had been awaiting your arrival for some time...");
				}
			}
			
			if(game.getScreen() instanceof Village2SultanHouse) {
				if(Intersector.overlaps(npcRect, this.rect)) {
					reposition();
					
					Village2SultanHouse house = (Village2SultanHouse) game.getScreen();
					
					if(game.stage == 1) {
						house.displayDialogue(house.allDialogueText[0]);
					}
					
					if(game.stage == 2) {
						house.displayDialogue(house.allDialogueText[1]);
						house.setInConversation(true);
					}
					
					if(game.stage == 3) {
						house.displayDialogue(house.allDialogueText[6]);
					}
					
				}
			}
		}
		
		for(Rectangle rect : specialMapObjectsRects) {
			if(Intersector.overlaps(rect, this.rect)) {
				reposition();
				
				if(game.getScreen() instanceof Village1) {
					if(rect.y == 256.0f && Gdx.input.isKeyPressed(Input.Keys.UP)) {
						setDestination("Sultan");
					}
					
					if(rect.y == 96.0f && Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
						setDestination("House 1");
						Village1 screen = (Village1) game.getScreen();
						screen.door1.open();
					}
					
					if(rect.y == 48.0f && Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
						setDestination("House 2");
						Village1 screen = (Village1) game.getScreen();
						screen.door1.open();
					}
					
					if(rect.y == -16.0f && Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
						setDestination("Overworld");
					}
					
					if(rect.x == 80f && rect.y == 240.0f) {
						Village1 screen = (Village1) game.getScreen();
						screen.displayDialogue("This is the Sultan Popescu's house.");
						
					}
					
					if(rect.x == 160f && rect.y == 32.0f) {
						Village1 screen = (Village1) game.getScreen();
						screen.displayDialogue("Welcome to the Village of Popescu.");
					}
					
					if(rect.x == 32.0 && rect.y == 32.0) {
						Village1 screen = (Village1) game.getScreen();
						screen.displayDialogue("Looks like the house is locked...");
					}
				}
				
				if(game.getScreen() instanceof Village1House1) {
					if(rect.x == 0f) {
						Village1House1 house1 = (Village1House1) game.getScreen();
						house1.displayDialogue("There is some sauerkraut left here...");
					}
					
					if(rect.x == 64f && rect.y == 80f) {
						Village1House1 house1 = (Village1House1) game.getScreen();
						house1.displayDialogue("Stop looking at other people's stuff...");
					}
					
					if(rect.x == 96f && rect.y == 80f) {
						Village1House1 house1 = (Village1House1) game.getScreen();
						house1.displayDialogue("There is a book by Camus inside...");
					}
					
					if(rect.x == 96f && rect.y == 0f) {
						Village1House1 house1 = (Village1House1) game.getScreen();
						house1.displayDialogue("There seems to be nothing inside...");
					}
					
					if(rect.y == -16f && Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
						setDestination("Village");
					}
				}
				
				if(game.getScreen() instanceof Village1House2) {
					if(rect.x == 64) {
						Village1House2 house2 = (Village1House2) game.getScreen();
						house2.displayDialogue("There are some brown shoes and\npython boots inside...");
					}
					
					if(rect.x == 96 && rect.y == 96f) {
						Village1House2 house2 = (Village1House2) game.getScreen();
						house2.displayDialogue("There is a book by Barry Burd inside...");
					}
					
					if(rect.x == 96 && rect.y == 48f) {
						Village1House2 house2 = (Village1House2) game.getScreen();
						house2.displayDialogue("Someone was cruising for burgers\nthe other day...");
					}
					
					if(rect.x == 112) {
						Village1House2 house2 = (Village1House2) game.getScreen();
						house2.displayDialogue("It is just some wheat...");
					}
					
					if(rect.y == -16 && Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
						setDestination("Village");
					}
				}
				
				if(game.getScreen() instanceof Village1SultanHouse) {					
					if(rect.y == -16.0f) {
						setDestination("Village");
					}
					
					if(rect.y == 240f) {
						Village1SultanHouse sultanHouse = (Village1SultanHouse) game.getScreen();
						sultanHouse.displayDialogue("I wouldn't go touch the Sultan's stuff...");
					}
				}
				
				if(game.getScreen() instanceof Overworld) {
					if(rect.x == 624 && Gdx.input.isKeyPressed(Input.Keys.UP)) {
						setDestination("Village1");
					}
					if(rect.x == 608 && rect.y == 80) {
						Overworld overworld = (Overworld) game.getScreen();
						overworld.displayDialogue("Village of Popescu");
					}
					if(rect.x == 576 || rect.x == 608 && rect.y == 640) {
						Overworld overworld = (Overworld) game.getScreen();
						overworld.displayDialogue("Village of Vladi");
					}
					
					if(rect.x == 592 && Gdx.input.isKeyPressed(Input.Keys.UP)) {
						setDestination("Village2");
					}
				}
				
				if(game.getScreen() instanceof Village2) {
					if(rect.x == 224f && rect.y == 64f) {
						Village2 village2 = (Village2) game.getScreen();
						village2.displayDialogue("Welcome to the Village of Vladi");
					}
					
					if(rect.x == 80f && rect.y == 128f && Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
						setDestination("House 1");
					}
					
					if(rect.x == 112f && rect.y == 528f && Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
						setDestination("House 2");
					}
					
					if(rect.x == 336f && rect.y == 528f && Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
						Village2 village = (Village2) game.getScreen();
						village.displayDialogue("The house seems to be locked...");
					}
					
					if(rect.x == 448f && rect.y == 160f && Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
						setDestination("House 4");
					}
					
					if(rect.x == 224f && rect.y == 656f && Gdx.input.isKeyPressed(Input.Keys.UP)) {
						setDestination("Sultan");
					}
					
					if(rect.y == -16f && Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
						setDestination("Overworld");
					}
				}
				
				if(game.getScreen() instanceof Village2House1) {					
					if(rect.x == 48f && rect.y == 96f) {
						Village2House1 house = (Village2House1) game.getScreen();
						house.displayDialogue("There is a DVD of Taxi Driver inside...");
					}
					
					if(rect.x == 32f && rect.y == 96f) {
						Village2House1 house = (Village2House1) game.getScreen();
						house.displayDialogue("There doesn't seem to be much inside...");
					}
					
					if(rect.x == 0f && rect.y == 48f) {
						Village2House1 house = (Village2House1) game.getScreen();
						house.displayDialogue("It's just some barley");
					}
					
					if(rect.x == 0f && rect.y == 0f) {
						Village2House1 house = (Village2House1) game.getScreen();
						house.displayDialogue("It is just some wheat");
					}
					
					if(rect.x == 16f && rect.y == -16f && Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
						setDestination("Village 2");
					}
				}
				
				if(game.getScreen() instanceof Village2House2) {					
					if(rect.x == 96f) {
						Village2House2 house = (Village2House2) game.getScreen();
						house.displayDialogue("There is a book called Steppenwolf inside...");
					}
					
					if(rect.x == 64f) {
						Village2House2 house = (Village2House2) game.getScreen();
						house.displayDialogue("Just some pots and pans...");
					}
					
					if(rect.x == 16f && Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
						setDestination("Village 2");
					}
				}
				
				if(game.getScreen() instanceof Village2SultanHouse) {					
					if(rect.x == 112f && rect.y == -16f && Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
						setDestination("Village 2");
					}
					
					if(rect.y == 240f) {
						Village2SultanHouse house = (Village2SultanHouse) game.getScreen();
						house.displayDialogue("I wouldn't go touching that if I were you...");
					}
				}
				
				if(game.getScreen() instanceof Village2House3) {					
					if(rect.y == -16f && Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
						setDestination("Village 2");
					}
					
					if(rect.x == 96f && rect.y == 96f) {
						Village2House3 house = (Village2House3) game.getScreen();
						house.displayDialogue("There is a book by Al Steigwart inside...");
					}
					
					if(rect.x == 64f && rect.y == 96f) {
						Village2House3 house = (Village2House3) game.getScreen();
						house.displayDialogue("There doesn't seem to be much here...");
					}	
				}
			}
		}
	}
	
	public float getWalkingToDestinationTimer() {
		return walkingToDestinationTimer;
	}
	
	public void reposition() {
		if(!isWalkingToDestination) {
			setPosition(prevPosX, prevPosY);
		}
		
		if(walkingSouth) walkingSouth = false;
		else if(walkingNorth) walkingNorth = false;
		else if(walkingEast) walkingEast = false;
		else if(walkingWest) walkingWest = false;
	}
	
	public void setDestination(String destination) {
		isWalkingToDestination = true;
		this.destination = destination;
	}
}