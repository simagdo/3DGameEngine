package de.simagdo.engine.gui.mainUI;

import de.simagdo.engine.gui.uiComponent.UIComponent;

public class UIContainer extends UIComponent {

    protected UIContainer() {
        super.forceInitialization(0, 0, 1, 1);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void updateSelf() {

    }

    @Override
    public boolean isMouseOver() {
        for (UIComponent child : super.getChildren()) {
            if (child.isMouseOver()) return true;
        }
        return false;
    }

}
