package ru.babobka.vsjws.webcontroller;


public interface WebController<I, O> {

    O onHead(I request);

    O onGet(I request);

    O onPost(I request);

    O onPut(I request);

    O onPatch(I request);

    O onDelete(I request);
}
