package id.ypran.nytnews.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ypran.core.article.domain.model.Multimedia
import id.ypran.core.article.domain.model.MultimediaDiffCallback
import id.ypran.nytnews.databinding.ItemsArticleImageLayoutBinding
import id.ypran.nytnews.ui.ImagePagerAdapter.ViewHolder
import id.ypran.nytnews.ui.ImagePagerAdapter.ViewHolder.Companion.from

class ImagePagerAdapter : ListAdapter<Multimedia, ViewHolder>(MultimediaDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class ViewHolder(private val binding: ItemsArticleImageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(multimedia: Multimedia) {
            binding.multimedia = multimedia
        }

        companion object {
            fun from(parent: ViewGroup) =
                ViewHolder(
                    ItemsArticleImageLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }
    }
}