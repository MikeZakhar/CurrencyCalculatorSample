package com.zakorchook.currencytest.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.zakorchook.currencytest.Constants
import com.zakorchook.currencytest.R
import com.zakorchook.currencytest.data.model.ExchangeResult
import com.zakorchook.currencytest.databinding.FragmentHomeBinding
import com.zakorchook.currencytest.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private val uiDateFormat = SimpleDateFormat(Constants.UI_DATE_FORMAT, Locale.US)
    private var selectedDate = Date()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initListeners()
        initUi()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        binding.textInputLayout.editText?.apply {
            doOnTextChanged { text, _, _, _ ->
                binding.btnExchange.isEnabled = !text.isNullOrBlank()
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE && text.toString().isNotBlank())
                    doExchange()
                false
            }
        }

        binding.btnExchange.setOnClickListener {
            doExchange()
        }

        binding.tvSelectedDate.apply {
            text = uiDateFormat.format(selectedDate)
            setOnClickListener { showDatePicker() }
        }
    }

    private fun initListeners() {
        homeViewModel.apply {
            allCurrenciesResult.observe(viewLifecycleOwner) {
                binding.spinnerCurrency.adapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_item, it
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            }
            exchangeResult.observe(viewLifecycleOwner) {
                binding.tvResult.text = combineResultToMessage(it)
            }
            exception.observe(viewLifecycleOwner) {
                binding.tvResult.text = null
                showException(it)
            }
            progress.observe(viewLifecycleOwner) {
                showProgress(it)
            }
        }
    }

    private fun combineResultToMessage(exchangeResult: ExchangeResult): String {
        exchangeResult.apply {
            return getString(R.string.exchange_result_format, srcAmount, resultAmount, currencyName)
        }
    }

    private fun showDatePicker() {
        MaterialDatePicker.Builder.datePicker()
            .setSelection(selectedDate.time)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                    .setValidator(DateValidatorPointBackward.now())
                    .build()
            )
            .build().apply {
                addOnPositiveButtonClickListener {
                    selectedDate = Date(it)
                    binding.tvSelectedDate.text = uiDateFormat.format(selectedDate)
                }
            }.show(childFragmentManager, null)
    }

    private fun doExchange() {
        homeViewModel.requestExchange(
            binding.spinnerCurrency.selectedItem as String,
            binding.textInputLayout.editText?.text?.toString()?.toFloat() ?: 0f,
            selectedDate
        )
    }

    private fun showException(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.title_exception)
            .setMessage(message)
            .setPositiveButton(android.R.string.cancel, null)
            .show()
    }

    private fun showProgress(show: Boolean) {
        (activity as? MainActivity)?.showProgress(show)
    }
}