package ru.babobka.submaster.listener;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodemasterserver.listener.SlaveStorageChangeListener;
import ru.babobka.nodeutils.waiter.Waiter;
import ru.babobka.submaster.SubMasterApp;

public class GotSlavesListener implements SlaveStorageChangeListener {

    private static final Logger logger = Logger.getLogger(SubMasterApp.class);
    private final Waiter slaveCreationWaiter;

    public GotSlavesListener(@NonNull Waiter slaveCreationWaiter) {
        this.slaveCreationWaiter = slaveCreationWaiter;
    }

    @Override
    public void onChange(SlaveStorageChangeType changeType, int currentSize) {
        switch (changeType) {
            case ADD: {
                if (currentSize == 1) {
                    slaveCreationWaiter.able();
                }
                return;
            }
            case REMOVE: {
                if (currentSize == 0) {
                    slaveCreationWaiter.disable();
                }
                return;
            }
            case CLEAR: {
                logger.info("all the slaves are gone. goodbye.");
            }
        }
    }

}
