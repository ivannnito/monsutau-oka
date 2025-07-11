package edu.chapman.monsutauoka.ui.fourth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.databinding.FragmentCharlieBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.fourth.CharlieViewModel

class CharlieFragment : Fragment() {
    private var _binding: FragmentCharlieBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CharlieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, ::onCreateView.name)

        _binding = FragmentCharlieBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)

        // When first load, make our viewmodel text appear
        binding.textCharlie.text = viewModel.text.value;

        // Android doesn't like this but manually changed oopsies
        binding.button.text = "Apply text here."

        // Here we modify our text
        // Use a ViewModel to update a the TextView
        // from EditText on button click

        // when button is clicked
        binding.button.setOnClickListener{
            Log.i(TAG, "Button Pressed")

            // our text value is set to what our user has inputted
            //viewModel.text.value = viewModel.text.toString()
            binding.textCharlie.text = binding.editTextText.text
            Log.i(TAG, "Text updated from Button Pressed")
        }

    }

    override fun onDestroyView() {
        Log.d(TAG, ::onDestroyView.name)

        super.onDestroyView()
        _binding = null
    }
}