package engine.particleRendering;

import engine.entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import engine.renderEngine.Loader;

import java.util.*;

// Master Renderer for the in particleSystems contained particles
public class ParticleMaster {
    private static final Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;

    public static void init(Loader loader, Matrix4f projectionMatrix) {
        renderer = new ParticleRenderer(loader, projectionMatrix);
    }

    // update all the particles and remove dead ones
    public static void update() {
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while (iterator.hasNext()) {
                Particle particle = iterator.next();
                boolean stillAlive = particle.update(); // update particle
                if (!stillAlive) { // removes a dead particle when their lifetime is over
                    iterator.remove();
                    if (list.isEmpty())
                        mapIterator.remove();
                }
            }
        }
    }

    public static void render(Camera camera) {
        renderer.render(particles, camera);
    }

    // adds a particle to the list of alive particles
    public static void addParticle(Particle particle) {
        List<Particle> list = particles.get(particle.getTexture());
        if (list == null) {
            list = new ArrayList<>();
            particles.put(particle.getTexture(), list);
        }
        list.add(particle);
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }
}
