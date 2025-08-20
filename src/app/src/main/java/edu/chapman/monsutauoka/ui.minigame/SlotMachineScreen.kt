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

//Shows slot machine
@Composable
fun SlotMachineScreen() {
    // Build the VM with the *same* StickRepository singleton used everywhere
    val vm: MiniGameViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                //create MiniGameViewModel --> shared Stick Repository
                return MiniGameViewModel(ServiceLocator.stickRepo) as T
            }
        }
    )
    //updates automatically current num of sticks from viewmodel
    val sticks by vm.sticksFlow.collectAsStateWithLifecycle(initialValue = 0)
    //update on spin, current slot results from viewmodel
    val slots by vm.slots.collectAsStateWithLifecycle()
    //gets most recent payout from viewmodel
    val lastPayout by vm.lastPayout.collectAsStateWithLifecycle()

    //vertical layout(UI)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //shows how many sticks you have
        Text("Sticks: $sticks", color = Color.White, fontSize = 20.sp)

        Spacer(Modifier.height(12.dp))  //adds space between stick num and slot (aesthetics)

        //show slot machine results
        Row {
            slots.forEach { //reads each slot result
                Text(
                    text = it,                              //shows result
                    color = Color.Yellow,   //makes slot yellow
                    fontSize = 48.sp,                        //text size
                    modifier = Modifier.padding(8.dp)
                )      //add "padding" (space)
            }
        }

        Spacer(Modifier.height(16.dp))  //space before button (aesthetic)

        //Spin button
        Button(
            onClick = vm::spinSlotMachine,  //when clicked, spins machine
            enabled = sticks >= MiniGameViewModel.SPIN_COST //checks if ur allowed to spin(enabled when you have enough)
        ) {
            Text("Spin (Cost ${MiniGameViewModel.SPIN_COST})")
        }

        Spacer(Modifier.height(12.dp))

        //results of last spin
        val resultText = when {
            lastPayout > 0  -> "You won +$lastPayout sticks!"   //num of sticks gained
            lastPayout == 0 -> if (sticks < MiniGameViewModel.SPIN_COST) "Not enough sticks." else "No change." //not enough or broke even
            else            -> "You lost ${-lastPayout} stick(s)."  //num of sticks lost
        }
        Text(resultText, color = Color.White)

        Spacer(Modifier.height(12.dp))
        //debug  (TESTING!!)
        OutlinedButton(onClick = { vm.addDebugSticks() }) { Text("Add +5 sticks") }
    }
}
