package de.simagdo.engine.openglWrapper.openglUtils;

import de.simagdo.engine.openglWrapper.openglObjects.Attribute;
import de.simagdo.engine.openglWrapper.openglObjects.VAO;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class VAOUtils {

    public static VAO createVao(float[] interleavedData, Attribute... attributes) {
        VAO vao = VAO.create();
        vao.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(interleavedData.length);
            buffer.put(interleavedData);
            buffer.flip();
            vao.initDataFeed(buffer, GL15.GL_STATIC_DRAW, attributes);
        }

        vao.unbind();
        return vao;
    }

}
