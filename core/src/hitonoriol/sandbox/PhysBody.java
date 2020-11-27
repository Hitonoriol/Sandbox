package hitonoriol.sandbox;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import me.xdrop.jrand.JRand;

public class PhysBody {
    Body body;
    final Sprite sprite;
    private final World world;

    public PhysBody(World world, Sprite sprite, float x, float y) {
        this.world = world;
        this.sprite = sprite;
        sprite.setPosition(x, y);
        body = createBoxBody(JRand.flt().range(0.1f, 2.5f).gen());
        Sandbox.out("Created a new body at: " + x + ", " + y);
    }

    private Body createBoxBody(float density) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(sprite.getX(), sprite.getY());

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    public Sprite updateSprite() {
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2f,
                body.getPosition().y - sprite.getHeight() / 2f);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        return sprite;
    }

    public boolean overlaps(float x, float y) {
        for (Fixture fixture : body.getFixtureList())
            if (fixture.testPoint(x, y))
                return true;
        return false;
    }
}
