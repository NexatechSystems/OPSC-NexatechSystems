package Fragments

import Adapters.MessageAdapter
import Models.Message
import Instance.RetrofitInstance
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentMessageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageFragment : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext())

        // Retrieve employee email from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val employeeEmail = sharedPreferences.getString("employee_email", null)

        if (employeeEmail != null) {
            fetchMessages(employeeEmail)
        } else {
            Toast.makeText(requireContext(), "Failed to retrieve employee email", Toast.LENGTH_SHORT).show()
        }

        binding.btnAnnouncementRight.setOnClickListener {
            findNavController().navigate(R.id.action_messageFragment_to_announcementFragment)
        }
    }

    private fun fetchMessages(employeeEmail: String) {
        // Show the ProgressBar and hide the RecyclerView initially
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewMessages.visibility = View.GONE

        RetrofitInstance.api.getMessages(employeeEmail).enqueue(object : Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                // Hide the ProgressBar after getting the response
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val messages = response.body()!!
                    binding.recyclerViewMessages.visibility = View.VISIBLE
                    binding.recyclerViewMessages.adapter = MessageAdapter(messages)
                } else {
                    Toast.makeText(requireContext(), "No messages found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
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
