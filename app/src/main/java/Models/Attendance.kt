package Models

data class Attendance(
    val employee_email: String,
    val name: String,
    val surname: String,
    val clocked_in: Boolean
)
