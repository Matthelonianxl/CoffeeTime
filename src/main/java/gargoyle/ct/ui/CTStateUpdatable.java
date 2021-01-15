package gargoyle.ct.ui;

import gargoyle.ct.task.CTTaskUpdatable;
import gargoyle.ct.task.impl.CTTask;
import org.jetbrains.annotations.NotNull;

public abstract class CTStateUpdatable implements CTTaskUpdatable {
    private State state;

    @Override
    public void doUpdate(@NotNull CTTask task, long currentMillis) {
        if (task.isSleeping(currentMillis)) {
            changeState(state, State.SLEEP);
        }
        if (task.isWarn(currentMillis)) {
            changeState(state, State.WARN);
        }
        if (task.isBlocked(currentMillis)) {
            changeState(state, State.BLOCK);
        }
    }

    private void changeState(State oldState, State newState) {
        if (oldState != newState) {
            state = newState;
            onStateChange(oldState, newState);
        }
    }

    protected abstract void onStateChange(State oldState, State newState);

    protected enum State {
        SLEEP, WARN, BLOCK
    }
}
