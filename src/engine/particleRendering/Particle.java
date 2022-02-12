package engine.particleRendering;

import org.lwjgl.util.vector.Vector3f;
import engine.renderEngine.DisplayManager;

// Particles are spawned by a particleSystem updating via physics and dying after a certain lifetime
public class Particle {
    public static final float GRAVITY = -50;
    private final Vector3f position;
    private final Vector3f velocity;
    private final float gravityEffect;
    private final float lifeLength;
    private final float rotation;
    private final float scale;
    private final ParticleTexture texture;
    private float elapsedTime = 0;

    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) {
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        this.texture = texture;
        ParticleMaster.addParticle(this); // adds itself to the list of alive engine.particles
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    // calculate new state of particle according to physics
    protected boolean update() {
        velocity.y += GRAVITY * gravityEffect * DisplayManager.getFrameTimeSeconds();
        Vector3f change = new Vector3f(velocity);
        change.scale(DisplayManager.getFrameTimeSeconds());
        Vector3f.add(change, position, position);
        elapsedTime += DisplayManager.getFrameTimeSeconds();
        return elapsedTime < lifeLength;
    }
}
