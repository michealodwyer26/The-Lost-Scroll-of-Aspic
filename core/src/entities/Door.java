package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import lost.scroll.of.aspic.RpgGame;

public class Door extends Sprite {
		
	private RpgGame game;
	
	private TextureRegion region;
	private TextureRegion closed;
	private static Texture spriteSheet = new Texture("data/tilesets/animated/doorsCapture.png");
	
	public Rectangle rect;
	
	private Animation<TextureRegion> openingAnimation;
	
	private float x;
	private float y;
	
	private float animationTimer;
	private boolean isOpening;
	
	public Door(RpgGame game, float x, float y, String color) {
		super(spriteSheet);
		
		this.game = game;
		
		float width = 16f;
		float height = 32f;
		
		this.x = x;
		this.y = y;
		
		setBounds(x, y, width, height);
		
		closed = new TextureRegion(getTexture(), 55, 7, 16, 32);
		TextureRegion turning = new TextureRegion(getTexture(), 55, 39, 16, 32);
		TextureRegion opened = new TextureRegion(getTexture(), 55, 71, 16, 32);
		
		if(color.equals("red")) {
			closed = new TextureRegion(getTexture(), 183, 7, 16, 32);
			turning = new TextureRegion(getTexture(), 183, 39, 16, 32);
			opened = new TextureRegion(getTexture(), 183, 71, 16, 32);
		}
		
		TextureRegion notVisible = new TextureRegion(getTexture(), 55, 101, 16, 32);
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		frames.add(closed);
		frames.add(turning);
		frames.add(opened);
		frames.add(notVisible);
		
		openingAnimation = new Animation<TextureRegion>(.1f, frames);
		frames.clear();
		
		region = closed;
		
		setRegion(region);
		
		rect = new Rectangle(getX(), getY(), width, height);
		
		animationTimer = 0f;
	}
	
	public void update(float dt) {
		region = getFrame(dt);
		setRegion(region);
		
		if(isOpening) {
			animationTimer += dt;
		}
	}
	
	public TextureRegion getFrame(float dt) {
		if(isOpening) {
			region = (TextureRegion) openingAnimation.getKeyFrame(animationTimer, false);
		}
		else {
			region = closed;
		}
		return region;
	}
	
	public void open() {
		animationTimer = 0f;
		isOpening = true;
	}
}
