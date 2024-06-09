package org.d3ifcool.telyuconsign.FragmentMain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import org.d3ifcool.telyuconsign.ui.AddActivity
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.databinding.FragmentAddBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(layoutInflater)
        return inflater.inflate(R.layout.fragment_add, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addBtn = view.findViewById<Button>(R.id.addBtn)
        addBtn.setOnClickListener {
            val intent = android.content.Intent(activity, AddActivity::class.java)
            startActivity(intent)
        }
    }
}