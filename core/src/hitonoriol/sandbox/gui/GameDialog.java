package hitonoriol.sandbox.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.Timer;
import hitonoriol.sandbox.Utils;

public class GameDialog extends Dialog {

    public GameDialog(String title) {
        super(title, Gui.skin);
        super.getTitleTable().padTop(25);
        super.addListener(new OverlayMouseoverListener());
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog ret = super.show(stage);
        Gui.gameUnfocused = true;
        Utils.out("Unfocus");
        return ret;
    }

    @Override
    public boolean remove() {
        boolean ret = super.remove();
        Timer.instance().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                Gui.gameUnfocused = false;
            }
        }, 0.1f);
        Utils.out("Focus");
        return ret;
    }
}
