package hitonoriol.sandbox;

import com.badlogic.gdx.graphics.Texture;

public class Resources {
    static Texture box, circle;

    public static void load() {
        box = new Texture("objects/box.png");
        circle = new Texture("objects/circle.png");
    }
}
