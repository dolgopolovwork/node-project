package ru.babobka.nodeslaveserver.task;

import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.RequestStatus;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RaceStyleTaskStorageTest {

    @Test
    public void testIsRepeatedRaceNotRepeated() {
        UUID id = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getRequestStatus()).thenReturn(RequestStatus.RACE);
        when(request.getTaskId()).thenReturn(id);
        RaceStyleTaskStorage raceStyleTaskStorage = new RaceStyleTaskStorage();
        assertFalse(raceStyleTaskStorage.isRepeated(request));
    }

    @Test
    public void testIsRepeatedNotRace() {
        UUID id = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getRequestStatus()).thenReturn(RequestStatus.NORMAL);
        when(request.getTaskId()).thenReturn(id);
        RaceStyleTaskStorage raceStyleTaskStorage = new RaceStyleTaskStorage();
        assertFalse(raceStyleTaskStorage.isRepeated(request));
        assertFalse(raceStyleTaskStorage.isRepeated(request));
    }

    @Test
    public void testIsRepeatedRaceTrueRepeated() {
        UUID id = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getRequestStatus()).thenReturn(RequestStatus.RACE);
        when(request.getTaskId()).thenReturn(id);
        RaceStyleTaskStorage raceStyleTaskStorage = new RaceStyleTaskStorage();
        raceStyleTaskStorage.isRepeated(request);
        assertTrue(raceStyleTaskStorage.isRepeated(request));
    }

    @Test
    public void testUnregisterRepeated() {
        UUID id = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getRequestStatus()).thenReturn(RequestStatus.RACE);
        when(request.getTaskId()).thenReturn(id);
        RaceStyleTaskStorage raceStyleTaskStorage = new RaceStyleTaskStorage();
        raceStyleTaskStorage.isRepeated(request);
        raceStyleTaskStorage.unregister(request);
        assertFalse(raceStyleTaskStorage.isRepeated(request));
    }

    @Test
    public void testUnregisterRepeatedDifferent() {
        NodeRequest request1 = mock(NodeRequest.class);
        when(request1.getRequestStatus()).thenReturn(RequestStatus.RACE);
        when(request1.getTaskId()).thenReturn(UUID.randomUUID());
        NodeRequest request2 = mock(NodeRequest.class);
        when(request2.getRequestStatus()).thenReturn(RequestStatus.RACE);
        when(request2.getTaskId()).thenReturn(UUID.randomUUID());
        RaceStyleTaskStorage raceStyleTaskStorage = new RaceStyleTaskStorage();
        assertFalse(raceStyleTaskStorage.isRepeated(request1));
        assertFalse(raceStyleTaskStorage.isRepeated(request2));
        raceStyleTaskStorage.unregister(request2);
        assertTrue(raceStyleTaskStorage.isRepeated(request1));
        assertFalse(raceStyleTaskStorage.isRepeated(request2));
    }

    @Test
    public void testClear() {
        NodeRequest request1 = mock(NodeRequest.class);
        when(request1.getRequestStatus()).thenReturn(RequestStatus.RACE);
        when(request1.getTaskId()).thenReturn(UUID.randomUUID());
        NodeRequest request2 = mock(NodeRequest.class);
        when(request2.getRequestStatus()).thenReturn(RequestStatus.RACE);
        when(request2.getTaskId()).thenReturn(UUID.randomUUID());
        RaceStyleTaskStorage raceStyleTaskStorage = new RaceStyleTaskStorage();
        assertFalse(raceStyleTaskStorage.isRepeated(request1));
        assertFalse(raceStyleTaskStorage.isRepeated(request2));
        raceStyleTaskStorage.clear();
        assertFalse(raceStyleTaskStorage.isRepeated(request1));
        assertFalse(raceStyleTaskStorage.isRepeated(request2));
    }

    @Test
    public void testIsRepeatedRaceDifferentTasks() {
        NodeRequest request1 = mock(NodeRequest.class);
        when(request1.getRequestStatus()).thenReturn(RequestStatus.RACE);
        when(request1.getTaskId()).thenReturn(UUID.randomUUID());
        NodeRequest request2 = mock(NodeRequest.class);
        when(request2.getRequestStatus()).thenReturn(RequestStatus.RACE);
        when(request2.getTaskId()).thenReturn(UUID.randomUUID());
        RaceStyleTaskStorage raceStyleTaskStorage = new RaceStyleTaskStorage();
        raceStyleTaskStorage.isRepeated(request1);
        assertFalse(raceStyleTaskStorage.isRepeated(request2));
    }
}
