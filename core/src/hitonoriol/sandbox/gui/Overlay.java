package hitonoriol.sandbox.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import hitonoriol.sandbox.Main;
import hitonoriol.sandbox.Mouse;
import hitonoriol.sandbox.PhysBody;

public class Overlay extends Stage {
    Table bottomTable = new Table();

    float BTN_WIDTH = 150;
    float BTN_HEIGHT = 30;

    public Overlay() {
        super();
        initBottomTable();
    }

    private void initBottomTable() {
        TextButton modeBtn = new TextButton(Mouse.mode.name(), Gui.skin);
        TextButton bodyTypeBtn = new TextButton(Mouse.spawnType.name(), Gui.skin);
        TextButton propButton = new TextButton("Edit", Gui.skin);
        TextButton particleTypeButton = new TextButton("Water", Gui.skin);
        TextButton clearWorldBtn = new TextButton("Clear", Gui.skin);
        particleTypeButton.setDisabled(true);

        bottomTable.defaults().size(BTN_WIDTH, BTN_HEIGHT).padLeft(7);
        bottomTable.add(buttonTable("Mode", modeBtn));
        bottomTable.add(buttonTable("Body Type", bodyTypeBtn));
        bottomTable.add(buttonTable("Particle Type", particleTypeButton));
        bottomTable.add(buttonTable("Properties", propButton));
        bottomTable.add(buttonTable("World", clearWorldBtn));
        bottomTable.align(Align.bottomLeft);

        bottomTable.addListener(new OverlayMouseoverListener());
        super.addActor(bottomTable);
        bottomTable.moveBy(0, 14);

        modeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                modeBtn.setText(Mouse.nextMode().name());
                disableButtons(Mouse.mode != Mouse.Mode.Spawn, bodyTypeBtn, propButton);
            }
        });

        bodyTypeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bodyTypeBtn.setText(Mouse.nextSpawnType().name());
                disableButtons(Mouse.spawnType != PhysBody.Type.Particle, particleTypeButton);
            }
        });

        propButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new PropDialog().show(actor.getStage());
            }
        });

        particleTypeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                particleTypeButton.setText(Mouse.nextParticleType().name());
            }
        });

        clearWorldBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.sandbox.clearWorld();
            }
        });
    }

    private void disableButtons(boolean disable, TextButton... buttons) {
        for (TextButton button : buttons)
            button.setDisabled(disable);
    }

    private Table buttonTable(String text, TextButton button) {
        Table table = new Table(Gui.skin);
        table.add(text).row();
        table.add(button).size(BTN_WIDTH, BTN_HEIGHT);
        return table;
    }
}
