package hitonoriol.sandbox.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import hitonoriol.sandbox.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.resizable = false;
		config.foregroundFPS = 60;
		config.backgroundFPS = 0;
		config.vSyncEnabled = true;
		config.title = "Sandbox";
		new LwjglApplication(new Main(), config);
	}
}
