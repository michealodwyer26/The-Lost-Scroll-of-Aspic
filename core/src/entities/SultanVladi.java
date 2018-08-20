package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import lost.scroll.of.aspic.RpgGame;

public class SultanVladi extends Sprite {
private RpgGame game;
	
	private TextureRegion standSouth;
	
	private TextureRegion region;
	private static Texture spriteSheet = new Texture("data/sprites/sultanVladi.png");
		
	public Rectangle rect;	
	
	public SultanVladi(RpgGame game) {
		super(spriteSheet);
		
		this.game = game;
		
		float startX = 8 * RpgGame.TILE_SIZE;
		float startY = 12 * RpgGame.TILE_SIZE;
		
		float width = RpgGame.TILE_SIZE;
		float height = RpgGame.TILE_SIZE;
		
		setBounds(startX, startY, width, height);
		
		standSouth = new TextureRegion(getTexture(), 64, 0, 32, 32);
		region = standSouth;
		
		setRegion(region);
		rect = new Rectangle(getX(), getY(), width, height);
	}
}
