package io.eberlein.insane.bluepwn.object;

public class SettingsItem {
    private String title;
    private Class c;

    public SettingsItem(String title, Class c) {
        this.title = title;
        this.c = c;
    }

    public String getTitle() {
        return title;
    }

    public Class getC() {
        return c;
    }
}
