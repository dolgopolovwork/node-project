package ru.babobka.nodemasterserver.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 04.11.2017.
 */
public class ClientStorageTest {

    private ClientStorage clientStorage;
    private NodeLogger nodeLogger;

    @Before
    public void setUp() {
        nodeLogger = mock(NodeLogger.class);
        Container.getInstance().put(nodeLogger);
        clientStorage = new ClientStorage();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateClient() {
        clientStorage.validateClient(null);
    }

    @Test
    public void testHeartBeatAllClients() throws IOException {
        Client client = mock(Client.class);
        List<Client> clients = Arrays.asList(client, client, client);
        clientStorage.addAll(clients);
        clientStorage.heartBeatAllClients();
        verify(client, times(clients.size())).sendHeartBeating();
    }

    @Test
    public void testHeartBeatAllClientsOneException() throws IOException {
        Client client1 = mock(Client.class);
        Client exceptionClient = mock(Client.class);
        doThrow(new IOException()).when(exceptionClient).sendHeartBeating();
        Client client2 = mock(Client.class);
        List<Client> clients = Arrays.asList(client1, exceptionClient, client2);
        clientStorage.addAll(clients);
        clientStorage.heartBeatAllClients();
        verify(client1).sendHeartBeating();
        verify(client2).sendHeartBeating();
        verify(nodeLogger).error(any(Exception.class));
    }

    @Test
    public void testClear() {
        Client client = mock(Client.class);
        List<Client> clients = Arrays.asList(client, client, client);
        clientStorage.addAll(clients);
        clientStorage.clear();
        verify(client, times(clients.size())).close();
        assertTrue(clientStorage.isEmpty());
        assertEquals(clientStorage.getSize(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAllNull() {
        clientStorage.addAll(null);
    }

    @Test
    public void testAddAll() {
        Client client = mock(Client.class);
        List<Client> clients = Arrays.asList(client, client, client);
        clientStorage.addAll(clients);
        assertFalse(clientStorage.isEmpty());
        assertEquals(clientStorage.getSize(), clients.size());
    }

    @Test
    public void testRemove() {
        Client client = mock(Client.class);
        clientStorage.add(client);
        clientStorage.remove(client);
        assertTrue(clientStorage.isEmpty());
        assertEquals(clientStorage.getSize(), 0);
    }

    @Test
    public void testRemoveExactlyClient() {
        Client client1 = mock(Client.class);
        Client client2 = mock(Client.class);
        clientStorage.addAll(Arrays.asList(client1, client2));
        clientStorage.remove(client2);
        assertFalse(clientStorage.isEmpty());
        assertEquals(clientStorage.getSize(), 1);
        assertTrue(clientStorage.contains(client1));
        assertFalse(clientStorage.contains(client2));
    }

    @Test
    public void testClearException() {
        Client client = mock(Client.class);
        doThrow(new RuntimeException()).when(client).close();
        List<Client> clients = Arrays.asList(client, client, client);
        clientStorage.addAll(clients);
        clientStorage.clear();
        verify(nodeLogger, times(clients.size())).error(any(Exception.class));
        assertTrue(clientStorage.isEmpty());
        assertEquals(clientStorage.getSize(), 0);
    }
}
