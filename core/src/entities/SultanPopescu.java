package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import lost.scroll.of.aspic.RpgGame;

public class SultanPopescu extends Sprite {
	
	private RpgGame game;
	
	private TextureRegion standSouth;
	
	private TextureRegion region;
	private static Texture spriteSheet = new Texture("data/sprites/sultanPopescu.png");
		
	public Rectangle rect;
	
	
	public SultanPopescu(RpgGame game) {
		super(spriteSheet);
		
		this.game = game;
		
		float width = (float) RpgGame.TILE_SIZE;
		float height = (float) RpgGame.TILE_SIZE;
		
		float startX = (float) RpgGame.TILE_SIZE * 8;
		float startY = (float) RpgGame.TILE_SIZE * 12;
		
		setBounds(startX, startY, width, height);
		
		standSouth = new TextureRegion(getTexture(), 64, 0, 32, 32);
		region = standSouth;
		
		setRegion(region);
		rect = new Rectangle(getX(), getY(), width, height);
	}
}