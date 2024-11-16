package Models

data class LeaveRequest(
    val employee_email: String,
    val leave_type: String,
    val start_date: String,
    val end_date: String,
    val information: String,
    val approved: Boolean
)
