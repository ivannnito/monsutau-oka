package edu.chapman.monsutauoka.ui.third

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.databinding.FragmentGammaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase

class GammaFragment : MainFragmentBase<FragmentGammaBinding>() {

    private val viewModel: GammaViewModel by viewModels {
        GenericViewModelFactory {
            GammaViewModel()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGammaBinding {
        return FragmentGammaBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.text.observe(viewLifecycleOwner) { txt ->
            if (binding.content.text.toString() != txt) {
                binding.content.setText(txt)
                binding.content.setSelection(binding.content.text?.length ?: 0)
            }
        }

        viewModel.busy.observe(viewLifecycleOwner) { isBusy ->
            binding.progress.visibility = if (isBusy) View.VISIBLE else View.GONE
            binding.save.isEnabled = !isBusy
            binding.content.isEnabled = !isBusy
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }
        }

        binding.save.setOnClickListener {
            viewModel.save(binding.content.text?.toString().orEmpty())
        }
    }

    override fun onStart() {
        Log.d(TAG, ::onStart.name)
        super.onStart()
        viewModel.load()
    }
}