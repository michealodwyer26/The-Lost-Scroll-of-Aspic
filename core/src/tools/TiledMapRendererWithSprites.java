package tools;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TiledMapRendererWithSprites extends OrthogonalTiledMapRenderer {
	
	private ArrayList<Sprite> sprites;
	private int drawSpritesAfterLayer = 2;
	
	public TiledMapRendererWithSprites(TiledMap map) {
		super(map);
		sprites = new ArrayList<Sprite>();
	}
	
	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}
	
    @Override
    public void render() {
        beginRender();
        int currentLayer = 0;
        for (MapLayer layer : map.getLayers()) {
            if (layer.isVisible()) {
                if (layer instanceof TiledMapTileLayer) {
                    renderTileLayer((TiledMapTileLayer)layer);
                    currentLayer++;
                    if(currentLayer == drawSpritesAfterLayer){
                        for(Sprite sprite : sprites)
                            sprite.draw(this.getBatch());
                    }
                } else {
                    for (MapObject object : layer.getObjects()) {
                        renderObject(object);
                    }
                }
            }
        }
        endRender();
    }
    
    @Override
    public void dispose() {
    	super.dispose();
    }
}
