package Fragments.SubFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nexatech.staffsyncv3.MainActivity
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentMessageBinding
import com.nexatech.staffsyncv3.databinding.FragmentPersonalBinding

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClosePersonal.setOnClickListener {
            closeFragment()
        }

        binding.btnSave.setOnClickListener {

        }

        binding.txtChangeEmail.addTextChangedListener {

            binding.btnSave.visibility = View.VISIBLE

        }

        binding.txtChangePhone.addTextChangedListener {

            binding.btnSave.visibility = View.VISIBLE

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun closeFragment()
    {
        bottomNavigationView = (activity as? MainActivity)?.findViewById(R.id.bottomNavigationView) ?: return

        findNavController().navigate(R.id.action_personalFragment_to_requestFragment)
        bottomNavigationView.visibility = View.VISIBLE
    }

}