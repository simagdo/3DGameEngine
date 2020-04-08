package de.simagdo.game;

import de.simagdo.engine.window.Window;
import de.simagdo.utils.Utils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Hud {

    private static final String FONT_NAME = "BOLD";
    private long vg;
    private NVGColor colour;
    private ByteBuffer fontBuffer;
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private DoubleBuffer posx;
    private DoubleBuffer posy;
    private int counter;

    public void init(Window window) throws Exception {
        nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (this.vg == NULL) {
            throw new Exception("Could not init nanovg");
        }

        this.fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", 150 * 1024);
        int font = nvgCreateFontMem(vg, FONT_NAME, this.fontBuffer, 0);
        if (font == -1) {
            throw new Exception("Could not add font");
        }
        this.colour = NVGColor.create();

        this.posx = MemoryUtil.memAllocDouble(1);
        this.posy = MemoryUtil.memAllocDouble(1);

        this.counter = 0;
    }

    public void render(Window window) {
        nvgBeginFrame(this.vg, window.getPixelWidth(), window.getPixelHeight(), 1);

        // Upper ribbon
        nvgBeginPath(this.vg);
        nvgRect(this.vg, 0, window.getPixelHeight() - 100, window.getPixelWidth(), 50);
        nvgFillColor(this.vg, rgba(0x23, 0xa1, 0xf1, 200, colour));
        nvgFill(this.vg);

        // Lower ribbon
        nvgBeginPath(this.vg);
        nvgRect(this.vg, 0, window.getPixelHeight() - 50, window.getPixelWidth(), 10);
        nvgFillColor(this.vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(this.vg);

        glfwGetCursorPos(window.getWindowId(), posx, posy);
        int xcenter = 50;
        int ycenter = window.getPixelHeight() - 75;
        int radius = 20;
        int x = (int) posx.get(0);
        int y = (int) posy.get(0);
        boolean hover = Math.pow(x - xcenter, 2) + Math.pow(y - ycenter, 2) < Math.pow(radius, 2);

        // Circle
        nvgBeginPath(this.vg);
        nvgCircle(this.vg, xcenter, ycenter, radius);
        nvgFillColor(this.vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(this.vg);

        // Clicks Text
        nvgFontSize(this.vg, 25.0f);
        nvgFontFace(this.vg, FONT_NAME);
        nvgTextAlign(this.vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (hover) {
            nvgFillColor(this.vg, rgba(0x00, 0x00, 0x00, 255, colour));
        } else {
            nvgFillColor(this.vg, rgba(0x23, 0xa1, 0xf1, 255, colour));

        }
        nvgText(this.vg, 50, window.getPixelHeight() - 87, String.format("%02d", counter));

        // Render hour text
        nvgFontSize(this.vg, 40.0f);
        nvgFontFace(this.vg, FONT_NAME);
        nvgTextAlign(this.vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(this.vg, rgba(0xe6, 0xea, 0xed, 255, colour));
        nvgText(this.vg, window.getPixelWidth() - 150, window.getPixelHeight() - 95, dateFormat.format(new Date()));

        nvgEndFrame(this.vg);

    }

    public void incCounter() {
        this.counter++;
        if (this.counter > 99) {
            this.counter = 0;
        }
    }

    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }

    public void cleanUp() {
        nvgDelete(vg);
        if (posx != null) {
            MemoryUtil.memFree(posx);
        }
        if (posy != null) {
            MemoryUtil.memFree(posy);
        }
    }
}