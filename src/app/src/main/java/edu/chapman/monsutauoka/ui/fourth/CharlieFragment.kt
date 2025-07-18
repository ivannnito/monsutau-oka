package edu.chapman.monsutauoka.ui.fourth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.MainActivity
import edu.chapman.monsutauoka.databinding.FragmentCharlieBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.fourth.CharlieViewModel

class CharlieFragment : Fragment() {
    private var _binding: FragmentCharlieBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CharlieViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val main = requireActivity() as MainActivity
        val service = main.getStepCounterService()
        viewModel.initialize(service)
    }



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
        // Here we modify our text
        // Use a ViewModel to update a the TextView
        // from EditText on button click

        // when button is clicked
        binding.button.setOnClickListener{
            Log.i(TAG, "Button Pressed")

            viewModel.text.value = binding.editText.text.toString()
            // our text value is set to what our user has inputted
            //viewModel.text.value = viewModel.text.toString()

            Log.i(TAG, "Text updated from Button Pressed")
        }

        // saving previous value through our observer
        viewModel.steps.observe(viewLifecycleOwner) { stepValue ->
            //Log.i(TAG, "TextValue: " + textValue + "\n" + "Text Changed: " + binding.editText.text)
            // text isn't changing correctly, changes the EditText attribute instead of the actual
            // textCharlie attribute? textCharlie doesn't show up unless the viewModel.text has the default value
            // set to it, instead of the correctly updated binding.textCharlie.text = textValue, textValue is always the default text

            // MODIFIED SO THAT STEPS ARE JUST RENDERED
            binding.textCharlie.text = stepValue.toString()
        }


    }

    override fun onDestroyView() {
        Log.d(TAG, ::onDestroyView.name)

        super.onDestroyView()
        _binding = null
    }
}