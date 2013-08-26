package com.example.brickrack2;

public class MaskConf {
    private int leftOffset;
    private int topOffset;

    public MaskConf(int leftOffset, int topOffset) {
        super();
        this.leftOffset = leftOffset;
        this.topOffset = topOffset;
    }

    public int getLeftOffset() {
        return leftOffset;
    }

    public int getTopOffset() {
        return topOffset;
    }

}
