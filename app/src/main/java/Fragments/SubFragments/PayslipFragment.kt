// Fragments/SubFragments/PayslipFragment.kt
package Fragments.SubFragments

import Instance.RetrofitInstance
import Models.Payslip
import PayslipAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentPayslipBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PayslipFragment : Fragment() {

    private var _binding: FragmentPayslipBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPayslipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClosePay.setOnClickListener {
            findNavController().navigate(R.id.action_payslipFragment_to_requestFragment)
        }

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val employeeEmail = sharedPref.getString("employee_email", null)

        if (employeeEmail != null) {
            fetchPayslips(employeeEmail)
        } else {
            Toast.makeText(requireContext(), "Employee email not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPayslips(email: String) {
        RetrofitInstance.api.getPayslips(email).enqueue(object : Callback<List<Payslip>> {
            override fun onResponse(call: Call<List<Payslip>>, response: Response<List<Payslip>>) {
                if (response.isSuccessful && response.body() != null) {
                    val payslipList = response.body()!!
                    setupRecyclerView(payslipList)
                } else {
                    Toast.makeText(requireContext(), "Failed to load payslips", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Payslip>>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show()
                Log.e("PayslipFragment", "Error: ${t.message}", t)
            }
        })
    }

    private fun setupRecyclerView(payslipList: List<Payslip>) {
        binding.rcPayslips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = PayslipAdapter(requireContext(), payslipList) // Pass context and list
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
