package ru.babobka.nodeweb.mapper;

import ru.babobka.nodebusiness.dto.ConnectedSlave;
import ru.babobka.nodeutils.func.Mapper;
import ru.babobka.nodeweb.dto.ConnectedSlaveDTO;

public class ConnectedSlaveToDTOMapper extends Mapper<ConnectedSlave, ConnectedSlaveDTO> {
    @Override
    protected ConnectedSlaveDTO mapImpl(ConnectedSlave entity) {
        return new ConnectedSlaveDTO(
                entity.getIpAddress().getHostAddress(),
                entity.getId().toString(),
                entity.getUserName());
    }
}
