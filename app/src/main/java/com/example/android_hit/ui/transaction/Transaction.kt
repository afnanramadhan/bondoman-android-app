package com.example.android_hit.ui.transaction

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_hit.adapter.TransactionAdapter
import com.example.android_hit.databinding.FragmentTransactionBinding
import com.example.android_hit.room.TransactionDB
import com.example.android_hit.room.TransactionEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale


class Transaction : Fragment() {
    private lateinit var binding: FragmentTransactionBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var database: TransactionDB
    private lateinit var fab: FloatingActionButton
    private var list = mutableListOf<TransactionEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvTransaction

        fab = binding.fabAdd

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        adapter = TransactionAdapter(list)
        recyclerView.adapter = adapter
        fab.setOnClickListener {
            startActivity(Intent(requireContext(), DetailTransactionActivity::class.java))
        }

        adapter.setOnDeleteClickListener(object : TransactionAdapter.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                deleteTransaction(position)
            }
        })

        database = TransactionDB.getInstance(requireContext())
        getData()
    }

    private fun deleteTransaction(position: Int) {
        val deletedItem = list[position]
        if (deletedItem.category == "Expense") {
            val expenseAmount = deletedItem.amount
            val currentTotalExpense = database.transactionDao.getTotalExpense()
            val newTotalExpense = currentTotalExpense - expenseAmount
            binding.amountExpense.text = newTotalExpense.toString()
        } else {
            val incomeAmount = deletedItem.amount
            val currentTotalIncome = database.transactionDao.getTotalIncome()
            val newTotalIncome = currentTotalIncome - incomeAmount
            binding.amountIncome.text = newTotalIncome.toString()
        }
        database.transactionDao.deleteTransaction(deletedItem)
        list.removeAt(position)
        adapter.notifyItemRemoved(position)
        getIncomeExpense()
    }

    private fun getIncomeExpense() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val totalExpenseAmount = database.transactionDao.getTotalExpense()
        val totalIncomeAmount = database.transactionDao.getTotalIncome()
        binding.amountExpense.text = currencyFormat.format(totalExpenseAmount)
        binding.amountIncome.text = currencyFormat.format(totalIncomeAmount)
    }

    private fun getData() {
        list.clear()
        list.addAll(database.transactionDao.getAllTransaction())
        adapter.notifyDataSetChanged()
        getIncomeExpense()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

}