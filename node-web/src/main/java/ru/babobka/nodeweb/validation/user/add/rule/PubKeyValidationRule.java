package ru.babobka.nodeweb.validation.user.add.rule;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.security.spec.InvalidKeySpecException;

public class PubKeyValidationRule implements ValidationRule<UserDTO> {
    @Override
    public void validate(UserDTO userDTO) {
        if (userDTO.getBase64PubKey() == null) {
            throw new IllegalArgumentException("pubKey is null");
        }
        try {
            KeyDecoder.decodePublicKey(userDTO.getBase64PubKey());
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("cannot decode public key");
        }
    }
}
