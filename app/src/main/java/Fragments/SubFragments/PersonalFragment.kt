package Fragments.SubFragments

import EmployeeApiService
import Instance.RetrofitInstance
import Models.EmployeeResponse
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nexatech.staffsyncv3.MainActivity
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentPersonalBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var apiService: EmployeeApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        apiService = RetrofitInstance.api
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve user email from shared preferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val employeeEmail = sharedPreferences.getString("employee_email", null)

        // Populate fields with GET request
        if (employeeEmail != null) {
            fetchEmployeeDetails(employeeEmail)
        }

        binding.btnClosePersonal.setOnClickListener {
            closeFragment()
        }

        // Show save button when text changes in email or phone fields
        binding.txtChangeEmail.addTextChangedListener {
            binding.btnSave.visibility = View.VISIBLE
        }

        binding.txtChangePhone.addTextChangedListener {
            binding.btnSave.visibility = View.VISIBLE
        }

        // Save updated data on button click
        binding.btnSave.setOnClickListener {
            updateEmployeeDetails(employeeEmail)
        }
    }

    private fun fetchEmployeeDetails(email: String) {
        apiService.getEmployee(email).enqueue(object : Callback<EmployeeResponse> {
            override fun onResponse(call: Call<EmployeeResponse>, response: Response<EmployeeResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val employee = response.body()!!
                    binding.txtChangeEmail.setText(employee.email_personal)
                    binding.txtChangePhone.setText(employee.mobile)
                    binding.txtPosition.setText(employee.position)
                    binding.txtPosition.isEnabled = false // Make position uneditable
                } else {
                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateEmployeeDetails(email: String?) {
        if (email != null) {
            val updatedEmail = binding.txtChangeEmail.text.toString()
            val updatedPhone = binding.txtChangePhone.text.toString()

            val updatedFields = mapOf(
                "email_personal" to updatedEmail,
                "mobile" to updatedPhone
            )

            apiService.updateEmployeeByEmail(email, updatedFields).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Details updated successfully", Toast.LENGTH_SHORT).show()
                        binding.btnSave.visibility = View.INVISIBLE // Hide save button after saving
                    } else {
                        Toast.makeText(requireContext(), "Failed to update details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Email not found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun closeFragment() {
        bottomNavigationView = (activity as? MainActivity)?.findViewById(R.id.bottomNavigationView) ?: return
        findNavController().navigate(R.id.action_personalFragment_to_requestFragment)
        bottomNavigationView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
