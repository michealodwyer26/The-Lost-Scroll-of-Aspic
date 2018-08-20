package entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import lost.scroll.of.aspic.RpgGame;

public class FemaleVillager extends Sprite {
	
	public enum State {EAST, WEST, NORTH, SOUTH,
						STANDING_NORTH, STANDING_SOUTH,
						STANDING_EAST, STANDING_WEST};
					
	private RpgGame game;
	
	private State currentState, previousState;
	private float stateTimer;
	
	private ArrayList<Rectangle> mapObjectsRects;
	private Rectangle playerRect;
	
	private static Texture spriteSheet = new Texture("data/sprites/femaleVillager.png");
	private TextureRegion region;
	private Animation<TextureRegion> walkEast, walkWest, walkNorth, walkSouth;
	private TextureRegion standEast, standWest, standNorth, standSouth;
	
	private float prevPosX, prevPosY;
	public boolean walkingSouth, walkingNorth, walkingEast, walkingWest;
	
	public Rectangle rect;
	
	private int stateIndex;
	private float frameDuration;
	private int tilesPerSecond;
	
	public FemaleVillager(RpgGame game, ArrayList<Rectangle> mapObjectsRects, Rectangle playerRect) {
		super(spriteSheet);
		
		this.game = game;
		this.mapObjectsRects = mapObjectsRects;
		this.playerRect = playerRect;
		
		currentState = State.STANDING_NORTH;
		previousState = currentState;
		
		frameDuration = 0.25f;
		tilesPerSecond = 2;
		
		standWest = new TextureRegion(getTexture(), 38, 32, 20, 32);
		standEast = new TextureRegion(getTexture(), 102, 32, 20, 32);
		standNorth = new TextureRegion(getTexture(), 1, 0, 29, 32);
		standSouth = new TextureRegion(getTexture(), 64, 0, 32, 32);
		
		Array<TextureRegion> frames = new Array<TextureRegion>();

		frames.add(new TextureRegion(getTexture(), 32, 0, 29, 32));
		frames.add(standNorth);
		
		walkNorth = new Animation<TextureRegion>(frameDuration, frames);
		frames.clear();
		
		frames.add(new TextureRegion(getTexture(), 96, 0, 32, 32));
		frames.add(standSouth);
		
		walkSouth = new Animation<TextureRegion>(frameDuration, frames);
		frames.clear();
		
		frames.add(new TextureRegion(getTexture(), 4, 32, 25, 32));
		frames.add(standWest);

		
		walkWest = new Animation<TextureRegion>(frameDuration, frames);
		frames.clear();
		
		frames.add(new TextureRegion(getTexture(), 67, 32, 25, 32));
		frames.add(standEast);

		walkEast = new Animation<TextureRegion>(frameDuration, frames);
		frames.clear();
		
		float width = (float) RpgGame.TILE_SIZE;
		float height = (float) RpgGame.TILE_SIZE;
		
		float startX = (float) RpgGame.TILE_SIZE * 2;
		float startY = (float) RpgGame.TILE_SIZE * 4;
		
		setBounds(startX, startY, width, height);
		
		rect = new Rectangle(getX(), getY(), width, height);
		
		prevPosX = getX();
		prevPosY = getY();
		
		stateIndex = 0;
		
		setPosition(prevPosX, prevPosY);
	}
	
	public void update(float dt) {
		region = getFrame(dt);
		
		setRegion(region);
		
		
		// Handle all the walking variables and system
		setWalkingVariables();
		
		boolean isWalking = (walkingSouth || walkingNorth || walkingEast || walkingWest);
		
		if(isWalking) {
			
			// North
			
			if(getY() > prevPosY + (RpgGame.TILE_SIZE)) {
				setPosition(prevPosX, prevPosY + (RpgGame.TILE_SIZE));
				walkingNorth = false;
				stateIndex++;
				prevPosY += RpgGame.TILE_SIZE;
			}
			
			// South
			
			if(getY() < prevPosY - (RpgGame.TILE_SIZE)) {
				setPosition(prevPosX, prevPosY - (RpgGame.TILE_SIZE));
				
				walkingSouth = false;
				stateIndex++;
				prevPosY -= RpgGame.TILE_SIZE;
			}
			
			// East
			
			if(getX() > prevPosX + (RpgGame.TILE_SIZE)) {
				setPosition(prevPosX + (RpgGame.TILE_SIZE), prevPosY);
				
				walkingEast = false;
				stateIndex++;
				prevPosX += RpgGame.TILE_SIZE;
			}
			
			// West
			
			if(getX() < prevPosX - (RpgGame.TILE_SIZE)) {
				setPosition(prevPosX - (RpgGame.TILE_SIZE), prevPosY);
				walkingWest = false;
				stateIndex++;
				prevPosX -= RpgGame.TILE_SIZE;
			}
			
			rect.x = getX();
			rect.y = getY();
		}
		
		if(!isWalking) {
			if(stateTimer > 4f && stateTimer < 4.1f){
				stateIndex++;
			}
		}
				
		if(stateIndex == 4 || stateTimer > 10f) {
			stateIndex = 0;
		}
		
	}
	
	public void setWalkingVariables() {
		if(stateIndex == 0) {
			walkingSouth = true;
		}
		else if(stateIndex == 2) {
			walkingNorth = true;
		}
		
		checkCollisions();
	}
	
	public TextureRegion getFrame(float dt) {
		currentState = getState();
		
		float increment = dt * RpgGame.TILE_SIZE * tilesPerSecond;
		
		switch(currentState) {
			case NORTH:
				region = (TextureRegion) walkNorth.getKeyFrame(stateTimer, true);
				translate(0, increment);
				break;
				
			case SOUTH:
				region = (TextureRegion) walkSouth.getKeyFrame(stateTimer, true);
				translate(0, -increment);
				break;
				
			case EAST:
				region = (TextureRegion) walkEast.getKeyFrame(stateTimer, true);
				translate(increment, 0);
				break;
				
			case WEST:
				region = (TextureRegion) walkWest.getKeyFrame(stateTimer, true);
				translate(-increment, 0);
				break;
				
			case STANDING_NORTH:
				region = standNorth;
				break;
				
			case STANDING_SOUTH:
				region = standSouth;
				break;
				
			case STANDING_EAST:
				region = standEast;
				break;
				
			case STANDING_WEST:
				region = standWest;
				break;
			
		}
		
		stateTimer = currentState == previousState ? stateTimer += dt : 0f;
		previousState = currentState;
		return region;
	}
	
	private State getState() {
		if(walkingSouth) {
			return State.SOUTH;
		} else if(walkingNorth) {
			return State.NORTH;
		} else if(walkingEast) {
			return State.EAST;
		} else if(walkingWest) {
			return State.WEST;
		}
		
		else if(previousState == State.SOUTH || previousState == State.STANDING_SOUTH){
			return State.STANDING_SOUTH;
		} else if(previousState == State.NORTH || previousState == State.STANDING_NORTH){
			return State.STANDING_NORTH;
		} else if(previousState == State.EAST || previousState == State.STANDING_EAST) {
			return State.STANDING_EAST;
		} else if(previousState == State.WEST || previousState == State.STANDING_WEST) {
			return State.STANDING_WEST;
		} else {
			return State.STANDING_SOUTH;
		} 
	}
	
	public void checkCollisions() {
		for(Rectangle rect : mapObjectsRects) {
			if(Intersector.overlaps(rect, this.rect)) {
				reposition();
			}
		}
		
		if(Intersector.overlaps(playerRect, this.rect)) {
			reposition();
		}
	}
	
	public void reposition() {
		setPosition(prevPosX, prevPosY);
		
		if(walkingSouth) walkingSouth = false;
		else if(walkingNorth) walkingNorth = false;
		else if(walkingEast) walkingEast = false;
		else if(walkingWest) walkingWest = false;
	}
	
	public void dispose() {
	}
}
