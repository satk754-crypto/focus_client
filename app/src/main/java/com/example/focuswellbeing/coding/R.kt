import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun Counter(){
    var count by remember {
        mutableStateOf(0)
    }
    Column() {
        Text("")
        Button(onClick = {
            count++
        }) {
            Text("Increase")
        }
    }
}