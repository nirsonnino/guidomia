package com.example.guidomia

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.guidomia.databinding.ItemCarCollapsedBinding
import com.example.guidomia.databinding.ItemCustomBulletListBinding

class CarExpandableListAdapter(
    private val context: Context,
    private val cars: List<Car>,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<CarExpandableListAdapter.CarViewHolder>(), Filterable {

    private var expandedPosition: Int = 0

    private var filteredCars: List<Car> = cars.toList()

    inner class CarViewHolder(val binding: ItemCarCollapsedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarCollapsedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = filteredCars[position]
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

            val isExpanded = position == expandedPosition
            expandedContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

            root.setOnClickListener {
                val oldExpandedPosition = expandedPosition
                expandedPosition = if (isExpanded) RecyclerView.NO_POSITION else holder.adapterPosition

                // Notify item changes for old and new expanded positions
                notifyItemChanged(oldExpandedPosition)
                notifyItemChanged(expandedPosition)

                // Scroll to the expanded item if it's partially or fully outside the visible area
                if (!isExpanded) {
                    recyclerView.smoothScrollToPosition(holder.adapterPosition)
                }
            }

            if (isExpanded) {
                displayProsCons(prosContainer, car.prosList)
                displayProsCons(consContainer, car.consList)
            }

            line.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE
        }
    }

    override fun getItemCount(): Int = filteredCars.size

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

    private fun displayProsCons(layout: LinearLayout, items: List<String>) {
        layout.removeAllViews()
        val bulletColor = ContextCompat.getColor(context, R.color.orange)
        for (item in items) {
            val itemBinding = ItemCustomBulletListBinding.inflate(LayoutInflater.from(context), layout, false)
            with(itemBinding) {
                if (item.isEmpty().not()) {
                    tvDesc.text = item
                    ivBullet.setColorFilter(bulletColor)
                    layout.addView(root)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint.isNullOrEmpty()) {
                    results.values = cars.toList()
                } else {
                    val filteredList = cars.filter { car ->
                        car.make.equals(constraint.toString(), ignoreCase = true) ||
                                car.model.equals(constraint.toString(), ignoreCase = true)
                    }
                    results.values = filteredList.takeIf { it.isNotEmpty() } ?: cars.toList()
                }
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredCars = results?.values as? List<Car> ?: emptyList()
                notifyDataSetChanged()
            }
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
