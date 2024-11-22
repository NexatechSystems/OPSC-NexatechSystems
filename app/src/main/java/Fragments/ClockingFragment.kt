package Fragments

import Models.Attendance
import Instance.RetrofitInstance
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomNavigationView: BottomNavigationView
    private val workLatitude = -26.1001167 // Hardcoded workplace latitude
    private val workLongitude = 28.1259247 // Hardcoded workplace longitude
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

        // Initialize BottomNavigationView
        bottomNavigationView = (activity as? MainActivity)?.findViewById(R.id.bottomNavigationView) ?: return

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Set current time and date
        setCurrentDateTime()

        // Get employee email from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val employeeEmail = sharedPreferences.getString("employee_email", null)

        // Fetch attendance status
        if (employeeEmail != null) {
            fetchAttendanceStatus(employeeEmail)
        } else {
            Toast.makeText(requireContext(), "Failed to retrieve employee email", Toast.LENGTH_SHORT).show()
        }

        // Set up clock-in button click listener
        binding.btnClockIn.setOnClickListener {
            if (employeeEmail != null) {
                fetchUserLocationAndVerify(employeeEmail)
            }
        }

        // Set up close button click listener
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    private fun fetchUserLocationAndVerify(email: String) {
        showLoading(true)

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            showLoading(false)
            return
        }

        // Get user's current location
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            showLoading(false)
            if (location != null) {
                val distance = calculateDistance(
                    location.latitude, location.longitude,
                    workLatitude, workLongitude
                )
                if (distance <= 300) { // Check if within 50 meters
                    toggleClockInState(email)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You are not at the workplace location!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Failed to fetch location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            showLoading(false)
            Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateDistance(
        userLat: Double, userLng: Double,
        workLat: Double, workLng: Double
    ): Float {
        val userLocation = Location("").apply {
            latitude = userLat
            longitude = userLng
        }
        val workLocation = Location("").apply {
            latitude = workLat
            longitude = workLng
        }
        val distance = userLocation.distanceTo(workLocation) // Distance in meters
        Log.d("ClockingFragment", "User Location: ($userLat, $userLng)")
        Log.d("ClockingFragment", "Work Location: ($workLat, $workLng)")
        Log.d("ClockingFragment", "Distance: $distance meters")
        return distance
    }


    private fun toggleClockInState(email: String) {
        val newClockedInStatus = if (isClockedIn) 0 else 1
        val requestBody = mapOf("clocked_in" to newClockedInStatus)

        showLoading(true)

        RetrofitInstance.api.updateClockedInStatus(email, requestBody).enqueue(object : Callback<Attendance> {
            override fun onResponse(call: Call<Attendance>, response: Response<Attendance>) {
                showLoading(false)
                if (response.isSuccessful) {
                    isClockedIn = !isClockedIn
                    updateClockInButtonUI()
                } else {
                    Toast.makeText(requireContext(), "Failed to update clock-in status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Attendance>, t: Throwable) {
                showLoading(false)
                Toast.makeText(requireContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAttendanceStatus(email: String) {
        RetrofitInstance.api.getAttendance(email).enqueue(object : Callback<List<Attendance>> {
            override fun onResponse(call: Call<List<Attendance>>, response: Response<List<Attendance>>) {
                if (response.isSuccessful && response.body() != null) {
                    val attendanceList = response.body()!!
                    if (attendanceList.isNotEmpty()) {
                        val attendance = attendanceList[0]
                        isClockedIn = attendance.clocked_in
                        updateClockInButtonUI()
                    } else {
                        Toast.makeText(requireContext(), "No attendance data found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch clock-in status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Attendance>>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateClockInButtonUI() {
        if (isClockedIn) {
            binding.btnClockIn.setImageResource(R.drawable.clockout_bg)
            binding.lblClockStatus.text = "Clocked in"
        } else {
            binding.btnClockIn.setImageResource(R.drawable.clockin_bg)
            binding.lblClockStatus.text = "Not clocked in"
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnClockIn.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setCurrentDateTime() {
        val currentTime = Calendar.getInstance().time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.lblTime.text = timeFormat.format(currentTime)

        val dayMonthFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        binding.lblDayMonth.text = dayMonthFormat.format(currentTime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
