package hitonoriol.sandbox;

import com.badlogic.gdx.*;
import hitonoriol.sandbox.gui.Gui;

public class Main extends Game {

    public static Sandbox sandbox;

    @Override
    public void create() {
        Resources.load();
        Gui.init();
        this.setScreen(sandbox = new Sandbox());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        sandbox.dispose();
    }

}
