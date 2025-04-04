package test;

import lombok.AllArgsConstructor;

public interface ItemIterator<T> {
    T next();
    default boolean hasNext() {
        return true;
    }

    @AllArgsConstructor
    public static class DoubleValueIterator implements ItemIterator<Double> {
        private double value;
        private double step;
        private double limit;

        @Override
        public Double next() {
            value += step;
            return value;
        }

        @Override
        public boolean hasNext() {
            return value + step <= limit;
        }

    }

}
