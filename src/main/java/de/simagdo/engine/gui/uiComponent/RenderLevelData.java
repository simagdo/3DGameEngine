package de.simagdo.engine.gui.uiComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class RenderLevelData {

    private int level;
    private Collection<UIBlock> uiElements = new ArrayList<>();

    protected RenderLevelData(int level) {
        this.level = level;
    }

    protected int getLevel() {
        return this.level;
    }

    public void addUIRenderData(UIBlock data) {
        this.uiElements.add(data);
    }

    public Collection<UIBlock> getUiElements() {
        return this.uiElements;
    }
}
