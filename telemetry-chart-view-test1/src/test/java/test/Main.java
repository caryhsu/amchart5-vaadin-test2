package test;

public class Main {
    public static void main(String[] args) {
        NormalDistributionFromFunction<Double> generator = new NormalDistributionFromFunction<>();

        ItemIterator<Double> iterator = new ItemIterator.DoubleValueIterator(1.0, 0.1, 10.0);
        generator.setItemIterator(iterator);

        generator.setToDoubleFunction(x -> x);

        // 設定主函數 y = x * x
        generator.setFunction(x -> x * x);

        // 設定變異數
        generator.setVariance(2.0);

        while(generator.hasNext()) {
            double sampled = generator.next();
            double x = generator.getX();
            double y = generator.getFunction().apply(x);
            System.out.printf("x = %.2f, y = %.4f, sampled = %.4f%n", x, y, sampled);
        }
    }
}
