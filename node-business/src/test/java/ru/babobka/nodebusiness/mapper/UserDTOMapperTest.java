package ru.babobka.nodebusiness.mapper;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.DebugBase64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;

import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 12.08.2017.
 */
public class UserDTOMapperTest {

    private UserDTOMapper userDTOMapper;

    @Before
    public void setUp() {
        userDTOMapper = new UserDTOMapper();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapNull() {
        userDTOMapper.map(null);
    }

    @Test
    public void testMap() throws InvalidKeySpecException {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("abc");
        userDTO.setEmail("abc@xyz.ru");
        userDTO.setBase64PubKey(DebugBase64KeyPair.DEBUG_PUB_KEY);
        User user = userDTOMapper.map(userDTO);
        assertEquals(userDTO.getName(), user.getName());
        assertEquals(user.getPublicKey(), KeyDecoder.decodePublicKey(userDTO.getBase64PubKey()));
        assertEquals(userDTO.getEmail(), user.getEmail());
    }
}
