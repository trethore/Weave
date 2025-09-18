package tytoo.weave.ui;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.ui.popup.PopupEntry;
import tytoo.weave.ui.shortcuts.ShortcutRegistry;

import java.util.ArrayList;
import java.util.List;

public class UIState {
    private final List<PopupEntry> popups = new ArrayList<>();
    private final List<ShortcutRegistry.Registration> shortcutRegistrations = new ArrayList<>();
    @Nullable
    private Component<?> root;
    @Nullable
    private Panel overlayRoot;
    @Nullable
    private tytoo.weave.theme.Stylesheet stylesheetOverride;
    @Nullable
    private Component<?> hoveredComponent;
    @Nullable
    private Component<?> clickedComponent;
    @Nullable
    private Component<?> focusedComponent;
    @Nullable
    private Component<?> activeComponent;
    private boolean shortcutsInitialized;

    @Nullable
    public Component<?> getRoot() {
        return root;
    }

    public void setRoot(@Nullable Component<?> root) {
        this.root = root;
    }

    @Nullable
    public Panel getOverlayRoot() {
        return overlayRoot;
    }

    public void setOverlayRoot(@Nullable Panel overlayRoot) {
        this.overlayRoot = overlayRoot;
    }

    @Nullable
    public tytoo.weave.theme.Stylesheet getStylesheetOverride() {
        return stylesheetOverride;
    }

    public void setStylesheetOverride(@Nullable tytoo.weave.theme.Stylesheet stylesheetOverride) {
        this.stylesheetOverride = stylesheetOverride;
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

    public List<PopupEntry> getPopups() {
        return popups;
    }

    public List<ShortcutRegistry.Registration> getShortcutRegistrations() {
        return shortcutRegistrations;
    }

    public boolean isShortcutsInitialized() {
        return shortcutsInitialized;
    }

    public void setShortcutsInitialized(boolean shortcutsInitialized) {
        this.shortcutsInitialized = shortcutsInitialized;
    }

    public void clearShortcutRegistrations() {
        for (ShortcutRegistry.Registration registration : shortcutRegistrations) {
            registration.unregister();
        }
        shortcutRegistrations.clear();
        shortcutsInitialized = false;
    }
}
