package com.m2.tur.factory;

import com.m2.tur.model.dto.response.StateResponse;
import com.m2.tur.model.entity.State;

import java.time.LocalDate;

public class StateFactory {
    public static State createEntity() {
        State state = new State();
        state.setId(1L);
        state.setName("Alagoas");
        state.setAbbreviation("AL");
        state.setCreatedAt(LocalDate.now());
        return state;
    }

    public static StateResponse createResponse() {
        return new StateResponse(
                1L,
                "Alagoas",
                "AL"
        );
    }
}
