package Fragments

import EmployeeApiService
import Instance.RetrofitInstance
import Models.EmployeeResponse
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nexatech.staffsyncv3.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var apiService: EmployeeApiService
    private lateinit var textViewName: TextView
    private lateinit var textViewPosition: TextView
    private lateinit var textViewEmail: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize API service
        apiService = RetrofitInstance.api

        // Initialize TextViews
        textViewName = view.findViewById(R.id.textView15)
        textViewPosition = view.findViewById(R.id.textView16)
        textViewEmail = view.findViewById(R.id.textView17)

        // Load employee data
        loadEmployeeData()

        return view
    }

    private fun loadEmployeeData() {
        // Get email from shared preferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("employee_email", null)

        if (email != null) {
            // Call the getEmployee method from API service
            apiService.getEmployee(email).enqueue(object : Callback<EmployeeResponse> {
                override fun onResponse(call: Call<EmployeeResponse>, response: Response<EmployeeResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val employee = response.body()!!

                        // Populate TextViews with employee data
                        textViewName.text = "${employee.name} ${employee.surname}"
                        textViewPosition.text = employee.position
                        textViewEmail.text = employee.employee_email
                    } else {
                        Toast.makeText(requireContext(), "Failed to load employee data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Email not found in SharedPreferences", Toast.LENGTH_SHORT).show()
        }
    }
}
