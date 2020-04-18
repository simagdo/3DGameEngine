package de.simagdo.engine.window;

import de.simagdo.engine.inputsOutputs.windowing.Sync;
import de.simagdo.engine.inputsOutputs.windowing.WindowSizeListener;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class Window {

    private final long windowId;
    private int pixelWidth;
    private int pixelHeight;
    private int desiredWidth;
    private int desiredHeight;
    private int widthScreenCoords;
    private int heightScreenCoords;
    private boolean fullscreen;
    private boolean vSync;
    private final Sync sync;
    private final int fps;
    private final WindowOptions options;
    private List<WindowSizeListener> listeners = new ArrayList<>();

    public static WindowBuilder newWindow(int width, int height, String title, WindowOptions options) {
        return new WindowBuilder(width, height, title, options);
    }

    protected Window(long windowId, int desiredWidth, int desiredHeight, int fps, boolean fullscreen, boolean vSync, WindowOptions options) {
        this.windowId = windowId;
        this.desiredWidth = desiredWidth;
        this.desiredHeight = desiredHeight;
        this.fullscreen = fullscreen;
        this.vSync = vSync;
        this.fps = fps;
        this.sync = new Sync(this.fps);
        this.options = options;
        this.getInitialWindowSizes();
        this.addScreenSizeListener();
        this.addPixelSizeListener();
    }

    public long getWindowId() {
        return windowId;
    }

    public int getPixelWidth() {
        return pixelWidth;
    }

    public void setPixelWidth(int pixelWidth) {
        this.pixelWidth = pixelWidth;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }

    public void setPixelHeight(int pixelHeight) {
        this.pixelHeight = pixelHeight;
    }

    public int getDesiredWidth() {
        return desiredWidth;
    }

    public void setDesiredWidth(int desiredWidth) {
        this.desiredWidth = desiredWidth;
    }

    public int getDesiredHeight() {
        return desiredHeight;
    }

    public void setDesiredHeight(int desiredHeight) {
        this.desiredHeight = desiredHeight;
    }

    public int getWidthScreenCoords() {
        return widthScreenCoords;
    }

    public void setWidthScreenCoords(int widthScreenCoords) {
        this.widthScreenCoords = widthScreenCoords;
    }

    public int getHeightScreenCoords() {
        return heightScreenCoords;
    }

    public void setHeightScreenCoords(int heightScreenCoords) {
        this.heightScreenCoords = heightScreenCoords;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public Sync getSync() {
        return sync;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.sync.setFps(fps);
    }

    public WindowOptions getOptions() {
        return options;
    }

    public void addSizeChangeListener(WindowSizeListener listener) {
        this.listeners.add(listener);
    }

    public List<WindowSizeListener> getListeners() {
        return listeners;
    }

    public float getAspectRatio() {
        return (float) this.pixelWidth / this.pixelHeight;
    }

    private void addScreenSizeListener() {
        glfwSetWindowSizeCallback(this.windowId, (window, width, heigt) -> {
            if (this.validateSizeChange(width, heigt, this.widthScreenCoords, this.heightScreenCoords)) {
                this.widthScreenCoords = width;
                this.heightScreenCoords = heigt;
            }
        });
    }

    private boolean validateSizeChange(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        if (newWidth == 0 || newHeight == 0) return false;
        return newWidth != oldWidth || newHeight != oldHeight;
    }

    private void addPixelSizeListener() {
        glfwSetFramebufferSizeCallback(this.windowId, (window, width, height) -> {
            if (this.validateSizeChange(width, height, this.pixelWidth, this.pixelHeight)) {
                this.pixelWidth = width;
                this.pixelHeight = height;
                this.notifyListeners();
            }
        });
    }

    private void getInitialWindowSizes() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            this.getInitialScreenSize(widthBuffer, heightBuffer);
            this.getInitialPixelSize(widthBuffer, heightBuffer);
        }
    }

    private void getInitialScreenSize(IntBuffer widthBuffer, IntBuffer heightBuffer) {
        glfwGetWindowSize(this.windowId, widthBuffer, heightBuffer);
        this.widthScreenCoords = widthBuffer.get(0);
        this.heightScreenCoords = heightBuffer.get(0);
        widthBuffer.clear();
        heightBuffer.clear();
    }

    private void getInitialPixelSize(IntBuffer widthBuffer, IntBuffer heightBuffer) {
        glfwGetFramebufferSize(this.windowId, widthBuffer, heightBuffer);
        this.pixelWidth = widthBuffer.get(0);
        this.pixelHeight = heightBuffer.get(0);
        widthBuffer.clear();
        heightBuffer.clear();
    }

    public boolean closeButtonPressed() {
        return glfwWindowShouldClose(this.windowId);
    }

    public void update() {
        glfwSwapBuffers(this.windowId);
        glfwPollEvents();
        this.sync.sync();
    }

    public void cleanUp() {
        glfwFreeCallbacks(this.windowId);
        glfwDestroyWindow(this.windowId);
        glfwTerminate();
        //glfwSetErrorCallback(null).free();
    }

    private void notifyListeners() {
        for (WindowSizeListener listener : this.listeners) {
            listener.sizeChanged(this.pixelWidth, this.pixelHeight);
        }
    }

}
