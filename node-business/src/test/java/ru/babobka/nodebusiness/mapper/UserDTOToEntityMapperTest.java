package ru.babobka.nodebusiness.mapper;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.util.TextUtil;

import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 12.08.2017.
 */
public class UserDTOToEntityMapperTest {

    private UserDTOToEntityMapper userDTOToEntityMapper;

    @Before
    public void setUp() {
        userDTOToEntityMapper = new UserDTOToEntityMapper();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapNull() {
        userDTOToEntityMapper.map(null);
    }

    @Test
    public void testMap() throws InvalidKeySpecException {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("abc");
        userDTO.setEmail("abc@xyz.ru");
        userDTO.setBase64PubKey(TextUtil.toBase64(KeyDecoder.generateKeyPair().getPublic().getEncoded()));
        User user = userDTOToEntityMapper.map(userDTO);
        assertEquals(userDTO.getName(), user.getName());
        assertEquals(user.getPublicKey(), KeyDecoder.decodePublicKey(userDTO.getBase64PubKey()));
        assertEquals(userDTO.getEmail(), user.getEmail());
    }
}
