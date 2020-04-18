package de.simagdo.engine.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowBuilder {

    private final int width;
    private final int height;
    private final String title;
    private boolean fullscreen = false;
    private boolean vSync = true;
    private int minWidth = 120;
    private int minHeight = 120;
    private int fps = 100;
    private int maxWidth = GLFW_DONT_CARE;
    private int maxHeight = GLFW_DONT_CARE;
    private int samples = 0;
    private long windowId;
    private WindowOptions options;

    protected WindowBuilder(int width, int height, String title, WindowOptions options) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.options = options;
    }

    public Window create() {
        //Setup an Error Callback. The default implementation will print the Error Message in System.err
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW!");
        glfwInit();
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        this.setWindowHints(vidMode);
        this.windowId = this.createWindow(vidMode);
        this.applyWindowSettings();
        return new Window(this.windowId, this.width, this.height, this.fps, this.fullscreen, this.vSync, this.options);
    }

    private long createWindow(GLFWVidMode vidMode) {
        if (this.fullscreen) {
            return glfwCreateWindow(vidMode.width(), vidMode.height(), this.title, glfwGetPrimaryMonitor(), NULL);
        } else {
            long windowId = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
            if (windowId == NULL) throw new RuntimeException("Failed to create the GLFW Window!");
            glfwSetWindowPos(windowId, (vidMode.width() - this.width) / 2, (vidMode.height() - this.height) / 2);
            return windowId;
        }
    }

    private void setWindowHints(GLFWVidMode vidMode) {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, this.samples);
        glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate());
    }

    private void applyWindowSettings() {
        glfwMakeContextCurrent(this.windowId);
        glfwSwapInterval(this.vSync ? 1 : 0);
        glfwSetWindowSizeLimits(this.windowId, this.minWidth, this.minHeight, this.maxWidth, this.maxHeight);
        glfwShowWindow(this.windowId);
        /*glEnable(GL_DEPTH_TEST);
        if (this.options.showTriangles) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if (this.options.cullFace) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }*/
    }

    public WindowBuilder setVSync(boolean vSync) {
        this.vSync = vSync;
        return this;
    }

}
