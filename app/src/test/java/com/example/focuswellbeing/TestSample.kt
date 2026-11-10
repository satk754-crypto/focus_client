import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class CounterViewTest {
    @Test
    fun first(){
        runTest {
            val model= CounterView2()
            assertEquals(0, model.count.value)
        }
    }
    @Test
    fun test2() {
        runTest {
            val model = CounterView2()
            model.increase(1)
            assertEquals(1, model.count.value)
        }
    }
}