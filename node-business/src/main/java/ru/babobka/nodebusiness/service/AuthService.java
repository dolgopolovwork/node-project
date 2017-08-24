package ru.babobka.nodebusiness.service;

import ru.babobka.nodeutils.network.NodeConnection;


/**
 * Created by dolgopolov.a on 29.10.15.
 */
public interface AuthService {

    boolean auth(NodeConnection nodeConnection);

}
