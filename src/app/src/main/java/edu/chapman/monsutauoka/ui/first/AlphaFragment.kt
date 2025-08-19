package edu.chapman.monsutauoka.ui.first

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import edu.chapman.monsutauoka.databinding.FragmentAlphaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.extensions.applySystemBarPadding
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlphaFragment : MainFragmentBase<FragmentAlphaBinding>() {

    private val viewModel: AlphaViewModel by viewModels {
        GenericViewModelFactory {
            AlphaViewModel(mainActivity.stepCounterService)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAlphaBinding {
        return FragmentAlphaBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, ::onViewCreated.name)
        binding.root.applySystemBarPadding()

        viewModel.steps.observe(viewLifecycleOwner) { stepCount ->
            binding.textSteps.text = stepCount.toString()
        }

        mainActivity.moodAndActivity.timeCheck(binding.displayImage, binding.moodText)

        binding.feedButton.setOnClickListener {
            mainActivity.moodAndActivity.feed(binding.displayImage, binding.moodText)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    mainActivity.moodAndActivity.timeCheck(binding.displayImage, binding.moodText)
                    delay(10_000) //10 sec
                }
            }
        }
    }
}