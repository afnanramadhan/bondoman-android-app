package com.example.android_hit.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.android_hit.DetailTransactionActivity
import com.example.android_hit.databinding.RowTransactionBinding
import com.example.android_hit.room.TransactionEntity

class TransactionAdapter(private val list: MutableList<TransactionEntity>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(private val binding: RowTransactionBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(transaction: TransactionEntity) {
            binding.apply {
                title.text = transaction.title
                amount.text = transaction.amount.toString()
                category.text = transaction.category
                location.text = transaction.location
                date.text = transaction.timestamp

                deleteButton.setOnClickListener {
                    val position = adapterPosition
                    onDeleteClickListener?.onDeleteClick(position)
                }

                editButton.setOnClickListener {
                    val intent = Intent(binding.root.context, DetailTransactionActivity::class.java)
                    intent.putExtra("id", transaction.id)
                    binding.root.context.startActivity(intent)
                }

                location.setOnClickListener {
                    val locationUri = "geo:0,0?q=${transaction.location}"
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
