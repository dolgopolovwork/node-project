package ru.babobka.nodesecurity.service;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 05.09.2018.
 */
public class SecureJSONServiceTest {

    private static final AESService aesService = new AESService();
    private static SecureJSONService secureJSONService;

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put(aesService);
        secureJSONService = new SecureJSONService();
    }

    @Test
    public void testEncryptDecryptJson() throws IOException {
        String password = "abc";
        TestPojo testPojo = new TestPojo();
        testPojo.setFlag(true);
        testPojo.setNumber(31);
        testPojo.setText("abc");
        byte[] cipher = secureJSONService.encrypt(testPojo, password);
        assertEquals(testPojo, secureJSONService.decrypt(cipher, password, TestPojo.class));
    }

    private class TestPojo implements Serializable {
        private static final long serialVersionUID = -3693756680502231729L;
        private int number;
        private String text;
        private boolean flag;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestPojo testPojo = (TestPojo) o;

            if (number != testPojo.number) return false;
            if (flag != testPojo.flag) return false;
            return text != null ? text.equals(testPojo.text) : testPojo.text == null;
        }

        @Override
        public int hashCode() {
            int result = number;
            result = 31 * result + (text != null ? text.hashCode() : 0);
            result = 31 * result + (flag ? 1 : 0);
            return result;
        }
    }
}
