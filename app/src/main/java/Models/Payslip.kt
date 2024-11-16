
package Models

data class Payslip(
    val payslip_id: Int,
    val month: String,
    val salary: Double,
    val bonus: Double,
    val total: Double,
    val employee_id: String
)
