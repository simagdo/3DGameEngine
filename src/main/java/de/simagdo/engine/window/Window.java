package de.simagdo.engine.window;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private final String title;
    private int width;
    private int height;
    private long windowHandle;
    private boolean resized;
    private boolean vSync;
    private WindowOptions windowOptions;
    private Matrix4f projectionMatrix;

    public Window(String title, int width, int height, boolean vSync, WindowOptions windowOptions) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resized = false;
        this.vSync = vSync;
        this.windowOptions = windowOptions;
        this.projectionMatrix = new Matrix4f();
    }

    public void init() {
        //Setup an error Callback. The default implementation will print the Error Message in System.err
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialize GLFW. Most GLFW functijons will not work before doing this
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW!");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

        if (this.windowOptions.compatibleProfile) {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        } else {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }

        boolean maximized = false;
        //If no size has been specified set it to maximized state
        if (width == 0 || height == 0) {
            //Set up a fixed width and height so window initialization does not fail
            this.width = 100;
            this.height = 100;
            glfwWindowHint(GLFW_MAXIMIZED, GL_TRUE);
            maximized = true;
        }

        // Create the window
        this.windowHandle = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (this.windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(this.windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(this.windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        if (!maximized) {
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            // Center our window
            glfwSetWindowPos(
                    windowHandle,
                    (vidmode.width() - this.width) / 2,
                    (vidmode.height() - this.height) / 2
            );
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(this.windowHandle);

        if (this.isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(this.windowHandle);

        createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        if (this.windowOptions.showTriangles) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        //Support for Transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (this.windowOptions.cullFace) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }

        if (this.windowOptions.antialiasing) {
            glfwWindowHint(GLFW_SAMPLES, 4);
        }

    }

    public long getWindowHandle() {
        return this.windowHandle;
    }

    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r, g, b, alpha);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWindowHandle(long windowHandle) {
        this.windowHandle = windowHandle;
    }

    public WindowOptions getWindowOptions() {
        return windowOptions;
    }

    public void setWindowOptions(WindowOptions windowOptions) {
        this.windowOptions = windowOptions;
    }

    public void setWindowTitle(String title) {
        glfwSetWindowTitle(this.windowHandle, title);
    }

    public void update() {
        glfwSwapBuffers(this.windowHandle);
        glfwPollEvents();
    }


    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width / (float) height;
        return projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public void restoreState() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if (this.windowOptions.cullFace) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
    }

}
