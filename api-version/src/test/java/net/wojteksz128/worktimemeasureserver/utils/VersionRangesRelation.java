package net.wojteksz128.worktimemeasureserver.utils;

public class VersionRangesRelation {
    private final boolean isSeparate;
    private final boolean isTouching;
    private final boolean isIntersected;
    private final boolean isInside;

    private VersionRangesRelation(boolean isSeparate, boolean isTouching, boolean isIntersected, boolean isInside) {
        this.isSeparate = isSeparate;
        this.isTouching = isTouching;
        this.isIntersected = isIntersected;
        this.isInside = isInside;
    }

    public boolean isSeparate() {
        return isSeparate;
    }

    public boolean isTouching() {
        return isTouching;
    }

    public boolean isIntersected() {
        return isIntersected;
    }

    public boolean isInside() {
        return isInside;
    }

    public static class Builder {

        private boolean isSeparate = false;
        private boolean isTouching = false;
        private boolean isIntersected = false;
        private boolean isInside = false;

        public Builder isSeparate() {
            isSeparate = true;
            return this;
        }

        public Builder isTouching() {
            isTouching = true;
            return this;
        }

        public Builder isIntersected() {
            isIntersected = true;
            return this;
        }

        public Builder isInside() {
            isInside = true;
            return this;
        }

        public VersionRangesRelation build() {
            return new VersionRangesRelation(isSeparate, isTouching, isIntersected, isInside);
        }
    }
}
