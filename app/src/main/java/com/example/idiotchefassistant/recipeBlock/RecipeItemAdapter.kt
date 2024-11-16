package com.example.idiotchefassistant.recipeBlock

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.idiotchefassistant.R
import com.example.idiotchefassistant.databinding.RecipeItemListBinding

class RecipeItemAdapter(private var items: List<RecipeItem>) :
    RecyclerView.Adapter<RecipeItemAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(private val binding: RecipeItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val item = items[position]
                    itemClickListener?.onItemClick(item)
                }
            }
        }

        fun bind(item: RecipeItem) {
            binding.itemTitle.text = item.title
            binding.itemReview.text = item.score.toString()
            val videoId = item.link?.let {
                val regex = "https?://(?:www\\.)?youtube\\.com/watch\\?v=([\\w-]+)".toRegex()
                val matchResult = regex.find(it)
                matchResult?.groups?.get(1)?.value
            }
            Log.i("video id",videoId.toString())
            var videoUrl = ""
            if (videoId != null) {
                Log.i("video id", "${item.title}: ${videoId}")
                videoUrl = "https://img.youtube.com/vi/${videoId}/hqdefault.jpg"
            } else {
                Log.e("video id", "無法從連結中取得 videoId")
            }
            binding.RecipeImage.load(videoUrl)
            videoUrl.let {
                binding.RecipeImage.load(it) {
                    placeholder(R.drawable.downloading_24)  // 可選的佔位符
                    error(R.drawable.broken_image_24)       // 可選的錯誤圖片
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecipeItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }

    fun updateItems(newItems: List<RecipeItem>){
        items = newItems
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClick(item: RecipeItem)
    }
}