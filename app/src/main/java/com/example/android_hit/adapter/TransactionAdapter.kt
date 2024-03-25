package com.example.android_hit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_hit.R
import com.example.android_hit.room.TransactionEntity

class TransactionAdapter(var list: List<TransactionEntity>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title = view.findViewById<TextView>(R.id.title)
        var amount = view.findViewById<TextView>(R.id.amount)
        var category = view.findViewById<TextView>(R.id.category)
        var location = view.findViewById<TextView>(R.id.location)
        var date = view.findViewById<TextView>(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.title.text = list[position].title
        holder.amount.text = list[position].amount.toString()
        holder.category.text = list[position].category
        holder.location.text = list[position].location
        holder.date.text = list[position].date
    }

    override fun getItemCount(): Int {
        return list.size
    }
}