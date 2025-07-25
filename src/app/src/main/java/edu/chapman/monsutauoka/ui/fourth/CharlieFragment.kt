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
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase
import edu.chapman.monsutauoka.ui.fourth.CharlieViewModel

class CharlieFragment : MainFragmentBase<FragmentCharlieBinding>() {
    private var _binding: FragmentCharlieBinding? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCharlieBinding {
        return FragmentCharlieBinding.inflate(inflater, container, false)
    }

    private val viewModel: CharlieViewModel by viewModels {
        GenericViewModelFactory {
            CharlieViewModel(mainActivity.stepCounterService)
        }
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
//        viewModel.text.observe(viewLifecycleOwner) { textValue ->
//            Log.i(TAG, "TextValue: " + textValue + "\n" + "Text Changed: " + binding.editText.text)
//            // text isn't changing correctly, changes the EditText attribute instead of the actual
//            // textCharlie attribute? textCharlie doesn't show up unless the viewModel.text has the default value
//            // set to it, instead of the correctly updated binding.textCharlie.text = textValue, textValue is always the default text
//            binding.textCharlie.text = textValue.toString()
//
//        }

        viewModel.steps.observe(viewLifecycleOwner) { stepValue ->
            Log.i(TAG, "CharlieFragment Step Value: " + stepValue)
            binding.stepCounterText.text = stepValue.toString()

        }

    }

    override fun onDestroyView() {
        Log.d(TAG, ::onDestroyView.name)

        super.onDestroyView()
        _binding = null
    }
}