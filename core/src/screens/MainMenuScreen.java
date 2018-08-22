package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import lost.scroll.of.aspic.RpgGame;

public class MainMenuScreen implements Screen {

	private Viewport viewport;
	private RpgGame game;
	private Stage stage;
		
	public MainMenuScreen(RpgGame game) {
		this.game = game;
		
		viewport = new FitViewport(RpgGame.V_WIDTH, RpgGame.V_HEIGHT);
		
		stage = new Stage(viewport, game.batch);
		defineStage();
		
		game.font10.setColor(Color.WHITE);
	}
	
	private void defineStage() {
		Table table = new Table();
		Table titleTable = new Table();
		
		titleTable.setFillParent(true);
		table.setFillParent(true);
		
		Label.LabelStyle titleLabelStyle = new Label.LabelStyle(game.menuFont, Color.GOLDENROD);
		Label.LabelStyle labelStyle = new Label.LabelStyle(game.menuFont, Color.WHITE);
		
		Label title = new Label("THE LOST SCROLL OF ASPIC", titleLabelStyle);
		Label playLabel = new Label("1 - PLAY", labelStyle);
		Label quitLabel = new Label("2 - QUIT", labelStyle);
		
		int pad = 50;
		
		titleTable.center().top();
		titleTable.add(title).pad(pad);
		
		table.center();
		
		table.add(playLabel).expandX();
		table.row();
		
		table.add(quitLabel).expandX();
		table.row();
		
		stage.addActor(titleTable);
		stage.addActor(table);
	}
	
	@Override
	public void show() {
		
	}
	
	private void handleInput() {
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
			// Village of Vladi = 592, 624
			// Village of Popescu = 640, 64
			game.setScreen(new Overworld(game, 592, 624)); 
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
			Gdx.app.exit();
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		handleInput();
		
		game.batch.setProjectionMatrix(stage.getCamera().combined);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
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
		stage.dispose();
	}

}
