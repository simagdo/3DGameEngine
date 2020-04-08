package de.simagdo.engine.graph;

import de.simagdo.engine.window.Window;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL30.*;

public class GBuffer {

    private static final int TOTAL_TEXTURES = 6;

    private int gBufferId;
    private int[] textureIds;
    private int width;
    private int height;

    public GBuffer(Window window) throws Exception {
        // Create G-Buffer
        this.gBufferId = glGenFramebuffers();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.gBufferId);

        this.textureIds = new int[TOTAL_TEXTURES];
        glGenTextures(this.textureIds);

        this.width = window.getPixelWidth();
        this.height = window.getPixelHeight();

        // Create textures for position, diffuse color, specular color, normal, shadow factor and depth
        // All coordinates are in world coordinates system
        for (int i = 0; i < TOTAL_TEXTURES; i++) {
            glBindTexture(GL_TEXTURE_2D, this.textureIds[i]);
            int attachmentType;
            switch (i) {
                case TOTAL_TEXTURES - 1:
                    // Depth component
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, this.width, this.height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
                    attachmentType = GL_DEPTH_ATTACHMENT;
                    break;
                default:
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, this.width, this.height, 0, GL_RGB, GL_FLOAT, (ByteBuffer) null);
                    attachmentType = GL_COLOR_ATTACHMENT0 + i;
                    break;
            }
            // For sampling
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            // Attach the the texture to the G-Buffer
            glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, GL_TEXTURE_2D, this.textureIds[i], 0);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer intBuff = stack.mallocInt(TOTAL_TEXTURES);
            int values[] = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3, GL_COLOR_ATTACHMENT4, GL_COLOR_ATTACHMENT5};
            for (int i = 0; i < values.length; i++) {
                intBuff.put(values[i]);
            }
            intBuff.flip();
            glDrawBuffers(intBuff);
        }

        // Unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getGBufferId() {
        return this.gBufferId;
    }

    public int[] getTextureIds() {
        return this.textureIds;
    }

    public int getPositionTexture() {
        return this.textureIds[0];
    }

    public int getDepthTexture() {
        return this.textureIds[TOTAL_TEXTURES - 1];
    }

    public void cleanUp() {
        glDeleteFramebuffers(gBufferId);

        if (this.textureIds != null) {
            for (int i = 0; i < TOTAL_TEXTURES; i++) {
                glDeleteTextures(this.textureIds[i]);
            }
        }
    }

}
