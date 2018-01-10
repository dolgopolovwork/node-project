package ru.babobka.dlp;

import java.math.BigInteger;

/**
 * Created by 123 on 06.01.2018.
 */
public interface DlpService {

    //gen^x=y
    //returns x
    BigInteger dlp(DlpTask dlpTask);

}
