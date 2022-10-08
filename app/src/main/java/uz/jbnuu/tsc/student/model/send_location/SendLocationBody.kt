package uz.jbnuu.tsc.student.model.send_location

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class SendLocationBody(
    @PrimaryKey
    var data_time: String = "",
    @SerializedName("lat") var latitude: String = "",
    @SerializedName("long") var longitude: String = "",
)
