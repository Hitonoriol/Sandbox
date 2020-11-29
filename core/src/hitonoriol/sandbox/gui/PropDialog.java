package hitonoriol.sandbox.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import hitonoriol.sandbox.Mouse;
import me.xdrop.jrand.JRand;

public class PropDialog extends GameDialog {

    Slider densitySlider = new Slider(0f, 500f, 0.05f, false, Gui.skin);
    Slider frictionSlider = new Slider(0f, 1f, 0.01f, false, Gui.skin);
    Slider restitutionSlider = new Slider(0f, 1f, 0.01f, false, Gui.skin);

    TextButton randButton = new TextButton("Random", Gui.skin);
    TextButton applyButton = new TextButton("Apply", Gui.skin);
    TextButton closeButton = new TextButton("Close", Gui.skin);

    public PropDialog() {
        super("Properties");
        super.add("").row();
        super.align(Align.left);
        densitySlider.setValue(Mouse.fixtureDef.density);
        frictionSlider.setValue(Mouse.fixtureDef.friction);
        restitutionSlider.setValue(Mouse.fixtureDef.restitution);

        if (Mouse.spawnType.spawnedByClick())
            createBodyPropMenu();

        super.add(randButton).pad(5);
        super.add(applyButton).pad(5);
        super.add(closeButton);

        initListeners();
    }

    private void createBodyPropMenu() {
        addSlider("Density: ", densitySlider);
        addSlider("Friction: ", frictionSlider);
        addSlider("Restitution: ", restitutionSlider);
    }

    private void addSlider(String text, Slider slider) {
        TextButton incButton = new TextButton("+", Gui.skin), decButton = new TextButton("-", Gui.skin);
        super.add(text);
        super.add(createSliderLabel(slider)).align(Align.left).width(125);
        super.add(decButton).size(15);
        super.add(slider).size(Gui.SLIDER_W, Gui.SLIDER_H);
        super.add(incButton).size(15).row();

        incButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                slider.setValue(slider.getValue() + slider.getStepSize());
            }
        });

        decButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                slider.setValue(slider.getValue() - slider.getStepSize());
            }
        });

    }

    private Label createSliderLabel(Slider slider) {
        Label label = new Label("(" + slider.getValue() + ")", Gui.skin);
        label.setAlignment(Align.left);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                label.setText("(" + slider.getValue() + ")");
            }
        });

        return label;
    }

    private void randomizeSlider(Slider slider) {
        slider.setValue(JRand.flt().range(slider.getMinValue(), slider.getMaxValue()).gen());
    }

    private void initListeners() {
        randButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                randomizeSlider(densitySlider);
                randomizeSlider(restitutionSlider);
                randomizeSlider(frictionSlider);
            }
        });

        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Mouse.fixtureDef.density = densitySlider.getValue();
                Mouse.fixtureDef.friction = frictionSlider.getValue();
                Mouse.fixtureDef.restitution = restitutionSlider.getValue();
                remove();
            }
        });

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
    }
}
