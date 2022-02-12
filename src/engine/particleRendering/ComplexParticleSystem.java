package engine.particleRendering;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import engine.renderEngine.DisplayManager;

// complex particle Systems spawn particles specified by the systems settings
public class ComplexParticleSystem {
	private final float pps, averageSpeed, gravity, averageLifeLength, averageScale;
	private final ParticleTexture texture;
	private final Random random = new Random();

	public ComplexParticleSystem(ParticleTexture texture, float pps, float speed, float gravity, float lifeLength, float scale) {
		this.pps = pps; // particles per second
		this.averageSpeed = speed;
		this.gravity = gravity;
		this.averageLifeLength = lifeLength;
		this.averageScale = scale;
		this.texture = texture;
	}

	// generates a for the passed time new particles depending on how many particles per second are to be spawned
	public void generateParticles(Vector3f systemCenter) {
		float delta = DisplayManager.getFrameTimeSeconds();
		float particlesToCreate = pps * delta; // determine how many new particles have to be created
		int count = (int) Math.floor(particlesToCreate);
		for (int i = 0; i < count; i++) { // create those particles
			emitParticle(systemCenter);
		}
		float partialParticle = particlesToCreate % 1;
		if (Math.random() < partialParticle) { // randomly create a particle depending on the fraction
			emitParticle(systemCenter);
		}
	}

	// actually emits/creates a particle with the parameters of the given particle system
	private void emitParticle(Vector3f center) {
		Vector3f velocity;
		velocity = generateRandomUnitVector();
		velocity.normalise();
		float speedError = 0;
		velocity.scale(generateValue(averageSpeed, speedError));
		float scaleError = 0;
		float scale = generateValue(averageScale, scaleError);
		float lifeError = 0;
		float lifeLength = generateValue(averageLifeLength, lifeError);
		new Particle(texture, new Vector3f(center), velocity, gravity, lifeLength, generateRandomRotation(), scale);
	}

	private float generateValue(float average, float errorMargin) {
		float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
		return average + offset;
	}

	private float generateRandomRotation() {
		return random.nextFloat() * 360f;
	}
	
	private Vector3f generateRandomUnitVector() {
		float theta = (float) (random.nextFloat() * 2f * Math.PI);
		float z = (random.nextFloat() * 2) - 1;
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));
		return new Vector3f(x, y, z);
	}
}
