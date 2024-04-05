package com.example.android_hit.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.android_hit.ui.transaction.DetailTransactionActivity
import com.example.android_hit.R
import com.example.android_hit.databinding.RowTransactionBinding
import com.example.android_hit.room.TransactionEntity
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(private val list: MutableList<TransactionEntity>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(private val binding: RowTransactionBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(transaction: TransactionEntity) {
            binding.apply {
                title.text = transaction.title
                val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                amount.text = currencyFormat.format(transaction.amount)
                category.text = transaction.category
                location.text = transaction.location
                date.text = transaction.timestamp

                val colorId = if (transaction.category == "Expense") R.color.secondary4 else R.color.secondary5
                category.setTextColor(ContextCompat.getColor(binding.root.context, colorId))

                deleteButton.setOnClickListener {
                    onDeleteClickListener?.onDeleteClick(adapterPosition)
                }

                editButton.setOnClickListener {
                    val intent = Intent(binding.root.context, DetailTransactionActivity::class.java)
                    intent.putExtra("id", transaction.id)
                    binding.root.context.startActivity(intent)
                }

                location.setOnClickListener {
                    val locationUri = if (transaction.coordinate != "-6.927314530264154, 107.77007155415649") {
                        "geo:0,0?q=${transaction.location}"
                    } else {
                        "geo:0,0?q=${transaction.coordinate}"
                    }
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUri))
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(binding.root.context, mapIntent, null)
                }
            }
        }
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    private var onDeleteClickListener: OnDeleteClickListener? = null

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = RowTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
