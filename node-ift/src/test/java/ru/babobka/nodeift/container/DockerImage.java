package ru.babobka.nodeift.container;

import lombok.NonNull;

public enum DockerImage {
    MASTER("test/node-project-master"),
    SLAVE("test/node-project-slave"),
    SUBMASTER("test/node-project-submaster");
    private final String imageName;

    DockerImage(@NonNull String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }
}
