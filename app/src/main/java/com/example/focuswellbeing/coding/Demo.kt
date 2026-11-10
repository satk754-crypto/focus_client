import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun numbers(): Flow<Int> = flow {
    emit(1)
    emit(2)
    emit(3)
}

fun main(): kotlin.Unit = runBlocking {
    coldFlow();
//    numbers().collect { value ->
//        println(value)
//    }
//    val a: CounterView = CounterView()
//    a.incr();
//    a.count.collect { it ->
//        println(it)
//    }
}

class CounterView : ViewModel() {
    private val _count = MutableStateFlow(0)

    val count: StateFlow<Int> = _count.asStateFlow()

    fun incr() {
        _count.update { it ->
            it + 1
        }
    }
}

suspend fun coldFlow() {
    withContext(Dispatchers.Default) {
        val pageFlow = flow {
            for (page in 1..3) {
                println("Loading page")
                emit("page $page")
            }
        }
        pageFlow.collect { it ->
            println(it)
            delay(100)
            println("Done process $it")

        }
    }

}