package edu.chapman.monsutauoka.ui.second

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.NotificationReceiver
import edu.chapman.monsutauoka.databinding.FragmentAlphaBinding
import edu.chapman.monsutauoka.databinding.FragmentBetaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase
import edu.chapman.monsutauoka.ui.first.AlphaViewModel
import edu.chapman.monsutauoka.ui.minigame.SlotMachineScreen
import edu.chapman.monsutauoka.ui.wallet.WalletViewModel
import edu.chapman.monsutauoka.ui.wallet.WalletVmFactory
import kotlin.getValue

class BetaFragment : MainFragmentBase<FragmentBetaBinding>() {

    private val viewModel: AlphaViewModel by viewModels {
        GenericViewModelFactory {
            AlphaViewModel(mainActivity.stepCounterService)
        }
    }

    private val walletVm: WalletViewModel by activityViewModels {
        WalletVmFactory(edu.chapman.monsutauoka.services.data.di.ServiceLocator.stickRepo)
    }


    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBetaBinding {
        return FragmentBetaBinding.inflate(inflater, container, false)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent { MaterialTheme { SlotMachineScreen() } }
        }
    }

}