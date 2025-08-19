package edu.chapman.monsutauoka.ui.first

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
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
import edu.chapman.monsutauoka.services.MoodAndActivity.*
import edu.chapman.monsutauoka.ui.wallet.WalletViewModel
import edu.chapman.monsutauoka.ui.wallet.WalletVmFactory
import kotlin.getValue


class AlphaFragment : MainFragmentBase<FragmentAlphaBinding>() {

    private val viewModel: AlphaViewModel by viewModels {
        GenericViewModelFactory {
            AlphaViewModel(mainActivity.stepCounterService)
        }
    }

    /*
   * Our global wallet, saves state and balance between exiting the application and coming back
   * every fragment will use this
   * */
    private val walletVm: WalletViewModel by activityViewModels {
        WalletVmFactory(edu.chapman.monsutauoka.services.data.di.ServiceLocator.stickRepo)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAlphaBinding {
        return FragmentAlphaBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewStick.bringToFront()
        binding.sticksAmount.bringToFront()
        binding.root.invalidate()
        binding.root.applySystemBarPadding()

        // steps display (unchanged)
        viewModel.steps.observe(viewLifecycleOwner) { stepCount ->
            binding.textSteps.text = stepCount.toString()
        }

        // wallet balance display (unchanged)
        walletVm.sticks.observe(viewLifecycleOwner) { balance ->
            binding.sticksAmount.text = balance.toString()
        }

        // 1) Try to spend a stick when the button is pressed
        binding.feedButton.setOnClickListener {
            walletVm.spend(1)
        }

        // 2) React to the result of spending:
        //    - success -> advance mood via MoodAndActivity
        //    - failure -> show your system notification (or snackbar)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletVm.spendResults.collect { ok ->
                    if (ok) {
                        // Only feed if we actually spent a stick
                        mainActivity.moodAndActivity.feedWithoutWallet(
                            binding.displayImage,
                            binding.moodText
                        )
                    } else {
                        // Use the notifier you already wrote earlier
                        Log.d(TAG, "NOT ENOUGH STICKS")
                    }
                }
            }
        }

        // decay check
        mainActivity.moodAndActivity.timeCheck(binding.displayImage, binding.moodText)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    mainActivity.moodAndActivity.timeCheck(binding.displayImage, binding.moodText)
                    delay(10_000)
                }
            }
        }
    }
}