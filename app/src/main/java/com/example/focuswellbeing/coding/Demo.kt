import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

fun numbers(): Flow<Int> = flow {
    emit(1)
    emit(2)
    emit(3)
}

fun main() = runBlocking {
    numbers().collect { value ->
        println(value)
    }
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