package com.example.guidomia

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.guidomia.databinding.ItemCarCollapsedBinding

class CarExpandableListAdapter(
    private val context: Context,
    private val cars: List<Car>
) : RecyclerView.Adapter<CarExpandableListAdapter.CarViewHolder>() {

    inner class CarViewHolder(val binding: ItemCarCollapsedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarCollapsedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]
        val binding = holder.binding

        with(binding) {
            ivCarImage.setImageResource(when(car.make) {
                LAND_ROVER -> R.drawable.land_rover
                ALPINE -> R.drawable.alpine_roadster
                BMW -> R.drawable.bmw
                MERCEDES_BENZ -> R.drawable.mercedes_benz
                else -> R.drawable.tacoma
            })

            tvCarName.text = car.make
            tvCarPrice.text = "$LABEL_PRICE${formatAmount(car.marketPrice)}"

            displayStarRating(car.rating, starContainer)

            line.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE

            root.setOnClickListener {
                car.isExpanded = !car.isExpanded
                notifyItemChanged(position)
            }
        }

        if (car.isExpanded) {

        } else {

        }
    }

    override fun getItemCount(): Int = cars.size

    private fun displayStarRating(rating: Int, starsContainer: ViewGroup) {
        starsContainer.removeAllViews()

        val starDrawable = ContextCompat.getDrawable(context, R.drawable.star)

        val starLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        starLayoutParams.setMargins(0, 0, dpToPx(8), 0)

        for (i in 1..rating) {
            val starView = ImageView(context)
            starView.layoutParams = starLayoutParams
            starView.setImageDrawable(starDrawable)
            starsContainer.addView(starView)
        }
    }

    private fun dpToPx(dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun formatAmount(amount: Double): String {
        return when {
            amount >= 1000000 -> "${(amount / 1000000).toInt()}m"
            amount >= 1000 -> "${(amount / 1000).toInt()}k"
            else -> amount.toString()
        }
    }

    companion object {
        const val LAND_ROVER = "Land Rover"
        const val ALPINE = "Alpine"
        const val BMW = "BMW"
        const val MERCEDES_BENZ = "Mercedes Benz"

        const val LABEL_PRICE = "Price: "
    }
}