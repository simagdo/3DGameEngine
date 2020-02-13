package de.simagdo.engine.graph.shadow;

import de.simagdo.engine.graph.ArrayTexture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

public class ShadowBuffer {

    public static final int SHADOW_MAP_WIDTH = (int) Math.pow(65, 2);
    public static final int SHADOW_MAP_HEIGHT = SHADOW_MAP_WIDTH;
    private final int depthMapFBO;
    private final ArrayTexture depthMap;

    public ShadowBuffer() throws Exception {
        // Create a FBO to render the depth map
        this.depthMapFBO = glGenFramebuffers();

        // Create the depth map textures
        this.depthMap = new ArrayTexture(ShadowRenderer.NUM_CASCADES, SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);

        // Attach the the depth map texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, this.depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.depthMap.getIds()[0], 0);

        // Set only depth
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }

        // Unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public ArrayTexture getDepthMapTexture() {
        return this.depthMap;
    }

    public int getDepthMapFBO() {
        return this.depthMapFBO;
    }

    public void bindTextures(int start) {
        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            glActiveTexture(start + i);
            glBindTexture(GL_TEXTURE_2D, this.depthMap.getIds()[i]);
        }
    }

    public void cleanUp() {
        glDeleteFramebuffers(this.depthMapFBO);
        this.depthMap.cleanUp();
    }

}
