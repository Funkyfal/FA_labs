import kotlin.math.*

// Функция x(t) = |t|
fun x_t(t: Double): Double {
    return abs(t)
}

// Интервал интегрирования
val interval = -1.0 to 1.0

// Задаем точность
const val EPSILON = 1e-4
const val MAX_N = 1000

// Вычисление интеграла с помощью численного метода Симпсона
fun integrateSimpson(f: (Double) -> Double, a: Double, b: Double, steps: Int = 1000): Double {
    val h = (b - a) / steps
    var sum = 0.0
    for (i in 0..steps) {
        val x = a + i * h
        sum += when (i) {
            0, steps -> f(x)
            else -> if (i % 2 == 0) 2 * f(x) else 4 * f(x)
        }
    }
    return (h / 3) * sum
}

// Коэффициент a0
fun computeA0(): Double {
    return integrateSimpson({ t -> x_t(t) }, interval.first, interval.second)
}

// Коэффициенты ak и bk
fun computeAk(k: Int): Double {
    return integrateSimpson({ t -> x_t(t) * cos(k * PI * t) }, interval.first, interval.second)
}

fun computeBk(k: Int): Double {
    return integrateSimpson({ t -> x_t(t) * sin(k * PI * t) }, interval.first, interval.second)
}

// Аппроксимирующий многочлен p_n(t)
fun p_n(t: Double, n: Int, a0: Double, ak: List<Double>, bk: List<Double>): Double {
    var result = a0 / 2
    for (k in 1..n) {
        result += ak[k - 1] * cos(k * PI * t) + bk[k - 1] * sin(k * PI * t)
    }
    return result
}

// Вычисление нормы разности между x(t) и p_n(t)
fun normDifference(n: Int, a0: Double, ak: List<Double>, bk: List<Double>): Double {
    val integrand: (Double) -> Double = { t -> (x_t(t) - p_n(t, n, a0, ak, bk)).pow(2) }
    return sqrt(integrateSimpson(integrand, interval.first, interval.second))
}

fun main() {
    val akValues = mutableListOf<Double>()
    val bkValues = mutableListOf<Double>()

    val a0 = computeA0()
    var n = 1

    // Итеративно вычисляем коэффициенты
    while (true) {
        akValues.add(computeAk(n))
        bkValues.add(computeBk(n))

        if (normDifference(n, a0, akValues, bkValues) < EPSILON || n >= MAX_N) {
            break
        }
        n++
    }

    if (n >= MAX_N) {
        println("Достигнуто максимальное значение n ($MAX_N). Приближение может быть недостаточно точным")
    } else {
        println("Минимальное n, при котором погрешность меньше $EPSILON: $n")
    }

    // Печать коэффициентов
    println("a0 = %.5f".format(a0))
    for (k in 1..n) {
        println("a$k = %.33f, b$k = %.33f".format(akValues[k - 1], bkValues[k - 1]))
    }
}
