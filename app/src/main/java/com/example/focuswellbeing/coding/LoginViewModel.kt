//import androidx.compose.foundation.layout.Column
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
//
//class LoginViewModel : ViewModel() {
//    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState())
//    val uiState = _uiState.asStateFlow()
//    fun login(userName: String) {
//        //ViewMode scope ties the coroutine to the viewModel lifecycle
//        //
//
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, userName = "asd") }
//        }
//        try {
//            //Api call here
//            //Result
//            _uiState.update { it.copy(isLoading = false, userName = "") }
//        } catch (e: Exception) {
//            _uiState.update { it.copy(isLoading = false) }
//        }
//    }
//
//}
//
//
//data class LoginUiState(
//    val isLoading: Boolean = false,
//    val userName: String = "",
//    val errorMessage: String = ""
//)
//
//@Composable
//fun LoginScreen(viewModel: LoginViewModel) {
//    val state by viewModel.uiState.collectAsStateWithLifecycle()
//
//    Column {
//        Text(text = state.userName)
//
//        if (state.isLoading) {
//            CircularProgressIndicator()
//        }
//
//
//
//        Button(
//            onClick = { viewModel.login("bharath") }
//        ) {
//            Text("Login")
//        }
//    }
//}