package com.daikyi.rhythmgauge.timing;

public enum NoteType {
    NOTE(1), HOLD_HEAD(2), HOLD_TAIL(3), MINE(4),
    LIFT(5), FAKE(6), ROLL_HEAD(7);

    private int numVal;

    NoteType(int numType) {
        this.numVal = numType;
    }

    public int getNumType() {
        return numVal;
    }
}
