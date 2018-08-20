package lost.scroll.of.aspic.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lost.scroll.of.aspic.RpgGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "The Lost Scroll of Aspic";
		config.backgroundFPS = 10;
		config.foregroundFPS = 60;
		config.vSyncEnabled = true;
		new LwjglApplication(new RpgGame(), config);
	}
}
