package ru.babobka.nodebusiness.mapper;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodeutils.func.Mapper;

import java.util.UUID;

/**
 * Created by 123 on 09.08.2017.
 */
public class UserDTOMapper extends Mapper<UserDTO, User> {
    @Override
    public User mapImpl(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("userDTO is null");
        }
        User user = new User();
        if (userDTO.getName() != null)
            user.setName(userDTO.getName());
        if (userDTO.getTaskCount() != null)
            user.setTaskCount(userDTO.getTaskCount());
        if (userDTO.getHashedPassword() != null)
            user.setHashedPassword(userDTO.getHashedPassword());
        if (userDTO.getEmail() != null)
            user.setEmail(userDTO.getEmail());
        user.setId(UUID.randomUUID());
        return user;
    }
}
