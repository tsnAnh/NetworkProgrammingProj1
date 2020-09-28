import java.util.*

data class Payload(
    val sender: String,
    val message: String? = null,
    val image: String? = null,
    val time: Date = Date(Calendar.getInstance().timeInMillis)
)