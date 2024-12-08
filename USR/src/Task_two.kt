import kotlin.math.*

fun main() {
    val A = arrayOf(
        doubleArrayOf(6.25, -1.0, 0.5),
        doubleArrayOf(-1.0, 5.0, 2.12),
        doubleArrayOf(0.5, 2.12, 3.6)
    )
    val ATransposed = Array(A[0].size) { i -> DoubleArray(A.size) { j -> A[j][i] } }
    val B = doubleArrayOf(7.5, -8.68, -0.24)
    val epsilon = 1e-4

    // Проверка определителя
    val detA = A[0][0] * (A[1][1] * A[2][2] - A[1][2] * A[2][1]) -
            A[0][1] * (A[1][0] * A[2][2] - A[1][2] * A[2][0]) +
            A[0][2] * (A[1][0] * A[2][1] - A[1][1] * A[2][0])
    if (detA <= 0) {
        println("Определитель матрицы A отрицательный или равен 0. Решение невозможно.")
        return
    }

    println("Матрица A (detA = $detA != 0):")
    printMatrix(A)
    println("\nВектор B: ${B.joinToString(" ") { "%.2f".format(it) }}")
    println("\nТранспонированная матрица А:")
    printMatrix(ATransposed)

    val AtA = Array(A.size) { DoubleArray(ATransposed[0].size) }

    for (i in A.indices)
        for (j in 0 until ATransposed[0].size)
            for (k in 0 until A[0].size)
                AtA[i][j] += A[i][k] * ATransposed[k][j]

    println("\nМатрица A' * A")
    printMatrix(AtA)

    val largestEigenvalue = powerIteration(AtA)
    println("\nНаибольшее собственное значение: $largestEigenvalue")

    val C = Array(3) { i ->
        DoubleArray(3) { j ->
            if (i == j) 1 - AtA[i][j] / largestEigenvalue
            else -AtA[i][j] / largestEigenvalue
        }
    }

    println("Матрица C: ")
    printMatrix(C)

    val D = DoubleArray(3) { row ->
        ATransposed[row].indices.sumOf { col -> ATransposed[row][col] * B[col] } / largestEigenvalue
    }
    println("\nВектор D: ${D.joinToString(" ") { "%.2f".format(it) }}")

    val alpha = powerIteration(C)
    println("\nКоэффициент сжатия α: $alpha")

    var x0 = doubleArrayOf(0.0, 0.0, 0.0)
    var x1 = DoubleArray(x0.size) { i ->
        C[i].indices.sumOf { C[i][it] * x0[it] } + D[i]
    }
    var delta = alpha / (1 - alpha) * norm2(x0, x1)

    println("delta: %.4f".format(delta))

    val aprioriIterations = ceil(
        ln(epsilon * (1 - alpha) / delta) / ln(alpha)
    ).toInt()
    var realIteration = 1
    println("Априорная оценка количества итераций: $aprioriIterations")

    while(delta > epsilon){
        x1 = DoubleArray(x0.size) { i ->
            C[i].indices.sumOf { j -> C[i][j] * x0[j] } + D[i]
        }
        delta = alpha / (1 - alpha) * norm2(x0, x1)
        x0 = x1
        realIteration++
    }
    println("X в конце ${x0.joinToString(" ")}")
    println("Реальное количество итераций $realIteration")
}

fun printMatrix(A: Array<DoubleArray>) {
    A.forEach { row -> println(row.joinToString(" ") { "%.2f".format(it) }) }
}

fun powerIteration(A: Array<DoubleArray>, epsilon: Double = 1e-4): Double {
    val n = A.size
    var v = DoubleArray(n) { 1.0 }
    var lambdaOld = 0.0

    while (true) {
        val Av = DoubleArray(n) { i -> A[i].indices.sumOf { j -> A[i][j] * v[j] } }
        val norm = sqrt(Av.sumOf { it * it })
        val vNew = Av.map { it / norm }.toDoubleArray()
        val lambdaNew = vNew.indices.sumOf { i -> vNew[i] * Av[i] }
        if (abs(lambdaNew - lambdaOld) < epsilon) return lambdaNew
        v = vNew
        lambdaOld = lambdaNew
    }
}

fun norm2(v1: DoubleArray, v2: DoubleArray): Double {
    return sqrt(v1.indices.sumOf { (v1[it] - v2[it]).pow(2) })
}