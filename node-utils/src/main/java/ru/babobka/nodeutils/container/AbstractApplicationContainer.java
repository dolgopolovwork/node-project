package ru.babobka.nodeutils.container;

public abstract class AbstractApplicationContainer {

    public void contain(Container container) {
        try {
            containImpl(container);
        } catch (Exception e) {
            container.clear();
            throw new ContainerException(e);
        }
    }

    protected abstract void containImpl(Container container) throws Exception;

}
