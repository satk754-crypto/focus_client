inline fun cal(bloc: () -> Unit) {

    print("Start")
    print("")
    println("End")
    bloc();
}

fun main() {
    val dogBox: Box<Dog> = Box(Dog())
}
open class Animal

class Dog : Animal()

class Cat : Animal()

class Box<T>(var item: T)