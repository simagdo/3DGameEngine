package de.simagdo.engine.graph.particles;

import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.items.GameItem;
import org.joml.Vector3f;

public class Particle extends GameItem {

    private Vector3f speed;
    private long timeToLive;
    private long updateTextureMillis;
    private long currentAnimTimeMillis;
    private int animFrames;

    public Particle(Mesh mesh, Vector3f speed, long timeToLive, long updateTextureMillis) {
        super(mesh);
        this.speed = new Vector3f(speed);
        this.timeToLive = timeToLive;
        this.updateTextureMillis = updateTextureMillis;
        this.currentAnimTimeMillis = 0;
        Texture texture = this.getMesh().getMaterial().getTexture();
        this.animFrames = texture.getNumCols() * texture.getNumRows();
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
        this.updateTextureMillis = baseParticle.getUpdateTextureMillis();
        this.currentAnimTimeMillis = 0;
        this.animFrames = baseParticle.getAnimFrames();
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
        this.timeToLive -= elapsedTime;
        if (this.currentAnimTimeMillis >= this.getUpdateTextureMillis() && this.animFrames > 0) {
            this.currentAnimTimeMillis = 0;
            int pos = this.getTextPos();
            pos++;
            if (pos < this.animFrames) this.setTextPos(pos);
            else this.setTextPos(0);
        }
        return this.timeToLive;
    }

    public long getUpdateTextureMillis() {
        return updateTextureMillis;
    }

    public void setUpdateTextureMillis(long updateTextureMillis) {
        this.updateTextureMillis = updateTextureMillis;
    }

    public long getCurrentAnimTimeMillis() {
        return currentAnimTimeMillis;
    }

    public void setCurrentAnimTimeMillis(long currentAnimTimeMillis) {
        this.currentAnimTimeMillis = currentAnimTimeMillis;
    }

    public int getAnimFrames() {
        return animFrames;
    }

    public void setAnimFrames(int animFrames) {
        this.animFrames = animFrames;
    }



}
