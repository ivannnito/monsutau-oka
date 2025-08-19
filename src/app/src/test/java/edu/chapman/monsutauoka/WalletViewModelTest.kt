package edu.chapman.monsutauoka.ui.wallet

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import app.cash.turbine.test
import edu.chapman.monsutauoka.data.StickRepository

@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {

    @get:Rule val instant = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var repo: StickRepository
    private lateinit var vm: WalletViewModel

    private val balance = MutableStateFlow(10) // start with 10 sticks

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        // Mock repo
        repo = mockk<StickRepository>(relaxed = false)

        // Expose the flow the VM turns into LiveData
        every { repo.balanceFlow } returns balance

        // Spend subtracts when enough
        coEvery { repo.spend(any()) } answers {
            val n = firstArg<Int>()
            val ok = balance.value >= n
            if (ok) balance.value = balance.value - n
            ok
        }

        // Grant adds
        coEvery { repo.grant(any()) } answers {
            val n = firstArg<Int>()
            balance.value = balance.value + n
            Unit
        }

        vm = WalletViewModel(repo) // uses sticks LiveData + spendResults  :contentReference[oaicite:1]{index=1}
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `grant increases sticks LiveData`() = scope.runTest {
        val observed = mutableListOf<Int>()
        val obs = Observer<Int> { if (it != null) observed.add(it) }
        vm.sticks.observeForever(obs)

        vm.grant(5)
        advanceUntilIdle()

        assertEquals(listOf(10, 10, 15), observed) // initial + updated
        coVerify { repo.grant(5) }

        vm.sticks.removeObserver(obs)
    }

    @Test
    fun `spend emits true when enough and decreases balance`() = scope.runTest {
        vm.spendResults.test {
            vm.spend(3)
            advanceUntilIdle()
            assertTrue(awaitItem())                  // spend OK
            assertEquals(7, balance.value)           // 10 - 3
        }
        coVerify { repo.spend(3) }
    }

    @Test
    fun `spend emits false when insufficient and leaves balance`() = scope.runTest {
        // Drain to 0
        vm.spend(10)
        advanceUntilIdle()

        vm.spendResults.test {
            vm.spend(1)
            advanceUntilIdle()
            val ok = awaitItem()
            assertEquals(false, ok)
            assertEquals(0, balance.value)
        }
        coVerify { repo.spend(1) }
    }
}

/** Tiny helper if you ever need a blocking value from LiveData. */
fun <T> LiveData<T>.observeOnce(): T? {
    var v: T? = null
    val obs = object : Observer<T> {
        override fun onChanged(t: T) { v = t; removeObserver(this) }
    }
    observeForever(obs)
    return v
}
