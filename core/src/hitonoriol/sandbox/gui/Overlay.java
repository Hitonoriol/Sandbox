package hitonoriol.sandbox.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import hitonoriol.sandbox.Mouse;

public class Overlay extends Stage {
    Table bottomTable = new Table();

    float BTN_WIDTH = 100;
    float BTN_HEIGHT = 35;

    public Overlay() {
        super();
        initBottomTable();
    }

    private void initBottomTable() {
        TextButton modeBtn = new TextButton(Mouse.mode.name(), Gui.skin);
        TextButton bodyTypeBtn = new TextButton(Mouse.spawnType.name(), Gui.skin);

        bottomTable.defaults().size(BTN_WIDTH, BTN_HEIGHT).pad(5);
        bottomTable.add(buttonTable("Mode", modeBtn));
        bottomTable.add(buttonTable("Body type", bodyTypeBtn));
        bottomTable.align(Align.bottomLeft);

        bottomTable.addListener(new OverlayMouseoverListener());
        super.addActor(bottomTable);

        modeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                modeBtn.setText(Mouse.nextMode().name());
            }
        });

        bodyTypeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bodyTypeBtn.setText(Mouse.nextSpawnType().name());
            }
        });
    }

    private Table buttonTable(String text, TextButton button) {
        Table table = new Table(Gui.skin);
        table.add(text).row();
        table.add(button).size(BTN_WIDTH, BTN_HEIGHT);
        return table;
    }
}
