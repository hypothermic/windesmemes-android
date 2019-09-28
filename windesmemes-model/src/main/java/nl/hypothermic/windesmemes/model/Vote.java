package nl.hypothermic.windesmemes.model;

public enum Vote {

    DOWNVOTE(-1),
    NEUTRAL ( 0),
    UPVOTE  (+1),

    ;

    public static Vote fromIndex(int index) {
        for (Vote vote : values()) {
            if (vote.weight == index) {
                return vote;
            }
        }
        throw new IllegalArgumentException("Vote weight not recognized");
    }

    private final int weight;

    private Vote(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
