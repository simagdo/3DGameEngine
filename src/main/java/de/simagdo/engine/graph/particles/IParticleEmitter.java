package de.simagdo.engine.graph.particles;

import de.simagdo.engine.items.GameItem;

import java.util.List;

public interface IParticleEmitter {

    void cleanUp();

    Particle getBaseParticle();

    List<GameItem> getParticles();

}
