import Models.Announcement
import Models.Attendance
import Models.EmployeeResponse
import Models.LeaveRequest
import Models.Message
import Models.Payslip
import Models.Program
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EmployeeApiService {
    @GET("api/employee/{email}")
    fun getEmployee(@Path("email") email: String): Call<EmployeeResponse>

    @GET("api/programs/{email}")
    fun getPrograms(@Path("email") email: String): Call<List<Program>>

    @GET("api/messages/{email}")
    fun getMessages(@Path("email") email: String): Call<List<Message>>

    @GET("api/announcements")
    fun getAnnouncements(): Call<List<Announcement>>

    @GET("api/attendance/{email}")
    fun getAttendance(@Path("email") email: String): Call<List<Attendance>>

    @PUT("api/attendance/{email}")
    fun updateClockedInStatus(@Path("email") email: String, @Body clockedIn: Map<String, Int>): Call<Attendance>

    @GET("api/payslips/{email}")
    fun getPayslips(@Path("email") email: String): Call<List<Payslip>>

    @POST("api/leave/")
    fun postLeave(@Body leaveRequest: LeaveRequest): Call<Void>

    @PUT("api/employee/{email}")
    fun updateEmployeeByEmail(
        @Path("email") email: String,
        @Body updatedFields: Map<String, String>
    ): Call<Void>
}
