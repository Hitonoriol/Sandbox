package hitonoriol.sandbox;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

public class Sandbox extends ApplicationAdapter {
    final float TIME_STEP = 1f / 60f;
    final int VEL_ITER = 7;
    final int POS_ITER = 2;

    SpriteBatch batch;
    Texture boxTexture;

    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;

    Stage stage;

    Vector3 worldMouseCoords = new Vector3();
    World world = new World(new Vector2(0, -98.1f), true);
    Body ground;
    List<PhysBody> bodies = new ArrayList<>();

    @Override
    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debugRenderer = new Box2DDebugRenderer();

        boxTexture = new Texture("box.png");

        initWorld();

        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenToWorld(x, y);
                bodies.add(new PhysBody(world, new Sprite(boxTexture), worldMouseCoords.x, worldMouseCoords.y));
            }
        });

        stage.addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenToWorld(x, y);
                world.QueryAABB(new QueryCallback() {
                    @Override
                    public boolean reportFixture(Fixture fixture) {
                        Body body;
                        bodies.remove(findBody(body = fixture.getBody()));
                        world.destroyBody(body);
                        return true;
                    }
                }, worldMouseCoords.x, worldMouseCoords.y, worldMouseCoords.x, worldMouseCoords.y);
            }
        });
    }

    private PhysBody findBody(Body body) {
        for (PhysBody pBody : bodies)
            if (pBody.body == body)
                return pBody;
        return null;
    }

    private Vector3 screenToWorld(float x, float y) {
        worldMouseCoords.set(x, Gdx.graphics.getHeight() - y, 0);
        return worldMouseCoords = camera.unproject(worldMouseCoords);
    }

    private void initWorld() {
        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.StaticBody;
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight() - 50;
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
    public void render() {
        camera.update();
        world.step(TIME_STEP, VEL_ITER, POS_ITER);

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        stage.act();
        batch.begin();

        for (PhysBody body : bodies)
            body.updateSprite().draw(batch);

        batch.end();
        debugRenderer.render(world, batch.getProjectionMatrix());
    }

    @Override
    public void dispose() {
        world.dispose();
    }

    public static void out(String str) {
        System.out.println(str);
    }
}
