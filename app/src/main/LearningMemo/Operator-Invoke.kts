// クラスを「関数のように呼び出す」ことを可能にする特殊関数！

/*
.Swift でも callAsFunction という命名の関数で以下のようなことができる

class Adder {
    func callAsFunction(_ a: Int, _ b: Int) -> Int {
        return a + b
    }
}

let adder = Adder()
let result1 = adder(1, 1) // 2
let result2 = adder(2, 3) // 5
let result3 = adder(4, 6) // 10
let result4 = adder(7, 5) // 12

 */

/*

kotlin の場合は、operator fun invoke を使用する

class GreetingUseCase {

    operator fun invoke(name: String): String {
        return "Hello, $name!"
    }
}

fun main() {
    val greet = GreetingUseCase()

    // 普通なら greet.invoke("Taro") と書くところを…
    val message = greet("Taro")  // ← これが operator の効果！

    println(message)  // => Hello, Taro!
}
 */