package hitonoriol.sandbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class Mouse {
    public static Mode mode = Mode.Spawn;
    public static PhysBody.Type spawnType = PhysBody.Type.Box;
    static Vector3 worldCoords = new Vector3();

    public static Vector3 screenToWorld(OrthographicCamera camera, float x, float y) {
        worldCoords.set(x, Gdx.graphics.getHeight() - y, 0);
        worldCoords = camera.unproject(worldCoords);
        worldCoords.x /= Sandbox.PPM;
        worldCoords.y /= Sandbox.PPM;
        return worldCoords;
    }

    public static Mode nextMode() {
        return (Mouse.mode = (Mouse.Mode) Utils.nextEnum(Mouse.mode));
    }

    public static PhysBody.Type nextSpawnType() {
        return Mouse.spawnType = (PhysBody.Type) Utils.nextEnum(Mouse.spawnType);
    }

    public enum Mode {
        Spawn, Move;
    }
}
