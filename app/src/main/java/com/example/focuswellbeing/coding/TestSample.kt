import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CounterView2 : ViewModel() {

    private val _count = MutableStateFlow<Int>(0)

    val count = _count.asStateFlow()

    suspend fun increase(v: Int) {
        _count.emit(v)
    }
}
