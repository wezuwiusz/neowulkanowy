package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.wulkanowy.data.enums.Gender
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "StudentInfo")
data class StudentInfo(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "second_name")
    val secondName: String,

    val surname: String,

    @ColumnInfo(name = "birth_date")
    val birthDate: LocalDate,

    @ColumnInfo(name = "birth_place")
    val birthPlace: String,

    val gender: Gender,

    @ColumnInfo(name = "has_polish_citizenship")
    val hasPolishCitizenship: Boolean,

    @ColumnInfo(name = "family_name")
    val familyName: String,

    @ColumnInfo(name = "parents_names")
    val parentsNames: String,

    val address: String,

    @ColumnInfo(name = "registered_address")
    val registeredAddress: String,

    @ColumnInfo(name = "correspondence_address")
    val correspondenceAddress: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "cell_phone_number")
    val cellPhoneNumber: String,

    val email: String,

    @Embedded(prefix = "first_guardian_")
    val firstGuardian: StudentGuardian,

    @Embedded(prefix = "second_guardian_")
    val secondGuardian: StudentGuardian

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

data class StudentGuardian(

    @ColumnInfo(name = "full_name")
    val fullName: String,

    val kinship: String,

    val address: String,

    val phones: String,

    val email: String
) : Serializable
