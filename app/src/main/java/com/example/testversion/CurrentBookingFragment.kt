import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testversion.R
import com.example.testversion.adapters.FinalizedBookingAdapter
import com.example.testversion.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class CurrentBookingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_current_booking, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val noBookingsText = view.findViewById<TextView>(R.id.noBookingsText)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val userEmail = requireActivity().intent?.getStringExtra("userEmail") ?: return@launch

            val currentBookings = withContext(Dispatchers.IO) {
                val database = AppDatabase.getInstance(requireContext())
                database.finalizedBookingDao().getCurrentBookings(userEmail, LocalDate.now())
            }

            if (currentBookings.isNotEmpty()) {
                noBookingsText.visibility = View.GONE
                recyclerView.adapter = FinalizedBookingAdapter(currentBookings)
            } else {
                noBookingsText.visibility = View.VISIBLE
            }
        }

        return view
    }
}