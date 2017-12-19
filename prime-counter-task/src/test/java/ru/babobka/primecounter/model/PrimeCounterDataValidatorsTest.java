package ru.babobka.primecounter.model;

import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.primecounter.task.Params;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 22.10.2017.
 */
public class PrimeCounterDataValidatorsTest {

    private final PrimeCounterDataValidators primeCounterDataValidators = new PrimeCounterDataValidators();

    @Test
    public void testValidResponseBadStatus() {
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.FAILED);
        assertFalse(primeCounterDataValidators.isValidResponse(response));
    }

    @Test
    public void testValidResponseNullPrimeCount() {
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.NORMAL);
        when(response.getDataValue(Params.PRIME_COUNT.getValue())).thenReturn(null);
        assertFalse(primeCounterDataValidators.isValidResponse(response));
    }

    @Test
    public void testValidResponseNegativePrimeCounter() {
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.NORMAL);
        when(response.getDataValue(Params.PRIME_COUNT.getValue())).thenReturn(-1);
        assertFalse(primeCounterDataValidators.isValidResponse(response));
    }

    @Test
    public void testValidResponse() {
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.NORMAL);
        when(response.getDataValue(Params.PRIME_COUNT.getValue())).thenReturn(12);
        assertTrue(primeCounterDataValidators.isValidResponse(response));
    }

    @Test
    public void testIsValidRequestArgumentsEmptyArguments() {
        assertFalse(primeCounterDataValidators.isValidRequest(mock(NodeRequest.class)));
    }

    @Test
    public void testIsValidRequestArgumentsNoBegin() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getDataValue(Params.END.getValue())).thenReturn(123L);
        assertFalse(primeCounterDataValidators.isValidRequest(request));
    }

    @Test
    public void testIsValidRequestArgumentsNoEnd() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getDataValue(Params.BEGIN.getValue())).thenReturn(123L);
        assertFalse(primeCounterDataValidators.isValidRequest(request));
    }

    @Test
    public void testIsValidRequestArgumentsBadRange() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getDataValue(Params.BEGIN.getValue())).thenReturn(123L);
        when(request.getDataValue(Params.END.getValue())).thenReturn(0L);
        assertFalse(primeCounterDataValidators.isValidRequest(request));
    }

    @Test
    public void testIsValidRequestArguments() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getDataValue(Params.BEGIN.getValue())).thenReturn(0L);
        when(request.getDataValue(Params.END.getValue())).thenReturn(123L);
        assertTrue(primeCounterDataValidators.isValidRequest(request));
    }
}
