package edu.chapman.monsutauoka.ui.fourth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.databinding.FragmentCharlieBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.extensions.applySystemBarPadding
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase

class CharlieFragment : MainFragmentBase<FragmentCharlieBinding>() {

    private val viewModel: CharlieViewModel by viewModels {
        GenericViewModelFactory {
            CharlieViewModel(mainActivity.stepCounterService)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCharlieBinding {
        return FragmentCharlieBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)
        binding.root.applySystemBarPadding()

        viewModel.steps.observe(viewLifecycleOwner) { stepCount ->
            binding.stepAmount.text = stepCount.toString()
        }

        viewModel.sticks.observe(viewLifecycleOwner) { balance ->
            binding.sticksAmount.text = balance.toString()
        }

        binding.button.setOnClickListener {
            val feeding = viewModel.useStick(1)
            if(!feeding) {
                Log.d(TAG, "NOT ENOUGH STICKS")
            }
        }

    }


}



