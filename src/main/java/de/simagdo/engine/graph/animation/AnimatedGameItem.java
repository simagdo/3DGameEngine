package de.simagdo.engine.graph.animation;

import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.items.GameItem;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AnimatedGameItem extends GameItem {

    private Map<String, Animation> animations;
    private Animation currentAnimation;

    public AnimatedGameItem(Mesh[] meshes, Map<String, Animation> animations) {
        super(meshes);
        this.animations = animations;
        Optional<Map.Entry<String, Animation>> entry = animations.entrySet().stream().findFirst();
        currentAnimation = entry.map(Map.Entry::getValue).orElse(null);
    }

    public Animation getAnimation(String name) {
        return animations.get(name);
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(Animation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }
}
