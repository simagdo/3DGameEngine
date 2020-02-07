package de.simagdo.engine.graph.particles;

import de.simagdo.engine.items.GameItem;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlowParticleEmitter implements IParticleEmitter {

    private int maxParticles;
    private boolean active;
    private final List<GameItem> particles;
    private final Particle baseParticle;
    private long creationPeriodMillis;
    private long lastCreationTime;
    private float speedRndRange;
    private float positionRndRange;
    private float scaleRndRange;
    private long animRange;

    public FlowParticleEmitter(Particle baseParticle, int maxParticles, long creationPeriodMillis) {
        this.particles = new ArrayList<>();
        this.baseParticle = baseParticle;
        this.maxParticles = maxParticles;
        this.active = false;
        this.lastCreationTime = 0;
        this.creationPeriodMillis = creationPeriodMillis;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getCreationPeriodMillis() {
        return this.creationPeriodMillis;
    }

    public void setCreationPeriodMillis(long creationPeriodMillis) {
        this.creationPeriodMillis = creationPeriodMillis;
    }

    public float getSpeedRndRange() {
        return this.speedRndRange;
    }

    public long getAnimRange() {
        return animRange;
    }

    public void setAnimRange(long animRange) {
        this.animRange = animRange;
    }

    public void setSpeedRndRange(float speedRndRange) {
        this.speedRndRange = speedRndRange;
    }

    public float getPositionRndRange() {
        return this.positionRndRange;
    }

    public void setPositionRndRange(float positionRndRange) {
        this.positionRndRange = positionRndRange;
    }

    public float getScaleRndRange() {
        return this.scaleRndRange;
    }

    public void setScaleRndRange(float scaleRndRange) {
        this.scaleRndRange = scaleRndRange;
    }

    public void update(long elapsedTime) {
        long now = System.currentTimeMillis();
        if (this.lastCreationTime == 0) this.lastCreationTime = now;

        Iterator<? extends GameItem> iterator = this.particles.iterator();

        while (iterator.hasNext()) {
            Particle particle = (Particle) iterator.next();
            if (particle.updateTimeToLive(elapsedTime) < 0) iterator.remove();
            else this.updatePosition(particle, elapsedTime);
        }

        int length = this.getParticles().size();
        if (now - this.lastCreationTime >= this.creationPeriodMillis && length < this.maxParticles) {
            this.createParticle();
            this.lastCreationTime = now;
        }

    }

    private void createParticle() {
        Particle particle = new Particle(this.getBaseParticle());
        float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
        float speedInc = sign * (float) Math.random() * this.speedRndRange;
        float posInc = sign * (float) Math.random() * this.positionRndRange;
        float scaleInc = sign * (float) Math.random() * this.scaleRndRange;
        long updateAnimInc = (long) sign * (long) (Math.random() * (float) this.animRange);
        particle.getPosition().add(posInc, posInc, posInc);
        particle.getSpeed().add(speedInc, speedInc, speedInc);
        particle.setScale(particle.getScale() + scaleInc);
        particle.setUpdateTextureMillis(particle.getUpdateTextureMillis() + updateAnimInc);
        this.particles.add(particle);
    }

    /**
     * Updates a Particle Position
     *
     * @param particle    the {@link Particle} to update
     * @param elapsedTime Elapsed Time in Milliseconds
     */
    private void updatePosition(Particle particle, long elapsedTime) {
        Vector3f speed = particle.getSpeed();
        float delta = elapsedTime / 1000.0f;
        float dx = speed.x * delta;
        float dy = speed.y * delta;
        float dz = speed.z * delta;
        Vector3f position = particle.getPosition();
        particle.setPosition(position.x + dx, position.y + dy, position.z + dz);
    }

    @Override
    public void cleanUp() {
        for (GameItem particle : this.getParticles()) particle.cleanUp();
    }

    @Override
    public Particle getBaseParticle() {
        return this.baseParticle;
    }

    @Override
    public List<GameItem> getParticles() {
        return this.particles;
    }
}
