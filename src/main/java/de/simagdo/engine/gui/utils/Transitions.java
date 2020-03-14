package de.simagdo.engine.gui.utils;

import de.simagdo.engine.gui.transitions.SlideTransition;
import de.simagdo.engine.gui.transitions.Transition;
import de.simagdo.engine.gui.transitions.TransitionType;

//TODO Not all Methods are included
public class Transitions {

    public static Transition fade(float length) {
        return new Transition().add(TransitionType.ALPHA, new SlideTransition(0, length));
    }

    public static Transition scaleUpFromLeft(float scale, float length) {
        Transition transition = new Transition();
        transition.add(TransitionType.WIDTH, new SlideTransition(scale, length));
        transition.add(TransitionType.Y_POS, new SlideTransition(-(scale - 1) / 2f, length));
        transition.add(TransitionType.HEIGHT, new SlideTransition(scale, length));
        return transition;
    }

    public static Transition slideXAndFade(float distance, float length) {
        Transition transition = new Transition();
        transition.add(TransitionType.X_POS, new SlideTransition(distance, length));
        transition.add(TransitionType.ALPHA, new SlideTransition(0, length));
        return transition;
    }

}