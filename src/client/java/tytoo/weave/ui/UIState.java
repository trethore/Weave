package tytoo.weave.ui;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;

public class UIState {
    @Nullable
    private Component<?> root;
    @Nullable
    private Component<?> hoveredComponent;
    @Nullable
    private Component<?> clickedComponent;
    @Nullable
    private Component<?> focusedComponent;
    @Nullable
    private Component<?> activeComponent;

    @Nullable
    public Component<?> getRoot() {
        return root;
    }

    public void setRoot(@Nullable Component<?> root) {
        this.root = root;
    }

    @Nullable
    public Component<?> getHoveredComponent() {
        return hoveredComponent;
    }

    public void setHoveredComponent(@Nullable Component<?> hoveredComponent) {
        this.hoveredComponent = hoveredComponent;
    }

    @Nullable
    public Component<?> getClickedComponent() {
        return clickedComponent;
    }

    public void setClickedComponent(@Nullable Component<?> clickedComponent) {
        this.clickedComponent = clickedComponent;
    }

    @Nullable
    public Component<?> getFocusedComponent() {
        return focusedComponent;
    }

    public void setFocusedComponent(@Nullable Component<?> focusedComponent) {
        this.focusedComponent = focusedComponent;
    }

    @Nullable
    public Component<?> getActiveComponent() {
        return activeComponent;
    }

    public void setActiveComponent(@Nullable Component<?> activeComponent) {
        this.activeComponent = activeComponent;
    }
}