package io.github.wulkanowy.utils

import android.os.Parcel
import android.os.Parcelable
import com.wdullaer.materialdatetimepicker.date.DateRangeLimiter
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import java.util.Calendar

@Suppress("UNUSED_PARAMETER")
class SchooldaysRangeLimiter : DateRangeLimiter {

    private val now = LocalDate.now()

    override fun setToNearestDate(day: Calendar): Calendar = day

    override fun isOutOfRange(year: Int, month: Int, day: Int): Boolean {
        val date = LocalDate.of(year, month + 1, day)
        val dayOfWeek = date.dayOfWeek
        return dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.SATURDAY || date.isHolidays
    }

    override fun getStartDate(): Calendar {
        val startYear = if (now.monthValue <= 6) now.year - 1 else now.year
        val startOfSchoolYear = now.withYear(startYear).firstSchoolDay

        val calendar = Calendar.getInstance()
        calendar.set(startOfSchoolYear.year, startOfSchoolYear.monthValue - 1, startOfSchoolYear.dayOfMonth)
        return calendar
    }

    override fun getEndDate(): Calendar {
        val endYear = if (now.monthValue > 6) now.year + 1 else now.year
        val endOfSchoolYear = now.withYear(endYear).lastSchoolDay

        val calendar = Calendar.getInstance()
        calendar.set(endOfSchoolYear.year, endOfSchoolYear.monthValue - 1, endOfSchoolYear.dayOfMonth)
        return calendar
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SchooldaysRangeLimiter> {

        override fun createFromParcel(parcel: Parcel): SchooldaysRangeLimiter = SchooldaysRangeLimiter()

        override fun newArray(size: Int): Array<SchooldaysRangeLimiter?> = arrayOfNulls(size)
    }
}
