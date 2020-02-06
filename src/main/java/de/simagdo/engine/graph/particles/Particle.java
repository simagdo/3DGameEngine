package de.simagdo.engine.graph.particles;

import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.items.GameItem;
import org.joml.Vector3f;

public class Particle extends GameItem {

    private Vector3f speed;
    private long timeToLive;

    public Particle(Mesh mesh, Vector3f speed, long timeToLive) {
        super(mesh);
        this.speed = new Vector3f(speed);
        this.timeToLive = timeToLive;
    }

    public Particle(Particle baseParticle) {
        super(baseParticle.getMesh());
        Vector3f aux = baseParticle.getPosition();
        this.setPosition(aux.x, aux.y, aux.z);
        aux = baseParticle.getRotation();
        this.setRotation(aux.x, aux.y, aux.z);
        this.setScale(baseParticle.getScale());
        this.speed = new Vector3f(baseParticle.getSpeed());
        this.timeToLive = baseParticle.getTimeToLive();
    }

    public Vector3f getSpeed() {
        return speed;
    }

    public void setSpeed(Vector3f speed) {
        this.speed = speed;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * Updates the Particle's Time To Live
     *
     * @param elapsedTime Elapsed Time in milliseconds
     * @return the Particle's Time To Live
     */
    public long updateTimeToLive(long elapsedTime) {
        return this.timeToLive -= elapsedTime;
    }

}
