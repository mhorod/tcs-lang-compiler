fun Int.f(): Int {
    return this + 1
}

fun main(args: Array<String>) {
    println("Hello World!")
    println("Program arguments: ${args.joinToString()}")

    var x: Int? = null;

    print(x?.f())
}
