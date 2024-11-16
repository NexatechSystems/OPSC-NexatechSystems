package Fragments.SubFragments

import EmployeeApiService
import Instance.RetrofitInstance
import Models.LeaveRequest
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nexatech.staffsyncv3.MainActivity
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentLeaveBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class LeaveFragment : Fragment() {

    private var _binding: FragmentLeaveBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var apiService: EmployeeApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaveBinding.inflate(inflater, container, false)
        apiService = RetrofitInstance.api // Initialize Retrofit API service
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up date pickers for start and end dates
        binding.etFromDate.setOnClickListener {
            showDatePickerDialog { date -> binding.etFromDate.setText(date) }
        }

        binding.etToDate.setOnClickListener {
            showDatePickerDialog { date -> binding.etToDate.setText(date) }
        }

        // Set up close and submit buttons
        binding.btnCloseLeave.setOnClickListener {
            closeFragment()
        }

        binding.imageView4.setOnClickListener {
            submitLeaveRequest()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun closeFragment() {
        bottomNavigationView = (activity as? MainActivity)?.findViewById(R.id.bottomNavigationView) ?: return
        findNavController().navigate(R.id.action_leaveFragment_to_requestFragment)
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Format date to YYYY-MM-DD
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun submitLeaveRequest() {
        // Retrieve email from shared preferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val employeeEmail = sharedPreferences.getString("employee_email", null)

        if (employeeEmail != null) {
            // Collect input values from the UI
            val leaveType = binding.spinnerLeaveType.selectedItem.toString()
            val startDate = binding.etFromDate.text.toString()
            val endDate = binding.etToDate.text.toString()
            val information = binding.etReason.text.toString()

            // Create a leave request object
            val leaveRequest = LeaveRequest(
                employee_email = employeeEmail,
                leave_type = leaveType,
                start_date = startDate,
                end_date = endDate,
                information = information,
                approved = false // Default to not approved
            )

            // Post the leave request using Retrofit
            apiService.postLeave(leaveRequest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Leave request submitted successfully", Toast.LENGTH_LONG).show()
                        closeFragment()
                    } else {
                        Toast.makeText(requireContext(), "Failed to submit leave request", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Employee email not found", Toast.LENGTH_LONG).show()
        }
    }
}
