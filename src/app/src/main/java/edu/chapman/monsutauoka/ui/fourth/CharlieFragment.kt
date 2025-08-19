package edu.chapman.monsutauoka.ui.fourth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.databinding.FragmentCharlieBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.extensions.applySystemBarPadding
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase
import edu.chapman.monsutauoka.ui.wallet.*


class CharlieFragment : MainFragmentBase<FragmentCharlieBinding>() {

    private val viewModel: CharlieViewModel by viewModels {
        GenericViewModelFactory {
            CharlieViewModel(mainActivity.stepCounterService)
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
    ): FragmentCharlieBinding {
        return FragmentCharlieBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)
        binding.root.applySystemBarPadding()
        binding.sticksAmount.text = "0"

        viewModel.steps.observe(viewLifecycleOwner) { stepCount ->
            binding.stepAmount.text = stepCount.toString()
        }

        walletVm.sticks.observe(viewLifecycleOwner) { balance ->
            binding.sticksAmount.text = balance.toString()
        }

        // on button click we take away from our balance
        binding.button.setOnClickListener {
            walletVm.spend(1)
        }

    }


}



