package ru.babobka.dlp.model.regular;

import org.junit.Test;
import ru.babobka.dlp.model.PollardDlpDataValidators;
import ru.babobka.dlp.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 28.01.2018.
 */
public class PollardDlpDataValidatorsTest {

    private PollardDlpDataValidators pollardDlpDataValidators = new PollardDlpDataValidators();

    @Test
    public void testIsValidResponseImplNotNormalStatus() {
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.FAILED);
        assertFalse(pollardDlpDataValidators.isValidResponse(response));
    }

    @Test
    public void testIsValidResponseImplNullValues() {
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.NORMAL);
        assertFalse(pollardDlpDataValidators.isValidResponse(response));
    }

    @Test
    public void testIsValidResponseImplNotDlpSolution() {
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.NORMAL);
        BigInteger x = BigInteger.ONE;
        BigInteger y = BigInteger.valueOf(2);
        BigInteger mod = BigInteger.TEN;
        BigInteger exp = BigInteger.ONE;
        when(response.getDataValue(Params.X.getValue())).thenReturn(x);
        when(response.getDataValue(Params.Y.getValue())).thenReturn(y);
        when(response.getDataValue(Params.MOD.getValue())).thenReturn(mod);
        when(response.getDataValue(Params.EXP.getValue())).thenReturn(exp);
        assertFalse(pollardDlpDataValidators.isValidResponse(response));
    }

    @Test
    public void testIsValidResponseImpl() {
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.NORMAL);
        BigInteger x = BigInteger.ONE;
        BigInteger y = BigInteger.ONE;
        BigInteger mod = BigInteger.TEN;
        BigInteger exp = BigInteger.ONE;
        when(response.getDataValue(Params.X.getValue())).thenReturn(x);
        when(response.getDataValue(Params.Y.getValue())).thenReturn(y);
        when(response.getDataValue(Params.MOD.getValue())).thenReturn(mod);
        when(response.getDataValue(Params.EXP.getValue())).thenReturn(exp);
        assertTrue(pollardDlpDataValidators.isValidResponse(response));
    }

    @Test
    public void testIsValidRequestImplNullValues() {
        NodeRequest request = mock(NodeRequest.class);
        assertFalse(pollardDlpDataValidators.isValidRequest(request));
    }

    @Test
    public void testIsValidRequestImplZeroX() {
        NodeRequest request = mock(NodeRequest.class);
        BigInteger x = BigInteger.ZERO;
        BigInteger y = BigInteger.ONE;
        BigInteger mod = BigInteger.TEN;
        when(request.getDataValue(Params.X.getValue())).thenReturn(x);
        when(request.getDataValue(Params.Y.getValue())).thenReturn(y);
        when(request.getDataValue(Params.MOD.getValue())).thenReturn(mod);
        assertFalse(pollardDlpDataValidators.isValidRequest(request));
    }

    @Test
    public void testIsValidRequestImplZeroY() {
        NodeRequest request = mock(NodeRequest.class);
        BigInteger x = BigInteger.ONE;
        BigInteger y = BigInteger.ZERO;
        BigInteger mod = BigInteger.TEN;
        when(request.getDataValue(Params.X.getValue())).thenReturn(x);
        when(request.getDataValue(Params.Y.getValue())).thenReturn(y);
        when(request.getDataValue(Params.MOD.getValue())).thenReturn(mod);
        assertFalse(pollardDlpDataValidators.isValidRequest(request));
    }

    @Test
    public void testIsValidRequestImplBadMod() {
        NodeRequest request = mock(NodeRequest.class);
        BigInteger x = BigInteger.ONE;
        BigInteger y = BigInteger.ONE;
        BigInteger mod = BigInteger.ONE;
        when(request.getDataValue(Params.X.getValue())).thenReturn(x);
        when(request.getDataValue(Params.Y.getValue())).thenReturn(y);
        when(request.getDataValue(Params.MOD.getValue())).thenReturn(mod);
        assertFalse(pollardDlpDataValidators.isValidRequest(request));
    }

    @Test
    public void testIsValidRequestImpl() {
        NodeRequest request = mock(NodeRequest.class);
        BigInteger x = BigInteger.ONE;
        BigInteger y = BigInteger.ONE;
        BigInteger mod = BigInteger.TEN;
        when(request.getDataValue(Params.X.getValue())).thenReturn(x);
        when(request.getDataValue(Params.Y.getValue())).thenReturn(y);
        when(request.getDataValue(Params.MOD.getValue())).thenReturn(mod);
        assertTrue(pollardDlpDataValidators.isValidRequest(request));
    }
}
