import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testversion.R
import com.example.testversion.database.BookingDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class PastBookingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_past_booking, container, false)
        val noBookingsText = view.findViewById<TextView>(R.id.noBookingsText)

        lifecycleScope.launch {
            val hasPastBookings = withContext(Dispatchers.IO) {
                val database = BookingDatabase.getInstance(requireContext())
                database.bookingDao().getPastBookings(LocalDate.now()).isNotEmpty()
            }

            if (hasPastBookings) {
                noBookingsText.visibility = View.GONE
            } else {
                noBookingsText.visibility = View.VISIBLE
            }
        }

        return view
    }
}