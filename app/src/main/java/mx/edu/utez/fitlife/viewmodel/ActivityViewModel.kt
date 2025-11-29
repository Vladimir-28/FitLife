package mx.edu.utez.fitlife.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import mx.edu.utez.fitlife.data.model.ActivityDay

class ActivityViewModel : ViewModel() {

    val weeklyActivity = MutableStateFlow(
        listOf(
            ActivityDay("Lun", 5200, 3.4f, "45m"),
            ActivityDay("Mar", 7600, 5.1f, "1h 10m"),
            ActivityDay("Mié", 3200, 2.1f, "30m"),
            ActivityDay("Jue", 8900, 6.4f, "1h 25m"),
            ActivityDay("Vie", 10400, 8.0f, "1h 55m"),
            ActivityDay("Sáb", 6500, 4.8f, "50m"),
            ActivityDay("Dom", 4000, 2.9f, "35m")
        )
    )
}
