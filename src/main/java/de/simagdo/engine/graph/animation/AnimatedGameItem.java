package de.simagdo.engine.graph.animation;

import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.items.GameItem;
import org.joml.Matrix4f;

import java.util.List;

public class AnimatedGameItem extends GameItem {

    private int currentFrame;
    private List<AnimatedFrame> frames;
    private List<Matrix4f> invJointMatrices;

    public AnimatedGameItem(Mesh[] meshes, List<AnimatedFrame> frames, List<Matrix4f> invJointMatrices) {
        super(meshes);
        this.frames = frames;
        this.invJointMatrices = invJointMatrices;
        this.currentFrame = 0;
    }

    public List<AnimatedFrame> getFrames() {
        return this.frames;
    }

    public void setFrames(List<AnimatedFrame> frames) {
        this.frames = frames;
    }

    public AnimatedFrame getCurrentFrame() {
        return this.frames.get(currentFrame);
    }

    public AnimatedFrame getNextFrame() {
        int nextFrame = this.currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            nextFrame = 0;
        }
        return this.frames.get(nextFrame);
    }

    public void nextFrame() {
        int nextFrame = this.currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            this.currentFrame = 0;
        } else {
            this.currentFrame = nextFrame;
        }
    }

    public List<Matrix4f> getInvJointMatrices() {
        return this.invJointMatrices;
    }
}