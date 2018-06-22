package org.bobstuff.parallaxscroll

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {
    val items = mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    lateinit var adapter: CustomAdapter
    lateinit var parallaxScrollView: ParallaxScrollView
    lateinit var recyclerView: RecyclerView
    var listWidth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = ""

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        listWidth = resources.getDimensionPixelSize(R.dimen.item_list_width)
        val decorationWidth = resources.getDimensionPixelSize(R.dimen.item_list_decoration)

        parallaxScrollView = findViewById(R.id.parallax_scroll_view)
        val target: Target<Bitmap> = object : BaseTarget<Bitmap>() {
            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                parallaxScrollView.setBackgroundImage(bitmap)
            }

            override fun getSize(cb: SizeReadyCallback) {
                cb.onSizeReady(SIZE_ORIGINAL, SIZE_ORIGINAL)
            }

            override fun removeCallback(cb: SizeReadyCallback) {}
        }
        parallaxScrollView.setRecyclerViewSizes(listWidth, decorationWidth)
        parallaxScrollView.maxScaleDifference = 0.4f

        parallaxScrollView.post({
            //do this after everything is laid out
            GlideApp.with(this.applicationContext)
                    .asBitmap()
                    .load(Uri.parse(resources.getString(R.string.background_image)))
                    .signature(ObjectKey("${this.resources.configuration.orientation}"))
                    .transforms(GlideScaleImage(parallaxScrollView))
                    .into(target)
        })

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false)
        adapter = CustomAdapter()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SimpleItemDecoration(decorationWidth))

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> adapter.addItem()
            R.id.action_remove -> adapter.removeItem()
        }
        return super.onOptionsItemSelected(item)
    }

    class GlideScaleImage(val container: View) : BitmapTransformation() {
        var w: Int = 0

        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int,
                               outHeight: Int): Bitmap {
            val viewWidth = container.width
            val viewHeight = container.height

            val aspectRatio = outWidth.toFloat() / outHeight

            val newWidth = Math.max(Math.ceil((viewHeight * aspectRatio).toDouble()).toInt(), viewWidth)
            val newHeight = (newWidth * aspectRatio).toInt()

            return TransformationUtils.fitCenter(pool, toTransform, newWidth, newHeight)
        }

        override fun equals(o: Any?): Boolean {
            return o is GlideScaleImage
        }

        override fun hashCode(): Int {
            return ID.hashCode()
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            val key = "$ID-$w"
            messageDigest.update(key.toByteArray(Key.CHARSET))
        }

        companion object {
            const val ID = "custom-glide-simple-scale-image"
        }
    }

    inner class CustomAdapter: RecyclerView.Adapter<CustomViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.recycler_item, parent, false)
            return CustomViewHolder(view)
        }

        fun addItem() {
            val llm = recyclerView.getLayoutManager() as LinearLayoutManager
            val firstIndex = llm.findFirstCompletelyVisibleItemPosition()
            items.add(firstIndex, "+")
            notifyItemInserted(firstIndex)
            recyclerView.layoutManager.smoothScrollToPosition(recyclerView, null, firstIndex)
        }

        fun removeItem() {
            if (items.size == 1) {
                return
            }
            val llm = recyclerView.layoutManager as LinearLayoutManager
            val firstIndex = llm.findFirstCompletelyVisibleItemPosition()
            items.removeAt(firstIndex)
            notifyItemRemoved(firstIndex)
            recyclerView.smoothScrollBy(1,0)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val digit = items[position]
            holder.textView.text = digit
        }
    }

    inner class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view)
    }
}
