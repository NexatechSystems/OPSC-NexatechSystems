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

        // Ensure ProgressBar is hidden and buttons are enabled by default
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
        binding.btnFingerprint.isEnabled = true

        // Set up the BiometricPrompt
        setupBiometricPrompt()

        // Check SharedPreferences for fingerprint settings
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val fingerprintEnabled = sharedPreferences.getBoolean("fingerprint_enabled", false)
        val savedEmail = sharedPreferences.getString(KEY_EMPLOYEE_EMAIL, null)

        if (fingerprintEnabled && savedEmail != null) {
            // Show fingerprint prompt only when the fingerprint button is pressed
            binding.btnFingerprint.setOnClickListener {
                showBiometricPrompt()
            }
        }

        // Regular login button click
        binding.btnLogin.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                showLoadingState(true) // Activate ProgressBar and disable buttons
                loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBiometricPrompt() {
        biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val savedEmail = sharedPreferences.getString(KEY_EMPLOYEE_EMAIL, null)
                    if (savedEmail != null) {
                        showLoadingState(true) // Activate ProgressBar and disable buttons
                        loginUser(savedEmail, "") // Call loginUser without password
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

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
        RetrofitInstance.api.getEmployee(email).enqueue(object : Callback<EmployeeResponse> {
            override fun onResponse(call: Call<EmployeeResponse>, response: Response<EmployeeResponse>) {
                showLoadingState(false) // Deactivate ProgressBar and enable buttons
                if (response.isSuccessful && response.body() != null) {
                    val employee = response.body()!!
                    if (password.isEmpty() || employee.password == password) {
                        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString(KEY_EMPLOYEE_EMAIL, email)
                            putBoolean("fingerprint_enabled", true)
                            apply()
                        }
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        Toast.makeText(requireContext(), "Invalid password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Connection failed, check your internet connection and try again", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                showLoadingState(false) // Deactivate ProgressBar and enable buttons
                Toast.makeText(requireContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnFingerprint.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

