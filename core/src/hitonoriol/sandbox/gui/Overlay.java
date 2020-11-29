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
    float BTN_HEIGHT = 30;

    public Overlay() {
        super();
        initBottomTable();
    }

    private void initBottomTable() {
        TextButton modeBtn = new TextButton(Mouse.mode.name(), Gui.skin);
        TextButton bodyTypeBtn = new TextButton(Mouse.spawnType.name(), Gui.skin);
        TextButton propButton = new TextButton("Edit", Gui.skin);

        bottomTable.defaults().size(BTN_WIDTH, BTN_HEIGHT).padLeft(7);
        bottomTable.add(buttonTable("Mode", modeBtn));
        bottomTable.add(buttonTable("Body Type", bodyTypeBtn));
        bottomTable.add(buttonTable("Properties", propButton));
        bottomTable.align(Align.bottomLeft);

        bottomTable.addListener(new OverlayMouseoverListener());
        super.addActor(bottomTable);
        bottomTable.moveBy(0, 14);

        modeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                modeBtn.setText(Mouse.nextMode().name());
                if (Mouse.mode != Mouse.Mode.Spawn)
                    bodyTypeBtn.setDisabled(true);
                else
                    bodyTypeBtn.setDisabled(false);
            }
        });

        bodyTypeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bodyTypeBtn.setText(Mouse.nextSpawnType().name());
            }
        });

        propButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new PropDialog().show(actor.getStage());
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
