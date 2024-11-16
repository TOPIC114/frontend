package com.example.idiotchefassistant.resultBlock

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.idiotchefassistant.R
import com.example.idiotchefassistant.databinding.ResultItemBinding
import coil.load

class ResultItemAdapter(private var items: List<ResultItem>) :
    RecyclerView.Adapter<ResultItemAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(
        private val binding: ResultItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val editButton: ImageView = itemView.findViewById(R.id.edit_button)
        private val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    val imageUrls = item.image
                    val fragment = ImageDetailFragment.newInstance(imageUrls)
                    fragment.show(
                        (binding.root.context as AppCompatActivity).supportFragmentManager,
                        "imageDetail"
                    )
                }
            }
        }

        fun bind(item: ResultItem) {
            binding.itemTitle.text = item.name
            // binding image
            val imageUrl = "https://topic114.bntw.dev/${item.image.firstOrNull()}"
            binding.itemImage.load(imageUrl)
            Log.i("Result image", imageUrl)
            editButton.setOnClickListener {
                itemClickListener?.onEditClick(item)
            }
            deleteButton.setOnClickListener {
                itemClickListener?.onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    fun updateItems(newItems: List<ResultItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onEditClick(item: ResultItem)
        fun onDeleteClick(item: ResultItem)
    }
}