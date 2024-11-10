package Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nexatech.staffsyncv3.MainActivity
import com.nexatech.staffsyncv3.R
import com.nexatech.staffsyncv3.databinding.FragmentClockingBinding
import com.nexatech.staffsyncv3.databinding.FragmentRequestBinding


class RequestFragment : Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = (activity as? MainActivity)?.findViewById(R.id.bottomNavigationView) ?: return
        fab = (activity as? MainActivity)?.findViewById(R.id.fab) ?: return

        binding.btnPersonalInfo.setOnClickListener{
            findNavController().navigate(R.id.action_requestFragment_to_personalFragment)
            closeNavbar()
        }

        binding.btnPayslips.setOnClickListener {
            findNavController().navigate(R.id.action_requestFragment_to_payslipFragment)
            closeNavbar()
        }

        binding.btnLeaveRequests.setOnClickListener {
            findNavController().navigate(R.id.action_requestFragment_to_leaveFragment)
            closeNavbar()
        }
        
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun closeNavbar()
    {
        bottomNavigationView.visibility = View.GONE
        fab.hide()
    }


}