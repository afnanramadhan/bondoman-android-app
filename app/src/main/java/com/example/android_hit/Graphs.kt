package com.example.android_hit


import java.text.NumberFormat
import java.util.Locale
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android_hit.adapter.TransactionAdapter
import com.example.android_hit.databinding.FragmentGraphsBinding
import com.example.android_hit.room.TransactionDB
import com.example.android_hit.room.TransactionEntity
import com.example.android_hit.utils.MyValueFormatter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter


class Graphs : Fragment() {
    private lateinit var binding: FragmentGraphsBinding
    private lateinit var pieChart: PieChart
    private lateinit var adapter: TransactionAdapter
    private lateinit var database: TransactionDB
    private var list = mutableListOf<TransactionEntity>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGraphsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TransactionAdapter(list)
        database = TransactionDB.getInstance(requireContext())
        getData()

        pieChart = view.findViewById(R.id.pie_chart)
        val list:ArrayList<PieEntry> = ArrayList()

        val totalExpenseAmount = database.transactionDao.getTotalExpense().toFloat()
        val totalIncomeAmount = database.transactionDao.getTotalIncome().toFloat()
        Log.e("PIE", totalIncomeAmount.toString())
        Log.e("PIE", totalExpenseAmount.toString())
        val percentageExpense = (totalExpenseAmount/(totalExpenseAmount+totalIncomeAmount))*100
        val percentageIncome = (totalIncomeAmount/(totalExpenseAmount+totalIncomeAmount))*100
        Log.e("PIE", percentageExpense.toString())
        Log.e("PIE", percentageIncome.toString())

        list.add(PieEntry(percentageExpense,"Expense"))
        list.add(PieEntry(percentageIncome,"Income"))

        val pieDataSet= PieDataSet(list,"")
        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#EFDAC7"))
        colors.add(Color.parseColor("#B1B8D8"))


        pieDataSet.colors = colors
        pieDataSet.valueTextColor= Color.BLACK
        pieDataSet.valueTextSize=15f
        pieDataSet.valueFormatter = MyValueFormatter()

        val pieData= PieData(pieDataSet)

        pieChart.data= pieData
        pieChart.description.isEnabled = false
        pieChart.legend.textSize = 18f
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        pieChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        pieChart.setDrawEntryLabels(false)

        pieChart.animateY(2000)
    }

    private fun getData() {
        list.clear()
        list.addAll(database.transactionDao.getAllTransaction())
        adapter.notifyDataSetChanged()
        val totalExpenseAmount = database.transactionDao.getTotalExpense()
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        binding.expenseValueText.text = currencyFormat.format(totalExpenseAmount)
        val totalIncomeAmount = database.transactionDao.getTotalIncome()
        binding.incomeValueText.text = currencyFormat.format(totalIncomeAmount)
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

}