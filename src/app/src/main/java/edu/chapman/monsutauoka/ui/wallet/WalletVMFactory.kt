package edu.chapman.monsutauoka.ui.wallet

import androidx.lifecycle.ViewModel
import edu.chapman.monsutauoka.data.StickRepository


class WalletVmFactory(
    private val repo: StickRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(repo) as T
        }
        error("Unknown VM class: $modelClass")
    }
}
