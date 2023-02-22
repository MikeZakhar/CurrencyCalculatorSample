package com.zakorchook.currencytest.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zakorchook.currencytest.Constants
import com.zakorchook.currencytest.R
import com.zakorchook.currencytest.data.db.HistoryEntity
import com.zakorchook.currencytest.databinding.FragmentHistoryBinding
import com.zakorchook.currencytest.databinding.ItemHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val historyViewModel: HistoryViewModel by viewModels()

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        historyViewModel.history.observe(viewLifecycleOwner) {
            initList(it)
        }
        historyViewModel.requestHistory()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initList(list: List<HistoryEntity>) {
        binding.apply {
            if (list.isEmpty()) {
                rvHistory.visibility = View.GONE
                tvEmptyList.visibility = View.VISIBLE
            } else {
                tvEmptyList.visibility = View.GONE
                rvHistory.apply {
                    visibility = View.VISIBLE
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = Adapter(list)
                }
            }
        }
    }

    private class Adapter(private val list: List<HistoryEntity>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {

        val dateFormatter = SimpleDateFormat(Constants.UI_DATE_FORMAT, Locale.US)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemHistoryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            list[position].let { item ->
                holder.binding.apply {
                    tvDate.text = dateFormatter.format(item.date)
                    tvValue.text = tvValue.context.getString(
                        R.string.exchange_result_format,
                        item.srcAmount, item.dstAmount, item.currencyName
                    ).replace("\n", " ")
                }
            }
        }

        private class ViewHolder(val binding: ItemHistoryBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}