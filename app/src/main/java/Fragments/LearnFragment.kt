package Fragments

import Adapters.ProgramAdapter
import Models.Program
import Instance.RetrofitInstance
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexatech.staffsyncv3.databinding.FragmentLearnBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.rcEvents.layoutManager = LinearLayoutManager(requireContext())

        // Retrieve employee email from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val employeeEmail = sharedPreferences.getString("employee_email", null)

        if (employeeEmail != null) {
            fetchPrograms(employeeEmail)
        } else {
            Toast.makeText(requireContext(), "Failed to retrieve employee email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPrograms(employeeEmail: String) {
        // Show the ProgressBar and hide the RecyclerView initially
        binding.progressBar.visibility = View.VISIBLE
        binding.rcEvents.visibility = View.GONE

        RetrofitInstance.api.getPrograms(employeeEmail).enqueue(object : Callback<List<Program>> {
            override fun onResponse(call: Call<List<Program>>, response: Response<List<Program>>) {
                // Hide the ProgressBar after getting the response
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val programs = response.body()!!
                    binding.rcEvents.visibility = View.VISIBLE
                    binding.rcEvents.adapter = ProgramAdapter(programs)
                } else {
                    Toast.makeText(requireContext(), "No programs found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Program>>, t: Throwable) {
                // Hide the ProgressBar and show an error message
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
