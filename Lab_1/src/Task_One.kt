import kotlin.math.*

// Исходная функция x(t) = |t|
fun xFunc(t: Double): Double = abs(t)

// Полиномы Лежандра
fun P0(t: Double): Double = 1.0
fun P1(t: Double): Double = t
fun P2(t: Double): Double = 0.5 * (3 * t.pow(2) - 1)
fun P3(t: Double): Double = 0.5 * (5 * t.pow(3) - 3 * t)

// Численное интегрирование на отрезке [a, b] методом трапеций
fun integrateTrapezoid(func: (Double) -> Double, a: Double, b: Double, n: Int = 1000): Double {
    val h = (b - a) / n
    var sum = 0.0
    for (i in 0 until n) {
        val t1 = a + i * h
        val t2 = t1 + h
        sum += (func(t1) + func(t2)) * h / 2
    }
    return sum
}

// Коэффициенты Фурье с полиномами Лежандра
fun fourierCoefficient(func: (Double) -> Double, poly: (Double) -> Double, k: Int, interval: Pair<Double, Double>): Double {
    val (a, b) = interval
    val integral = integrateTrapezoid({ t -> func(t) * poly(t) }, a, b)
    return (k + 0.5) * integral
}

fun main() {
    val interval = Pair(-1.0, 1.0)  // Интервал интегрирования

    // Вычисляем коэффициенты Фурье
    val C0 = fourierCoefficient(::xFunc, ::P0, 0, interval)
    val C1 = fourierCoefficient(::xFunc, ::P1, 1, interval)
    val C2 = fourierCoefficient(::xFunc, ::P2, 2, interval)
    val C3 = fourierCoefficient(::xFunc, ::P3, 3, interval)

    // Вывод коэффициентов
    println("C0 = %.5f".format(C0))
    println("C1 = %.32f".format(C1))
    println("C2 = %.5f".format(C2))
    println("C3 = %.31f".format(C3))

    // Аппроксимированные функции (без построения графиков)
    println("p0(t) = %.5f * P0(t)".format(C0))
    println("p1(t) = %.5f * P0(t) + %.32f * P1(t)".format(C0, C1))
    println("p2(t) = %.5f * P0(t) + %.32f * P1(t) + %.5f * P2(t)".format(C0, C1, C2))
    println("p3(t) = %.5f * P0(t) + %.32f * P1(t) + %.5f * P2(t) + %.31f * P3(t)".format(C0, C1, C2, C3))
}
