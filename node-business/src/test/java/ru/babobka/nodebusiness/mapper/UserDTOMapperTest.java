package ru.babobka.nodebusiness.mapper;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.HashUtil;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 12.08.2017.
 */
public class UserDTOMapperTest {

    private UserDTOMapper userDTOMapper;
    private SRPService SRPService;
    private SrpConfig srpConfig;

    @Before
    public void setUp() {
        SRPService = mock(SRPService.class);
        srpConfig = mock(SrpConfig.class);
        Container.getInstance().put(container -> {
            container.put(SRPService);
            container.put(srpConfig);
        });
        userDTOMapper = new UserDTOMapper();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapNull() {
        userDTOMapper.map(null);
    }

    @Test
    public void testMap() {
        String password = "test";
        UserDTO userDTO = new UserDTO();
        userDTO.setName("abc");
        userDTO.setEmail("abc@xyz.ru");
        userDTO.setHashedPassword(HashUtil.hexSha2(password));
        byte[] secret = {1, 2, 3};
        when(SRPService.secretBuilder(eq(HashUtil.hexStringToByteArray(userDTO.getHashedPassword())), any(), eq(srpConfig))).thenReturn(secret);
        User user = userDTOMapper.map(userDTO);
        assertEquals(userDTO.getName(), user.getName());
        assertArrayEquals(user.getSecret(), secret);
        assertEquals(userDTO.getEmail(), user.getEmail());
    }
}
