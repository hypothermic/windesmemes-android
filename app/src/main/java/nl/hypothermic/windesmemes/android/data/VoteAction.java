package nl.hypothermic.windesmemes.android.data;

import nl.hypothermic.windesmemes.model.Vote;

public class VoteAction {

    private final Vote vote;

    private final boolean triggeredByUser;
    private final Priority priority;

    private final long timestamp = System.currentTimeMillis();

    public VoteAction(Vote vote, boolean triggedByUser) {
        this.vote = vote;
        this.triggeredByUser = triggedByUser;
        this.priority = Priority.USER;
    }

    public VoteAction(Vote vote, boolean triggedByUser, Priority priority) {
        this.vote = vote;
        this.triggeredByUser = triggedByUser;
        this.priority = priority;
    }

    public Vote getVote() {
        return vote;
    }

    public boolean isTriggeredByUser() {
        return triggeredByUser;
    }

    public static enum Priority {

        LOWEST(-1),
        CACHE(0),
        LIVE(1),
        USER(2),

        ;

        private final int index;

        private Priority(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public boolean isHigherThan(Priority priority) {
            return this.getIndex() >= priority.getIndex();
        }
    }

    public Priority getPriority() {
        return priority;
    }
}
