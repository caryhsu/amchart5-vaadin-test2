package test;

@FunctionalInterface
public interface ToDoubleFunction<T> {
    double applyAsDouble(T value);
}
