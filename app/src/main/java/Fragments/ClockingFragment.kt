package Fragments

import Models.Attendance
import Instance.RetrofitInstance
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nexatech.staffsyncv3.MainActivity
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentClockingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ClockingFragment : Fragment() {
    private var _binding: FragmentClockingBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigationView: BottomNavigationView
    private var isClockedIn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClockingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = (activity as? MainActivity)?.findViewById(R.id.bottomNavigationView) ?: return
        setCurrentDateTime()

        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val employeeEmail = sharedPreferences.getString("employee_email", null)

        if (employeeEmail != null) {
            fetchAttendanceStatus(employeeEmail)
        } else {
            Toast.makeText(requireContext(), "Failed to retrieve employee email", Toast.LENGTH_SHORT).show()
        }

        binding.btnClockIn.setOnClickListener {
            if (employeeEmail != null) {
                toggleClockInState(employeeEmail)
            }
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    private fun setCurrentDateTime() {
        val currentTime = Calendar.getInstance().time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.lblTime.text = timeFormat.format(currentTime)

        val dayMonthFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        binding.lblDayMonth.text = dayMonthFormat.format(currentTime)
    }

    private fun fetchAttendanceStatus(email: String) {
        RetrofitInstance.api.getAttendance(email).enqueue(object : Callback<List<Attendance>> {
            override fun onResponse(call: Call<List<Attendance>>, response: Response<List<Attendance>>) {
                if (response.isSuccessful && response.body() != null) {
                    val attendanceList = response.body()!!

                    if (attendanceList.isNotEmpty()) {
                        val attendance = attendanceList[0] // Access the first item in the list
                        isClockedIn = attendance.clocked_in
                        Log.d("ClockingFragment", "Initial clocked_in status: $isClockedIn")
                        updateClockInButtonUI()
                    } else {
                        Toast.makeText(requireContext(), "No attendance data found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch clock-in status", Toast.LENGTH_SHORT).show()
                    Log.d("ClockingFragment", "Failed to fetch data from API")
                }
            }

            override fun onFailure(call: Call<List<Attendance>>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show()
                Log.e("ClockingFragment", "Error fetching attendance status", t)
            }
        })
    }


    private fun toggleClockInState(email: String) {
        val newClockedInStatus = if (isClockedIn) 0 else 1
        val requestBody = mapOf("clocked_in" to newClockedInStatus)

        RetrofitInstance.api.updateClockedInStatus(email, requestBody).enqueue(object : Callback<Attendance> {
            override fun onResponse(call: Call<Attendance>, response: Response<Attendance>) {
                if (response.isSuccessful) {
                    isClockedIn = !isClockedIn
                    Log.d("ClockingFragment", "Clock-in status updated to: $isClockedIn")
                    updateClockInButtonUI()
                } else {
                    Toast.makeText(requireContext(), "Failed to update clock-in status", Toast.LENGTH_SHORT).show()
                    Log.d("ClockingFragment", "Failed to update data in API")
                }
            }

            override fun onFailure(call: Call<Attendance>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show()
                Log.e("ClockingFragment", "Error updating attendance status", t)
            }
        })
    }

    private fun updateClockInButtonUI() {
        if (isClockedIn) {
            binding.btnClockIn.setImageResource(R.drawable.clockout_bg)
            binding.lblClockStatus.text = "Clocked in"
            Log.d("ClockingFragment", "Button updated to clock-out state")
        } else {
            binding.btnClockIn.setImageResource(R.drawable.clockin_bg)
            binding.lblClockStatus.text = "Not clocked in"
            Log.d("ClockingFragment", "Button updated to clock-in state")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
