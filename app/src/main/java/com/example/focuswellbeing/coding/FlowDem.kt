import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginViewModel : ViewModel() {
    private val _login = MutableStateFlow("i")

    val log: StateFlow<String> = _login.asStateFlow()

    fun login() {
        viewModelScope.launch {
            _login.value = "I"
            delay(200)
            _login.value = "S"
        }
    }
}

suspend fun getName(): String {
    return "Das"
}

class UserViewMode(private val Name: String) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    private val user = _user.asStateFlow()

    fun loadUser() {
        viewModelScope.launch {
            try {

                // call reposit here and get api response
            } catch (e: Exception) {

            }
        }
    }


}

fun main() {
    runBlocking {
        getNumbers().collect { it ->
            println("asdf")
        }
    }
}

data class User(var t: Int);


fun getNumbers(): Flow<Int> = flow {
    emit(1)
    delay(100)
}