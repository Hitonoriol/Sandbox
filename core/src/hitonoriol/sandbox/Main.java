package hitonoriol.sandbox;

import com.badlogic.gdx.*;
import hitonoriol.sandbox.gui.Gui;

public class Main extends Game {

    Sandbox sandbox;

    @Override
    public void create() {
        Resources.load();
        Gui.init();
        sandbox = new Sandbox();
        this.setScreen(sandbox);
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
