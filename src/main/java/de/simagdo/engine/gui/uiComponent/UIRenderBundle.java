package de.simagdo.engine.gui.uiComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIRenderBundle {

    private Map<Integer, RenderLevelData> renderData = new HashMap<>();

    public void addUIRenderData(UIBlock block) {
        RenderLevelData levelData = this.getLevelData(block.getLevel());
        levelData.addUIRenderData(block);
    }

    //TODO
    public void addText() {

    }

    public List<RenderLevelData> getRenderData() {
        List<RenderLevelData> levelData = new ArrayList<>();
        for (RenderLevelData data : this.renderData.values()) {
            this.sortLevelDataIntoList(levelData, data);
        }
        return levelData;
    }

    private RenderLevelData getLevelData(int level) {
        RenderLevelData levelData = this.renderData.get(level);
        if (levelData != null) return levelData;
        levelData = new RenderLevelData(level);
        this.renderData.put(level, levelData);
        return levelData;
    }

    private void sortLevelDataIntoList(List<RenderLevelData> list, RenderLevelData levelData) {
        for (int i = 0; i < list.size(); i++) {
            if (levelData.getLevel() < list.get(i).getLevel()) {
                list.add(i, levelData);
                return;
            }
        }
        list.add(levelData);
    }

}
