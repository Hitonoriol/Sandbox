package hitonoriol.sandbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import finnstr.libgdx.liquidfun.ParticleDef;
import finnstr.libgdx.liquidfun.ParticleGroupDef;

public class Mouse {
    public static Mode mode = Mode.Spawn;
    public static PhysBody.Type spawnType = PhysBody.Type.Box;
    public static FixtureDef fixtureDef = new FixtureDef();
    public static ParticleGroupDef particleDef;

    static Vector3 worldCoords = new Vector3();

    public static Vector3 screenToWorld(OrthographicCamera camera, float x, float y) {
        worldCoords.set(x, Gdx.graphics.getHeight() - y, 0);
        worldCoords = camera.unproject(worldCoords);
        worldCoords.x /= Sandbox.PPM;
        worldCoords.y /= Sandbox.PPM;
        return worldCoords;
    }

    public static Vector3 screenToWorld(OrthographicCamera camera) {
        return screenToWorld(camera, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
    }

    public static Mode nextMode() {
        return (Mouse.mode = (Mouse.Mode) Utils.nextEnum(Mouse.mode));
    }

    public static PhysBody.Type nextSpawnType() {
        return Mouse.spawnType = (PhysBody.Type) Utils.nextEnum(Mouse.spawnType);
    }

    public static  ParticleDef.ParticleType nextParticleType() {
        ParticleDef.ParticleType type = (ParticleDef.ParticleType) Utils.nextEnum(particleDef.flags.first());
        particleDef.flags.clear();
        particleDef.flags.add(type);
        return type;
    }

    public enum Mode {
        Spawn, Move;
    }
}
