package org.hyperskill.phrases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(data: List<Phrase>, private val delete: String, private val database: AppDatabase) : RecyclerView.Adapter<RecyclerAdapter.PhrasesViewHolder>() {

    private var data: List<Phrase> = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }



    fun updateData(list: List<Phrase>) {
        data = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhrasesViewHolder {
        val holder = PhrasesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_layout, parent, false))

        holder.delete.setOnClickListener {
            val position = holder.layoutPosition
            database.getPhraseDao().delete(data[position])  // Remove the item from the list
            updateData(database.getPhraseDao().getAll())
            notifyItemRemoved(position) // Notify the adapter that the item is removed
        }

        return holder
    }

    override fun onBindViewHolder(holder: PhrasesViewHolder, position: Int) {
        val phrase = data[position]

        holder.quote.text = phrase.name
        holder.delete.text =  delete
    }

    override fun getItemCount() = data.size


    class PhrasesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quote: TextView = view.findViewById(R.id.phraseTextView)
        val delete: TextView = view.findViewById(R.id.deleteTextView)
    }
}

