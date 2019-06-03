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
            try {
                user.setPublicKey(KeyDecoder.decodePublicKey(userDTO.getBase64PubKey()));
            } catch (InvalidKeySpecException e) {
                throw new IllegalArgumentException("cannot map user", e);
            }
        }
        if (userDTO.getEmail() != null)
            user.setEmail(userDTO.getEmail());
        user.setId(UUID.randomUUID());
        return user;
    }
}
