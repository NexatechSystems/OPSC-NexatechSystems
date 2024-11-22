package Fragments

import Adapters.AnnouncementAdapter
import Models.Announcement
import Instance.RetrofitInstance
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentAnnouncementBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnnouncementFragment : Fragment() {
    private var _binding: FragmentAnnouncementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.rcAnnouncements.layoutManager = LinearLayoutManager(requireContext())

        // Fetch announcements
        fetchAnnouncements()

        binding.btnMessageLeft.setOnClickListener {
            findNavController().navigate(R.id.action_announcementFragment_to_messageFragment)
        }
    }

    private fun fetchAnnouncements() {
        // Show the ProgressBar and hide the RecyclerView initially
        binding.progressBar.visibility = View.VISIBLE
        binding.rcAnnouncements.visibility = View.GONE

        RetrofitInstance.api.getAnnouncements().enqueue(object : Callback<List<Announcement>> {
            override fun onResponse(call: Call<List<Announcement>>, response: Response<List<Announcement>>) {
                // Hide the ProgressBar after getting the response
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val announcements = response.body()!!
                    binding.rcAnnouncements.visibility = View.VISIBLE
                    binding.rcAnnouncements.adapter = AnnouncementAdapter(announcements)
                } else {
                    Toast.makeText(requireContext(), "No announcements found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Announcement>>, t: Throwable) {
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
