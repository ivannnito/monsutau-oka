package edu.chapman.monsutauoka.ui.minigame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.chapman.monsutauoka.viewmodel.MiniGameViewModel
import edu.chapman.monsutauoka.data.StickRepository
import edu.chapman.monsutauoka.services.data.di.ServiceLocator // wherever your singleton lives

@Composable
fun SlotMachineScreen() {
    // Build the VM with the *same* StickRepository singleton used everywhere
    val vm: MiniGameViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MiniGameViewModel(ServiceLocator.stickRepo) as T
            }
        }
    )

    val sticks by vm.sticksFlow.collectAsStateWithLifecycle(initialValue = 0)
    val slots by vm.slots.collectAsStateWithLifecycle()
    val lastPayout by vm.lastPayout.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sticks: $sticks", color = Color.White, fontSize = 20.sp)

        Spacer(Modifier.height(12.dp))

        Row {
            slots.forEach {
                Text(text = it, color = Color.Yellow, fontSize = 48.sp, modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = vm::spinSlotMachine,
            enabled = sticks >= MiniGameViewModel.SPIN_COST
        ) {
            Text("Spin (Cost ${MiniGameViewModel.SPIN_COST})")
        }

        Spacer(Modifier.height(12.dp))

        val resultText = when {
            lastPayout > 0  -> "You won +$lastPayout sticks!"
            lastPayout == 0 -> if (sticks < MiniGameViewModel.SPIN_COST) "Not enough sticks." else "No change."
            else            -> "You lost ${-lastPayout} stick(s)."
        }
        Text(resultText, color = Color.White)

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { vm.addDebugSticks() }) { Text("Add +5 sticks") }
    }
}
