package edu.chapman.monsutauoka.ui.minigame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.chapman.monsutauoka.model.TreatStore
import edu.chapman.monsutauoka.viewmodel.MiniGameViewModel

@Composable
fun SlotMachineScreen() {
    val context = LocalContext.current
    val viewModel = remember { MiniGameViewModel(TreatStore(context)) }

    val dollars by viewModel.dollars.collectAsState()
    val slots by viewModel.slots.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dollars: $dollars", color = Color.White, fontSize = 20.sp)

        Row(modifier = Modifier.padding(24.dp)) {
            slots.forEach {
                Text(text = it, fontSize = 48.sp, modifier = Modifier.padding(8.dp), color = Color.Yellow)
            }
        }

        Button(onClick = viewModel::spinSlotMachine, enabled = dollars > 0) {
            Text("Spin Slot Machine")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.addFakeMoneyForDebugging() }) {
            Text("Add Debug $5")
        }
    }
}
