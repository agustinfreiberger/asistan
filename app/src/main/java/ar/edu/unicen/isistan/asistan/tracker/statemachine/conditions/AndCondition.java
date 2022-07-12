package ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;

public class AndCondition extends Condition {

    @NotNull
    private final List<Condition> conditions;

    public AndCondition(Condition... conditions) {
        this.conditions = new ArrayList<>(Arrays.asList(conditions));
    }

    @Override
    public boolean check(@NotNull Event event) {
        for (Condition condition: conditions)
            if (!condition.check(event))
                return false;
        return true;
    }
}
