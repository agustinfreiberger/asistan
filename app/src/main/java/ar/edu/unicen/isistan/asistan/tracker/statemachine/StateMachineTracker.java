package ar.edu.unicen.isistan.asistan.tracker.statemachine;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.synchronizer.data.works.MobilitySyncWork;
import ar.edu.unicen.isistan.asistan.synchronizer.reports.userstate.UserStateReporter;
import ar.edu.unicen.isistan.asistan.tracker.inquirer.Inquirer;

public class StateMachineTracker {

    private static final String PREFERENCES = "ar.edu.unicen.isistan.asistan.tracker.statemachine";

    // TrackerState instance is keept during each app instance
    @Nullable
    private static StateMachine stateMachineInstance;

    @NotNull
    private static StateMachine load(@NotNull Context context) {
        if (StateMachineTracker.stateMachineInstance == null) {
            StateMachineTracker.stateMachineInstance = new StateMachine();
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            StateMachineTracker.stateMachineInstance.load(preferences);
        }
        return StateMachineTracker.stateMachineInstance;
    }

    @SuppressWarnings("deprecation")
    private static void save(@NotNull Context context, @NotNull StateMachine stateMachine, int retries) {
        Database database = Database.getInstance();
        database.beginTransaction();
        boolean result;
        try {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            result = stateMachine.save(preferences);
            if (result)
                database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        if (!result)
            rollBack(context, retries);
    }

    private static void rollBack(@NotNull Context context, int retries) {
        if (retries > 0) {
            retries--;
            Event event = Database.getInstance().event().last();
            if (event != null) {
                clean();
                load(context);
                process(context, event, retries);
            }
        } else {
            clean();
            load(context);
        }
    }

    public synchronized static void clean() {
        StateMachineTracker.stateMachineInstance = null;
        Inquirer.clean();
    }

    public synchronized static void startTravelling(@NotNull Context context) {
        context = context.getApplicationContext();

        StateMachine stateMachine = load(context);
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        stateMachine.setTravelling(preferences,true);
    }

    public synchronized static void process(@NotNull Context context, @NotNull Event event) {
        process(context, event,1);

    }

    private static void process(@NotNull Context context, @NotNull Event event, int retries) {
        context = context.getApplicationContext();

        StateMachine stateMachine = load(context);
        Database.getInstance().event().insert(event);
        boolean change = stateMachine.executeStates(event);

        save(context, stateMachine, retries);

        notifyListeners(context,change,event);
    }

    public synchronized static void setCommute(@NotNull Context context, @NotNull Visit deleted, @NotNull Commute commute) {
        context = context.getApplicationContext();
        StateMachine stateMachine = load(context);
        if (stateMachine.current(deleted))
            stateMachine.setCommute(commute);
        save(context,stateMachine,0);
    }

    public synchronized static void setCommute(@NotNull Context context, @NotNull Commute deleted, @NotNull Commute commute) {
        context = context.getApplicationContext();
        StateMachine stateMachine = load(context);
        if (stateMachine.current(deleted))
            stateMachine.setCommute(commute);
        save(context,stateMachine,0);
    }

    public synchronized static void setVisit(@NotNull Context context, @NotNull Visit deleted, Visit visit, boolean fixedVisit) {
        context = context.getApplicationContext();

        StateMachine stateMachine = load(context);
        if (stateMachine.current(deleted))
            stateMachine.setVisit(visit);

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        stateMachine.setFixedVisit(preferences, fixedVisit);

        save(context,stateMachine,0);
    }

    public synchronized static void setVisit(@NotNull Context context, @NotNull Commute deleted, @NotNull Visit visit, boolean fixedVisit) {
        context = context.getApplicationContext();
        StateMachine stateMachine = load(context);
        if (stateMachine.current(deleted))
            stateMachine.setVisit(visit);
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        stateMachine.setFixedVisit(preferences, fixedVisit);
        save(context,stateMachine,0);
    }

    public synchronized static void remove(@NotNull Context context, @NotNull Commute commute) {
        context = context.getApplicationContext();
        StateMachine stateMachine = load(context);
        if (stateMachine.current(commute))
            stateMachine.clear();
        save(context,stateMachine,0);
    }

    public synchronized static void remove(@NotNull Context context, @NotNull Visit visit) {
        context = context.getApplicationContext();
        StateMachine stateMachine = load(context);
        if (stateMachine.current(visit))
            stateMachine.clear();
        save(context,stateMachine,0);
    }

    public synchronized static void userSynchronized(@NotNull Context context) {
        context = context.getApplicationContext();

        Visit visit = Database.getInstance().mobility().currentVisit();
        if (visit != null) {
            StateMachine stateMachine = load(context);
            stateMachine.setVisit(visit);
            save(context,stateMachine,0);
        }

    }


    private static void notifyListeners(@NotNull Context context, boolean changeState, @NotNull Event event) {
        StateMachine stateMachine = load(context);

        if (changeState)
            MobilitySyncWork.Builder.createWork(context);

        Visit visit = stateMachine.getCurrentVisit();
        Commute commute = stateMachine.getCurrentCommute();
        if (visit != null) {
            Inquirer.visitUpdated(context, visit);
            UserStateReporter.update(context,event,visit);
        } else if (commute != null) {
            UserStateReporter.update(context,event,commute);
        } else {
            UserStateReporter.update(context,event);
        }

    }

}
