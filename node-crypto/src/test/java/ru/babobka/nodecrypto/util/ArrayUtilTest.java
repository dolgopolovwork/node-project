package ru.babobka.nodecrypto.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 15.08.2017.
 */
public class ArrayUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void testGetLastBlockEmptyArray() {
        ArrayUtil.getLastBlock(new byte[]{}, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLastBlockNullArray() {
        ArrayUtil.getLastBlock(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLastBlockNegativeBlockSize() {
        ArrayUtil.getLastBlock(new byte[]{1, 2, 3}, -1);
    }

    @Test
    public void testGetLastBlock() {
        byte[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertArrayEquals(new byte[]{7, 8, 9}, ArrayUtil.getLastBlock(array, 3));
    }

    @Test
    public void testGetLastBlockNotComplete() {
        byte[] array = {1, 2, 3, 4, 5, 6, 7, 8};
        assertArrayEquals(new byte[]{7, 8}, ArrayUtil.getLastBlock(array, 3));
    }

    @Test
    public void testGetLastBlockFullArray() {
        byte[] array = {1, 2, 3, 4, 5, 6, 7, 8};
        assertArrayEquals(array, ArrayUtil.getLastBlock(array, array.length));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomArrayNegativeLength() {
        ArrayUtil.randomArray(-1);
    }

    @Test
    public void testRandomArray() {
        int length = 10;
        assertEquals(ArrayUtil.randomArray(length).length, length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConcatNullArrays() {
        ArrayUtil.concat(null, null);
    }

    @Test
    public void testConcat() {
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6}, ArrayUtil.concat(new byte[]{1, 2, 3}, new byte[]{4, 5, 6}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFirstBlockNullArray() {
        ArrayUtil.getFirstBlock(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFirstBlockEmptyArray() {
        ArrayUtil.getFirstBlock(new byte[]{}, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFirstBlockNegativeSize() {
        ArrayUtil.getFirstBlock(new byte[]{1, 2, 3}, -1);
    }

    @Test
    public void testGetFirstBlock() {
        byte[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertArrayEquals(new byte[]{1, 2, 3}, ArrayUtil.getFirstBlock(array, 3));
    }

    @Test
    public void testGetFirstBlockFullArray() {
        byte[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertArrayEquals(array, ArrayUtil.getFirstBlock(array, array.length));
    }

    @Test
    public void testGetFirstBlockBlockBigBlockSize() {
        byte[] array = {1, 2, 3};
        assertArrayEquals(array, ArrayUtil.getFirstBlock(array, array.length + 1));
    }

}
