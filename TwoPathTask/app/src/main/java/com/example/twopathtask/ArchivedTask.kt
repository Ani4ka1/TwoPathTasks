package com.example.twopathtask

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "archived_tasks")
data class ArchivedTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val date: Date,
    val isDaily: Boolean,
    val isWeekly: Boolean,
    val accountId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Date(parcel.readLong()),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(category)
        parcel.writeLong(date.time)
        parcel.writeByte(if (isDaily) 1 else 0)
        parcel.writeByte(if (isWeekly) 1 else 0)
        parcel.writeInt(accountId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArchivedTask> {
        override fun createFromParcel(parcel: Parcel): ArchivedTask {
            return ArchivedTask(parcel)
        }

        override fun newArray(size: Int): Array<ArchivedTask?> {
            return arrayOfNulls(size)
        }
    }
}
