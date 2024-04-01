package com.example.android_hit

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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Transaction.newInstance] factory method to
 * create an instance of this fragment.
 */
class Transaction : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentTransactionBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var database: TransactionDB
    private lateinit var fab: FloatingActionButton
    private var list = mutableListOf<TransactionEntity>()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

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
        val totalExpenseAmount = database.transactionDao.getTotalExpense()
        binding.amountExpense.text = totalExpenseAmount.toString()
    }

    private fun deleteTransaction(position: Int) {
        val deletedItem = list[position]
        // Kurangi total expense amount jika transaksi yang dihapus memiliki kategori expense
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
        // Hapus transaksi dari database dan daftar transaksi
        database.transactionDao.deleteTransaction(deletedItem)
        list.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun getData() {
        list.clear()
        list.addAll(database.transactionDao.getAllTransaction())
        adapter.notifyDataSetChanged()
        val totalExpenseAmount = database.transactionDao.getTotalExpense()
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        binding.amountExpense.text = currencyFormat.format(totalExpenseAmount)
        val totalIncomeAmount = database.transactionDao.getTotalIncome()
        binding.amountIncome.text = currencyFormat.format(totalIncomeAmount)
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Transaction.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Transaction().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}