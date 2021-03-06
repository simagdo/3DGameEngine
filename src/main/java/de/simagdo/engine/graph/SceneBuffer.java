package de.simagdo.engine.graph;

import de.simagdo.engine.window.Window;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;

public class SceneBuffer {

    private int bufferId;
    private int textureId;

    public SceneBuffer(Window window) throws Exception {
        // Create the buffer
        this.bufferId = glGenFramebuffers();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.bufferId);

        // Create texture
        int[] textureIds = new int[1];
        glGenTextures(textureIds);
        this.textureId = textureIds[0];
        glBindTexture(GL_TEXTURE_2D, this.textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, window.getPixelWidth(), window.getPixelHeight(), 0, GL_RGB, GL_FLOAT, (ByteBuffer) null);

        // For sampling
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Attach the the texture to the G-Buffer
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.textureId, 0);

        // Unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getBufferId() {
        return this.bufferId;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void cleanUp() {
        glDeleteFramebuffers(this.bufferId);

        glDeleteTextures(this.textureId);
    }

}
