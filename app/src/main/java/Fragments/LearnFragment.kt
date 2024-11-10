package Fragments

import Adapters.EventAdapter
import Adapters.MessageAdapter
import Models.Event
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentLearnBinding
import com.nexatech.staffsyncv3.databinding.FragmentMessageBinding

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    private val events = listOf(
        Event("Event 1", "Description for event 1", "https://example.com/1"),
        Event("Event 2", "Description for event 2", "https://example.com/2"),
        Event("Event 1", "Description for event 1", "https://example.com/1"),
        Event("Event 1", "Description for event 1", "https://example.com/1"),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcEvents.layoutManager = LinearLayoutManager(context)
        binding.rcEvents.adapter = EventAdapter(events)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}