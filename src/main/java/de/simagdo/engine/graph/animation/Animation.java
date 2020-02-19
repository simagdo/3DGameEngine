package de.simagdo.engine.graph.animation;

import java.util.List;

public class Animation {

    private int currentFrame;
    private List<AnimatedFrame> frames;
    private String name;
    private double duration;

    public Animation(String name, List<AnimatedFrame> frames, double duration) {
        this.name = name;
        this.frames = frames;
        currentFrame = 0;
        this.duration = duration;
    }

    public AnimatedFrame getCurrentFrame() {
        return this.frames.get(currentFrame);
    }

    public double getDuration() {
        return this.duration;
    }

    public List<AnimatedFrame> getFrames() {
        return this.frames;
    }

    public String getName() {
        return this.name;
    }

    public AnimatedFrame getNextFrame() {
        this.nextFrame();
        return this.frames.get(currentFrame);
    }

    public void nextFrame() {
        int nextFrame = this.currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            this.currentFrame = 0;
        } else {
            this.currentFrame = nextFrame;
        }
    }

}
