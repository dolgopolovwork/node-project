package ru.babobka.nodebusiness.mapper;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodeutils.func.Mapper;

public class UserEntityToDTOMapper extends Mapper<User, UserDTO> {

    @Override
    protected UserDTO mapImpl(User entity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(entity.getName());
        userDTO.setId(entity.getId());
        userDTO.setEmail(entity.getEmail());
        userDTO.setBase64PubKey(entity.getPublicKeyBase64());
        return userDTO;
    }
}
