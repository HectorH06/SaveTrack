package com.example.st5

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import coil.load
import java.util.*

internal class finanzasPRAdapter(
	var context: Context,
	private var images: List<String>
) :
	PagerAdapter() {
	var mLayoutInflater: LayoutInflater

	init {
		images = images
		mLayoutInflater =
			context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
	}

	override fun getCount(): Int {
		return images.size
	}

	override fun isViewFromObject(view: View, `object`: Any): Boolean {
		return view === `object` as LinearLayout
	}

	override fun instantiateItem(container: ViewGroup, position: Int): Any {
		val itemView: View = mLayoutInflater.inflate(R.layout.item_producto, container, false)

		val imageView = itemView.findViewById<View>(R.id.recomendado) as ImageView

		imageView.load(images[position])

		Objects.requireNonNull(container).addView(itemView)
		return itemView
	}

	override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
		container.removeView(`object` as LinearLayout)
	}
}
