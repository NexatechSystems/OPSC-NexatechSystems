package Fragments

import Adapters.EventAdapter
import Adapters.MessageAdapter
import Models.Event
import Models.Message
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentMessageBinding

class MessageFragment : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private val messages = listOf(
        Message("Message 1", "This is the first message."),
        Message("Message 2", "This is the second message."),
        Message("Message 2", "This is the second message."),
        Message("Message 2", "This is the second message."),

    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView for Messages
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewMessages.adapter = MessageAdapter(messages)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
