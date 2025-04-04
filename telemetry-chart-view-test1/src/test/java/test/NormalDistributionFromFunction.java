package test;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.function.Function;

public class NormalDistributionFromFunction<T> {

    private final Random random = new Random();

    @Getter
    @Setter
    private ItemIterator<T> itemIterator;

    @Getter
    @Setter
    private ToDoubleFunction<T> toDoubleFunction;

    @Getter
    @Setter
    private Function<Double, Double> function = x -> x * x; // 預設函數 y = x^2

    @Getter
    @Setter
    private double variance = 1.0;

    public boolean hasNext() {
        return itemIterator.hasNext();
    }

    @Getter
    private T current;

    @Getter
    private double x;

    public double next() {
        this.current = itemIterator.next();
        this.x = this.toDoubleFunction.applyAsDouble(current);
        double y = function.apply(x);
        double stdDev = Math.sqrt(variance);
        return y + stdDev * random.nextGaussian();
    }
}
