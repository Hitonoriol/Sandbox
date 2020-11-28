package hitonoriol.sandbox.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class OverlayMouseoverListener extends InputListener {
    public OverlayMouseoverListener() {
        super();
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        Gui.gameUnfocused = true;
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            Gui.gameUnfocused = false;
    }
}