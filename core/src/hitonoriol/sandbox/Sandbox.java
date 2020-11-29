package hitonoriol.sandbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import finnstr.libgdx.liquidfun.*;
import hitonoriol.sandbox.gui.Gui;
import hitonoriol.sandbox.gui.Overlay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sandbox extends InputAdapter implements Screen {

    final float ZOOM = 1f;
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

    ParticleSystem particleSystem;
    ParticleGroupDef particleDef;
    ParticleDebugRenderer particleDebugRenderer;

    Body ground;
    List<PhysBody> bodies = new ArrayList<>();
    List<Body> delQueue = new ArrayList<>();

    Set<Integer> heldButtons = new HashSet<>();

    World world = new World(new Vector2(0, -9.81f), true);

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / ZOOM, Gdx.graphics.getHeight() / ZOOM);
        debugRenderer = new Box2DDebugRenderer();

        overlay = new Overlay();
        Gdx.input.setInputProcessor(overlay);

        initWorld();
        initParticles();
        jointDef = new MouseJointDef();
        jointDef.bodyA = ground;
        jointDef.collideConnected = true;
        jointDef.maxForce = 125;
        initMouseListeners();
    }

    private void spawnParticles(float x, float y) {
        particleDef.position.set(x, y);
        particleSystem.createParticleGroup(particleDef);
        if (particleSystem.getParticleCount() > particleDebugRenderer.getMaxParticleNumber())
            particleDebugRenderer.setMaxParticleNumber(particleSystem.getParticleCount() + 1000);
    }

    private void initParticles() {
        ParticleSystemDef systemDef = new ParticleSystemDef();
        systemDef.radius = 3f / PPM;
        systemDef.dampingStrength = 2f;
        systemDef.destroyByAge = true;

        particleSystem = new ParticleSystem(world, systemDef);
        particleSystem.setParticleDensity(0.02f);

        particleDef = new ParticleGroupDef();
        particleDef.flags.add(ParticleDef.ParticleType.b2_waterParticle);
        particleDef.linearVelocity.set(0, 0);
        CircleShape partShape = new CircleShape();
        partShape.setRadius(20f / PPM);
        particleDef.shape = partShape;
        particleDef.lifetime = 60f;
        particleDef.angularVelocity = 10f;
        particleDef.stride = 0.075f;

        particleDebugRenderer = new ParticleDebugRenderer(new Color(0x87ceeb7f), 1000);
    }

    private void initMouseListeners() {
        overlay.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gui.gameUnfocused)
                    return;
                Mouse.screenToWorld(camera, x, y);

                if (Mouse.mode == Mouse.Mode.Spawn && Mouse.spawnType.spawnedByClick())
                    bodies.add(new PhysBody(world, Mouse.spawnType, Mouse.worldCoords.x, Mouse.worldCoords.y));
            }
        });

        overlay.addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Mouse.screenToWorld(camera, x, y);
                world.QueryAABB(new QueryCallback() {
                    @Override
                    public boolean reportFixture(Fixture fixture) {
                        PhysBody pBody = findBody(fixture.getBody());

                        if (pBody == null)
                            return false;

                        bodies.remove(pBody);
                        delQueue.add(pBody.body);
                        return false;
                    }

                    @Override
                    public boolean reportParticle(ParticleSystem particleSystem, int i) {
                        return false;
                    }

                    @Override
                    public boolean shouldQueryParticleSystem(ParticleSystem particleSystem) {
                        return false;
                    }
                }, Mouse.worldCoords.x, Mouse.worldCoords.y, Mouse.worldCoords.x, Mouse.worldCoords.y);
            }
        });

        overlay.addListener(new DragListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                heldButtons.add(button);
                if (Mouse.mode != Mouse.Mode.Move)
                    return true;
                Mouse.screenToWorld(camera, x, y);
                world.QueryAABB(new QueryCallback() {
                    @Override
                    public boolean reportFixture(Fixture fixture) {
                        if (!fixture.testPoint(Mouse.worldCoords.x, Mouse.worldCoords.y))
                            return true;

                        jointDef.bodyB = fixture.getBody();
                        jointDef.target.set(Mouse.worldCoords.x, Mouse.worldCoords.y);
                        joint = (MouseJoint) world.createJoint(jointDef);

                        return true;
                    }

                    @Override
                    public boolean reportParticle(ParticleSystem particleSystem, int i) {
                        return false;
                    }

                    @Override
                    public boolean shouldQueryParticleSystem(ParticleSystem particleSystem) {
                        return false;
                    }
                }, Mouse.worldCoords.x, Mouse.worldCoords.y, Mouse.worldCoords.x, Mouse.worldCoords.y);

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                heldButtons.remove(button);
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
        FixtureDef fixtureDef = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-width, -height / 2, width * 2, -height / 2);
        fixtureDef.shape = edgeShape;
        ground = world.createBody(groundDef);
        ground.createFixture(fixtureDef);
        edgeShape.dispose();

        BodyDef sideDef = new BodyDef();
        sideDef.type = BodyDef.BodyType.StaticBody;
        sideDef.position.set(-width / 2f, -height / 10f);
        Body left = world.createBody(sideDef);

        sideDef.position.set(width / 2f, -height / 10f);
        Body right = world.createBody(sideDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width * 0.005f, height);
        fixtureDef.shape = shape;

        left.createFixture(fixtureDef);
        right.createFixture(fixtureDef);
        shape.dispose();
    }

    private void pollHeldButtonActions() {
        if (Gui.gameUnfocused)
            return;

        boolean lmb = heldButtons.contains(Input.Buttons.LEFT);
        Mouse.screenToWorld(camera);

        if (lmb && Mouse.mode == Mouse.Mode.Spawn) {
            if (Mouse.spawnType == PhysBody.Type.Water)
                spawnParticles(Mouse.worldCoords.x, Mouse.worldCoords.y);
        }
    }

    private Matrix4 renderMatrix;

    @Override
    public void render(float delta) {
        camera.update();
        world.step(TIME_STEP, VEL_ITER, POS_ITER, particleSystem.calculateReasonableParticleIterations(Gdx.graphics.getDeltaTime()));

        pollHeldButtonActions();

        if (!delQueue.isEmpty()) {
            for (Body body : delQueue)
                world.destroyBody(body);
            delQueue.clear();
        }

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        overlay.act();
        overlay.draw();
        batch.begin();

        for (PhysBody body : bodies)
            body.updateSprite().draw(batch);

        batch.end();

        renderMatrix = batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0);
        debugRenderer.render(world, renderMatrix);
        particleDebugRenderer.render(particleSystem, PPM, renderMatrix);
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
