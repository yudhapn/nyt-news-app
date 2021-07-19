package id.ypran.core.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import id.ypran.core.R

@BindingAdapter("setFavoriteIcon")
fun ImageView.setFavoriteIcon(isFavorite: Boolean) {
    val drawable = if (isFavorite) R.drawable.ic_favorite_red else R.drawable.ic_favorite_border
    setImageResource(drawable)
}

@BindingAdapter("setPublishDate")
fun TextView.setPublishDate(publishDate: String?) {
    text = publishDate?.take(10) ?: ""
}

@BindingAdapter("articleUrl")
fun ImageView.loadArticle(articleUrl: String?) {
    val imageUrl = "https://static01.nyt.com/$articleUrl"
    bindGlideSrc(imageUrl)
}

@BindingAdapter(
    "glideSrc",
    "glideCenterCrop",
    "glideCircularCrop",
    requireAll = false
)
fun ImageView.bindGlideSrc(
    drawableRes: String?,
    centerCrop: Boolean = false,
    circularCrop: Boolean = false
) {
    if (drawableRes == null || drawableRes.isEmpty()) {
        return
    }
    val imageUrl = "https://static01.nyt.com/$drawableRes"
    createGlideRequest(
        context,
        imageUrl,
        centerCrop,
        circularCrop
    ).into(this)
}

fun createGlideRequest(
    context: Context,
    drawableRes: String,
    centerCrop: Boolean,
    circularCrop: Boolean
): RequestBuilder<Drawable> {
//    val url = "https://image.tmdb.org/t/p/w154$drawableRes"
    val req = Glide.with(context).load(drawableRes)
    if (centerCrop) req.centerCrop()
    if (circularCrop) req.circleCrop()
    return req
}

