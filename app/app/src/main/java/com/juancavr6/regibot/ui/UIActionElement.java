package com.juancavr6.regibot.ui;

import java.io.Serializable;

public class UIActionElement implements Serializable {
    private String id;
    private String iconDrawableName;
    private int nameSource;
    private boolean isEnabled;

    public UIActionElement(String id, String iconDrawable, int name, boolean isActivated) {
        this.id = id;
        this.iconDrawableName = iconDrawable;
        this.nameSource = name;
        this.isEnabled = isActivated;
    }

    public String getIconDrawableName() {
        return iconDrawableName;
    }

    public void setIconDrawableName(String iconDrawableName) {
        this.iconDrawableName = iconDrawableName;
    }

    public int getNameSource() {
        return nameSource;
    }

    public void setNameSource(int nameSource) {
        this.nameSource = nameSource;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
