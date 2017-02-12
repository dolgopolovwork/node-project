package ru.babobka.nodeutils.util;

import ru.babobka.nodeserials.NodeRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dolgopolov.a on 06.07.15.
 */
public interface MathUtil {

    static byte[] sha2(String message) {
	if (message != null) {
	    try {
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		byte[] messageBytes = message.getBytes(TextUtil.CHARSET);
		return sha256.digest(messageBytes);
	    } catch (NoSuchAlgorithmException e) {
		e.printStackTrace();

	    }
	}
	return new byte[256];
    }

    static NodeRequest[] subArray(NodeRequest[] requests, int beginIndex) {
	if (requests.length >= beginIndex) {
	    int newSize = requests.length - beginIndex;
	    NodeRequest[] newRequests = new NodeRequest[newSize];
	    int j = 0;
	    for (int i = beginIndex; i < requests.length; i++) {
		newRequests[j] = requests[i];
		j++;
	    }
	    return newRequests;
	} else {
	    throw new IllegalArgumentException("requests size is " + requests.length + " beginIndex is " + beginIndex);
	}
    }

}