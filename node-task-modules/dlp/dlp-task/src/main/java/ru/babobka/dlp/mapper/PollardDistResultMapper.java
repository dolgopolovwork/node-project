package ru.babobka.dlp.mapper;

import ru.babobka.dlp.model.dist.PollardDistResult;
import ru.babobka.dlp.model.regular.PollardEntity;
import ru.babobka.dlp.task.Params;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeutils.func.ReverseMapper;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 09.07.2018.
 */
public class PollardDistResultMapper extends ReverseMapper<PollardDistResult, Data> {
    @Override
    protected Data mapImpl(PollardDistResult pollardDistResult) {
        Data data = new Data();
        data.put(Params.COLLISIONS.getValue(), getProbableCollisions(pollardDistResult));
        data.put(Params.GEN_EXP_LIST.getValue(), getGenExpList(pollardDistResult));
        data.put(Params.VAL_EXP_LIST.getValue(), getValExpList(pollardDistResult));
        return data;
    }

    @Override
    protected PollardDistResult reverseMapImpl(Data data) {
        BigInteger exp = data.get(Params.EXP.getValue());
        if (exp != null) {
            return PollardDistResult.result(exp);
        }
        BigInteger mod = data.get(Params.MOD.getValue());
        List<Number> probableCollisions = data.get(Params.COLLISIONS.getValue());
        List<Number> genExpList = data.get(Params.GEN_EXP_LIST.getValue());
        List<Number> valExpList = data.get(Params.VAL_EXP_LIST.getValue());
        Map<Fp, PollardEntity> collisions = new HashMap<>();
        Fp y = new Fp(data.get(Params.Y.getValue()), mod);
        Fp x = new Fp(data.get(Params.X.getValue()), mod);
        for (int i = 0; i < probableCollisions.size(); i++) {
            Fp collision = new Fp((BigInteger) probableCollisions.get(i), mod);
            Fp valExp = new Fp((BigInteger) valExpList.get(i), mod.subtract(BigInteger.ONE));
            Fp xExp = new Fp((BigInteger) genExpList.get(i), mod.subtract(BigInteger.ONE));
            PollardEntity pollardEntity = new PollardEntity(collision, y, x, valExp, xExp);
            collisions.put(collision, pollardEntity);
        }
        return PollardDistResult.collisions(collisions);
    }

    private List<Number> getProbableCollisions(PollardDistResult pollardDistResult) {
        List<Number> probableCollisions = new ArrayList<>(pollardDistResult.getCollisions().size());
        for (Map.Entry<Fp, PollardEntity> pollardEntry : pollardDistResult.getCollisions().entrySet()) {
            probableCollisions.add(pollardEntry.getKey().getNumber());
        }
        return probableCollisions;
    }

    private List<Number> getGenExpList(PollardDistResult pollardDistResult) {
        List<Number> genExpList = new ArrayList<>(pollardDistResult.getCollisions().size());
        for (Map.Entry<Fp, PollardEntity> pollardEntry : pollardDistResult.getCollisions().entrySet()) {
            genExpList.add(pollardEntry.getValue().getGenExp().getNumber());
        }
        return genExpList;
    }

    private List<Number> getValExpList(PollardDistResult pollardDistResult) {
        List<Number> valExpList = new ArrayList<>(pollardDistResult.getCollisions().size());
        for (Map.Entry<Fp, PollardEntity> pollardEntry : pollardDistResult.getCollisions().entrySet()) {
            valExpList.add(pollardEntry.getValue().getValExp().getNumber());
        }
        return valExpList;
    }

}
