package edu.chapman.monsutauoka.ui.fourth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.MainActivity
import edu.chapman.monsutauoka.databinding.FragmentAlphaBinding
import edu.chapman.monsutauoka.databinding.FragmentCharlieBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.extensions.applySystemBarPadding
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase
import edu.chapman.monsutauoka.ui.first.CharlieViewModel

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
            binding.textCharlie.text = stepCount.toString()
        }
    }

}



