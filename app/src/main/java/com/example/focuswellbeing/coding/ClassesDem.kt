import java.util.concurrent.atomic.AtomicInteger

//Object is used to create singel tone
object AppTest {
    const val Url = "";
    fun foo() {
        println("hi")
    }
}


/// Campanion
class UserMange {
    companion object {
        val insance = UserMange();
    }
}

// Maek Campanion object thread safe
class CounterDemo {
    companion object {
        private var count = 0
    }

    @Synchronized
    fun inc() {

    }

    @Synchronized
    fun getCount(): Int {
        return count;
    }

}

// Atomic Int
class CounterAtomic {
    companion object {
        private var count = AtomicInteger()
    }

    fun inc(): Int {
        return count.incrementAndGet();
    }
}

fun main() {
    AppTest.Url;
    AppTest.foo()
    val res=CounterAtomic().inc();
    println(res)
}