package de.simagdo.engine.graph;

import de.simagdo.engine.graph.text.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {

    public static final int SHADOW_MAP_WIDTH = 1024;
    public static final int SHADOW_MAP_HEIGHT = 1024;
    private final int depthMapFBO;
    private final Texture depthMap;

    public ShadowMap() throws Exception {
        //Create a FBO to render the Depth Map
        this.depthMapFBO = glGenFramebuffers();

        //Creat the Depth Map Texture
        this.depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);

        //Attach the Depth Map Texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, this.depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D,this.depthMap.getId(),0);

        //Set only Depth
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new Exception("Could not create FrameBuffer!");

        //Unbind
        glBindFramebuffer(GL_FRAMEBUFFER,0);

    }

    public static int getShadowMapWidth() {
        return SHADOW_MAP_WIDTH;
    }

    public static int getShadowMapHeight() {
        return SHADOW_MAP_HEIGHT;
    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }

    public Texture getDepthMap() {
        return depthMap;
    }

    public void cleanUp() {
        glDeleteFramebuffers(this.depthMapFBO);
        this.depthMap.cleanUp();
    }

}
