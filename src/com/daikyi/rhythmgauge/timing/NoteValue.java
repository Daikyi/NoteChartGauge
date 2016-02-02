package com.daikyi.rhythmgauge.timing;

public enum NoteValue {
    _4TH(4), _8TH(8), _12TH(12), _16TH(16), _24TH(24),
    _32ND(32), _48TH(48), _64TH(64), _96TH(96), _192ND(192);

    private int numVal;

    NoteValue(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
