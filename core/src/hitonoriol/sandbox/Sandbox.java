package hitonoriol.sandbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import hitonoriol.sandbox.gui.Gui;
import hitonoriol.sandbox.gui.Overlay;

import java.util.ArrayList;
import java.util.List;

public class Sandbox extends InputAdapter implements Screen {

    public static final float PPM = 100f;
    final float TIME_STEP = 1f / 60f;
    final int VEL_ITER = 10;
    final int POS_ITER = 5;

    Overlay overlay;
    SpriteBatch batch;

    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;

    Vector2 jointTarget = new Vector2();
    MouseJointDef jointDef;
    MouseJoint joint;

    World world = new World(new Vector2(0, -9.81f), true);
    Body ground;
    List<PhysBody> bodies = new ArrayList<>();

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debugRenderer = new Box2DDebugRenderer();

        overlay = new Overlay();
        Gdx.input.setInputProcessor(overlay);

        initWorld();
        jointDef = new MouseJointDef();
        jointDef.bodyA = ground;
        jointDef.collideConnected = true;
        jointDef.maxForce = 125;
        initMouseListeners();
    }

    private void initMouseListeners() {
        overlay.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gui.gameUnfocused)
                    return;
                Mouse.screenToWorld(camera, x, y);
                if (Mouse.mode == Mouse.Mode.Spawn)
                    bodies.add(new PhysBody(world, Mouse.spawnType, Mouse.worldCoords.x, Mouse.worldCoords.y));
            }
        });

        overlay.addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Mouse.screenToWorld(camera, x, y);
                world.QueryAABB(fixture -> {
                    PhysBody pBody = findBody(fixture.getBody());

                    if (pBody == null)
                        return false;
                    if (pBody.body == null)
                        return false;

                    bodies.remove(pBody);
                    world.destroyBody(pBody.body);
                    return false;
                }, Mouse.worldCoords.x, Mouse.worldCoords.y, Mouse.worldCoords.x, Mouse.worldCoords.y);
            }
        });

        overlay.addListener(new DragListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Mouse.mode != Mouse.Mode.Move)
                    return false;
                Mouse.screenToWorld(camera, x, y);
                world.QueryAABB(fixture -> {
                    if (!fixture.testPoint(Mouse.worldCoords.x, Mouse.worldCoords.y))
                        return false;

                    jointDef.bodyB = fixture.getBody();
                    jointDef.target.set(Mouse.worldCoords.x, Mouse.worldCoords.y);
                    joint = (MouseJoint) world.createJoint(jointDef);

                    return true;
                }, Mouse.worldCoords.x, Mouse.worldCoords.y, Mouse.worldCoords.x, Mouse.worldCoords.y);

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (joint == null)
                    return;

                world.destroyJoint(joint);
                joint = null;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (joint == null)
                    return;

                Mouse.screenToWorld(camera, x, y);

                joint.setTarget(jointTarget.set(Mouse.worldCoords.x, Mouse.worldCoords.y));

                return;
            }

        });
    }

    private PhysBody findBody(Body body) {
        for (PhysBody pBody : bodies)
            if (pBody.body == body)
                return pBody;
        return null;
    }

    private void initWorld() {
        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.StaticBody;
        float width = Gdx.graphics.getWidth() / PPM;
        float height = (Gdx.graphics.getHeight() - 100) / PPM;
        groundDef.position.set(0, 0);
        FixtureDef fixtureDef2 = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-width / 2, -height / 2, width / 2, -height / 2);
        fixtureDef2.shape = edgeShape;
        ground = world.createBody(groundDef);
        ground.createFixture(fixtureDef2);
        edgeShape.dispose();
    }

    @Override
    public void render(float delta) {
        camera.update();
        world.step(TIME_STEP, VEL_ITER, POS_ITER);

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        overlay.act();
        overlay.draw();
        batch.begin();

        for (PhysBody body : bodies)
            body.updateSprite().draw(batch);

        batch.end();
        debugRenderer.render(world, batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        world.dispose();
    }

}
