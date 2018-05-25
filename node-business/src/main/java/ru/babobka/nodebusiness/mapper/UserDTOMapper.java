package ru.babobka.nodebusiness.mapper;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.Mapper;
import ru.babobka.nodeutils.util.HashUtil;

import java.util.UUID;

/**
 * Created by 123 on 09.08.2017.
 */
public class UserDTOMapper extends Mapper<UserDTO, User> {
    private static final int SALT_SIZE = 8;
    private final SRPService SRPService = Container.getInstance().get(SRPService.class);
    private final SrpConfig srpConfig = Container.getInstance().get(SrpConfig.class);

    @Override
    public User mapImpl(UserDTO userDTO) {
        User user = new User();
        if (userDTO.getName() != null)
            user.setName(userDTO.getName());
        if (userDTO.getHashedPassword() != null) {
            byte[] salt = new byte[SALT_SIZE];
            user.setSalt(salt);
            byte[] hashedPassword = HashUtil.hexStringToByteArray(userDTO.getHashedPassword());
            byte[] secret = SRPService.secretBuilder(hashedPassword, salt, srpConfig);
            user.setSecret(secret);
        }
        if (userDTO.getEmail() != null)
            user.setEmail(userDTO.getEmail());
        user.setId(UUID.randomUUID());
        return user;
    }
}
