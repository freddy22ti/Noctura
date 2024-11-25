import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso
import com.uhuy.noctura.databinding.HomeSlideItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AutoSliderAdapter(
    private val images: List<String>,
    private val viewPager: ViewPager2
) : RecyclerView.Adapter<AutoSliderAdapter.SliderViewHolder>() {
    private var currentPosition = 0

    init {
        startAutoSlide()
    }

    // ViewHolder class using ViewBinding
    class SliderViewHolder(val binding: HomeSlideItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        // Inflate the binding
        val binding = HomeSlideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val imageUrl = images[position]
        // Use the binding to access the ImageView
        Picasso.get().load(imageUrl).into(holder.binding.slideImage)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    private fun startAutoSlide() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(3000)
                currentPosition = (currentPosition + 1) % itemCount
                viewPager.setCurrentItem(currentPosition, true)
            }
        }
    }
}
