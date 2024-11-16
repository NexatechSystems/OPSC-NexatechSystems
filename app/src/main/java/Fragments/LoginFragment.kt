package Fragments

import Instance.RetrofitInstance
import Models.EmployeeResponse
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val PREFS_NAME = "UserPrefs"
private const val KEY_EMPLOYEE_EMAIL = "employee_email"

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the BiometricPrompt
        setupBiometricPrompt()

        // Check if fingerprint login is enabled in SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val fingerprintEnabled = sharedPreferences.getBoolean("fingerprint_enabled", false)
        val savedEmail = sharedPreferences.getString(KEY_EMPLOYEE_EMAIL, null)

        if (fingerprintEnabled && savedEmail != null) {
            // Show fingerprint prompt automatically if enabled
            showBiometricPrompt()
        }

        // Regular login button click
        binding.btnLogin.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Fingerprint button click
        binding.btnFingerprint.setOnClickListener {
            showBiometricPrompt()
        }
    }

    private fun setupBiometricPrompt() {
        // Create the BiometricPrompt instance
        biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Automatically log the user in when fingerprint is authenticated
                    val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val savedEmail = sharedPreferences.getString(KEY_EMPLOYEE_EMAIL, null)
                    if (savedEmail != null) {
                        loginUser(savedEmail, "") // Call loginUser without password
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        // Configure the biometric prompt
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Login")
            .setSubtitle("Log in using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun showBiometricPrompt() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun loginUser(email: String, password: String) {
        // Replace your login logic here, with or without password validation
        RetrofitInstance.api.getEmployee(email).enqueue(object : Callback<EmployeeResponse> {
            override fun onResponse(call: Call<EmployeeResponse>, response: Response<EmployeeResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val employee = response.body()!!
                    if (password.isEmpty() || employee.password == password) {
                        // Save employee email and enable fingerprint login
                        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString(KEY_EMPLOYEE_EMAIL, email)
                            putBoolean("fingerprint_enabled", true)
                            apply()
                        }
                        // Navigate to home if login is successful
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        Toast.makeText(requireContext(), "Invalid password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Employee not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
