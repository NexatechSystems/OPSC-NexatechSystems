package Models

data class Message(
    val message_id: Int,
    val employee_email: String,
    val subject: String,
    val message: String
)
