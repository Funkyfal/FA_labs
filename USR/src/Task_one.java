public class Task_one {

    // Исходная функция g(x) = x + sin(x/2) + x/(1 + x^2) - 6
    private static double g(double x) {
        return x + Math.sin(x / 2) + x / (1 + Math.pow(x, 2)) - 6;
    }

    // Производная g'(x) = 1 + cos(x/2)/2 + (1 - x^2)/((1 + x^2)^2)
    private static double gDerivative(double x) {
        return 1 + 0.5 * Math.cos(x / 2) + (1 - Math.pow(x, 2)) / Math.pow(1 + Math.pow(x, 2), 2);
    }

    // Приводим уравнение к x = f(x) с использованием λ
    private static double f(double x, double lambda) {
        return x - lambda * g(x);
    }

    public static void main(String[] args) {
        // Заданный отрезок [a, b]
        double a = 5;
        double b = 6;

        double k1 = gDerivative(a);
        double k2 = gDerivative(b);

        // Проверка производной на промежутке
        for (double x = a; x <= b; x += 0.01) {
            double derivative = gDerivative(x);
            if (derivative < k1) k1 = derivative;
            if (derivative > k2) k2 = derivative;
        }

        double lambda = 2 / (k1 + k2);
        double alpha = (k2 - k1) / (k2 + k1);

        double epsilon = 1e-4;
        double x0 = 5.3; // Начальное приближение

        // Итерационный процесс
        double xPrev = x0;
        double xNext = f(xPrev, lambda);
        int iterations = 1;

        // Априорная оценка числа итераций
        int aprioriIterations = (int) Math.floor(
                Math.log(epsilon * (1 - alpha) / Math.abs(xNext - xPrev)) / Math.log(alpha)
        ) + 1;

        // Выполняем итерации до выполнения условия
        while (alpha / (1 - alpha) * Math.abs(xNext - xPrev) > epsilon) {
            xPrev = xNext;
            xNext = f(xPrev, lambda);
            iterations++;
        }

        System.out.println("Априорная оценка числа итераций: " + aprioriIterations);
        System.out.println("x на последней итерации: " + xNext);
        System.out.println("Реальное число итераций: " + iterations);
        System.out.println("λ (вычисленное): " + lambda);
        System.out.println("α (коэффициент сжатия): " + alpha);
    }
}
