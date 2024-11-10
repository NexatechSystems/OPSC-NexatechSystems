package Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nexatech.staffsyncv3.MainActivity
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentClockingBinding
import java.text.SimpleDateFormat
import java.util.*

class ClockingFragment : Fragment() {
    private var _binding: FragmentClockingBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the binding object
        _binding = FragmentClockingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = (activity as? MainActivity)?.findViewById(R.id.bottomNavigationView) ?: return

        // Set the current time, day, and month
        setCurrentDateTime()

        // Toggle clock-in and clock-out background on click
        binding.btnClockIn.setOnClickListener {
            toggleClockInState()
        }

        binding.btnClose.setOnClickListener{
            findNavController().popBackStack()
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    private fun setCurrentDateTime() {
        // Get the current date and time
        val currentTime = Calendar.getInstance().time

        // Format for time (HH:mm)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = timeFormat.format(currentTime)
        binding.lblTime.text = formattedTime

        // Format for day and month (e.g., "Wednesday, 01 November")
        val dayMonthFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        val formattedDayMonth = dayMonthFormat.format(currentTime)
        binding.lblDayMonth.text = formattedDayMonth
    }

    private var isClockedIn = false

    private fun toggleClockInState() {
        if (isClockedIn) {
            // Switch to clock-out state
            binding.btnClockIn.setImageResource(R.drawable.clockin_bg)
            binding.lblClockStatus.text = "Not clocked in"
        } else {
            // Switch to clock-in state
            binding.btnClockIn.setImageResource(R.drawable.clockout_bg)
            binding.lblClockStatus.text = "Clocked in"
        }
        // Toggle the state
        isClockedIn = !isClockedIn
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding when the view is destroyed to avoid memory leaks
    }
}
