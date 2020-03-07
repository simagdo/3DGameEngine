package de.simagdo.engine.graph.shadow;

import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.ShaderProgram;
import de.simagdo.engine.loaders.assimp.StaticMeshesLoader;
import de.simagdo.engine.window.Window;
import de.simagdo.utils.Utils;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class ShadowTestRenderer {

    private ShaderProgram testShaderProgram;
    private Mesh quadMesh;

    public void init(Window window) throws Exception {
        setupTestShader();
    }

    private void setupTestShader() throws Exception {
        testShaderProgram = new ShaderProgram();
        testShaderProgram.createVertexShader(Utils.loadResource("/shaders/test/test_vertex.vs"));
        testShaderProgram.createFragmentShader(Utils.loadResource("/shaders/test/test_fragment.fs"));
        testShaderProgram.link();

        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            testShaderProgram.createUniform("texture_sampler[" + i + "]");
        }

        this.quadMesh = StaticMeshesLoader.load("/models/quad.obj", "")[0];
    }

    public void renderTest(ShadowBuffer shadowMap) {
        testShaderProgram.bind();

        testShaderProgram.setUniform("texture_sampler[0]", 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getIds()[0]);

        this.quadMesh.render();

        this.testShaderProgram.unbind();
    }

    public void cleanup() {
        if (testShaderProgram != null) {
            testShaderProgram.cleanUp();
        }
    }
}
