package hitonoriol.sandbox;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;

public class PhysBody {
    Body body;
    Type type;
    final Sprite sprite;
    private final World world;

    public PhysBody(World world, Type type, FixtureDef fixture, float x, float y) {
        this.world = world;
        this.type = type;
        this.sprite = new Sprite(type.getTexture());
        sprite.setPosition(x * Sandbox.PPM, y * Sandbox.PPM);
        body = createBody(fixture);
        Utils.out("Created a new body at: " + x + ", " + y);
    }

    private Body createBody(FixtureDef fixtureDef) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(sprite.getX() / Sandbox.PPM, sprite.getY() / Sandbox.PPM);
        bodyDef.linearDamping = 0;

        Shape shape;

        if (type == Type.Circle) {
            shape = new CircleShape();
            shape.setRadius(sprite.getWidth() / Sandbox.PPM / 2f);
            Utils.out("Creatin circle!!!1");
        } else {
            shape = new PolygonShape();
            ((PolygonShape) shape).setAsBox(sprite.getWidth() / Sandbox.PPM / 2f,
                    sprite.getHeight() / Sandbox.PPM / 2f);
        }

        fixtureDef.shape = shape;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    public Sprite updateSprite() {
        sprite.setPosition((body.getPosition().x * Sandbox.PPM) - (sprite.getWidth() / 2f),
                (body.getPosition().y * Sandbox.PPM) - (sprite.getHeight() / 2f));
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        return sprite;
    }

    public boolean overlaps(float x, float y) {
        for (Fixture fixture : body.getFixtureList())
            if (fixture.testPoint(x, y))
                return true;
        return false;
    }

    public enum Type {
        Box, Circle, Water;

        Texture getTexture() {
            switch (this) {

                case Box:
                    return Resources.box;
                case Circle:
                    return Resources.circle;
            }

            return Resources.box;
        }

        public boolean spawnedByClick() {
            return this != Water;
        }
    }
}
