package Models

data class EmployeeResponse(
    val employee_email: String,
    val password: String,
    val name: String,
    val surname: String,
    val email_personal: String,
    val mobile: String,
    val position: String,
    val access_level: String
)

