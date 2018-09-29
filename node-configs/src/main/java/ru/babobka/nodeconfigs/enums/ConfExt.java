package ru.babobka.nodeconfigs.enums;

/**
 * Created by 123 on 13.09.2018.
 */
public enum ConfExt {
    ENCRYPTED(".encrypted"), JSON(".json");
    public final String extension;

    ConfExt(String extension) {
        this.extension = extension;
    }
}
