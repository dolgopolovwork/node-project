package ru.babobka.nodebusiness.mapper;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.func.Mapper;

import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

/**
 * Created by 123 on 09.08.2017.
 */
public class UserDTOMapper extends Mapper<UserDTO, User> {

    @Override
    public User mapImpl(UserDTO userDTO) {
        User user = new User();
        if (userDTO.getName() != null)
            user.setName(userDTO.getName());
        if (userDTO.getBase64PubKey() != null) {
            user.setPublicKeyBase64(userDTO.getBase64PubKey());
        }
        if (userDTO.getEmail() != null)
            user.setEmail(userDTO.getEmail());
        user.setId(userDTO.getId());
        return user;
    }
}
