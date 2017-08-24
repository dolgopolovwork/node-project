package ru.babobka.nodebusiness.mapper;

import org.junit.Test;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodeutils.util.HashUtil;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 12.08.2017.
 */
public class UserDTOMapperTest {

    private UserDTOMapper userDTOMapper = new UserDTOMapper();

    private PodamFactory podamFactory = new PodamFactoryImpl();

    @Test(expected = IllegalArgumentException.class)
    public void testMapNull() {
        userDTOMapper.map(null);
    }


    @Test
    public void testMap() {
        UserDTO userDTO = podamFactory.manufacturePojo(UserDTO.class);
        User user = userDTOMapper.map(userDTO);
        assertEquals(userDTO.getName(), user.getName());
        assertEquals(userDTO.getTaskCount(), user.getTaskCount());
        assertArrayEquals(HashUtil.sha2(userDTO.getPassword()), user.getHashedPassword());
        assertEquals(userDTO.getEmail(), user.getEmail());
    }
}
