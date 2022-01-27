
package com.krish.hms.helper

import com.krish.hms.model.*
import com.krish.hms.ui.UIHandler
import java.io.File
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_YELLOW = "\u001B[33m"
const val ANSI_WHITE = "\u001B[37m"

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

fun String.toInt() = this.toIntOrNull() ?: -1

fun String.getDate(): LocalDate?{
    return try {
        LocalDate.parse(this, formatter)
    }
    catch (e: DateTimeParseException){
        null
    }
}

fun String.changeColor(logLevel: LogLevel = LogLevel.INFO): String{
    return when(logLevel){
        LogLevel.ERROR -> ANSI_RED + this + ANSI_RESET
        LogLevel.WARNING -> ANSI_YELLOW + this + ANSI_RESET
        LogLevel.INFO -> ANSI_WHITE + this + ANSI_RESET
    }
}

val Boolean.toInt : Int
    get() = if(this) 1 else 0

fun Int.getBoolean(): Boolean{
    return when(this){
        0 -> false
        1 -> true
        else -> false
    }
}

fun getGender(value: Int) : Gender {
    return when(value){
        0 -> Gender.MALE
        1 -> Gender.FEMALE
        2 -> Gender.OTHERS
        else -> Gender.OTHERS
    }
}

fun getMeridian(value: Int) : Meridian {
    return when(value){
        0 -> Meridian.AM
        1 -> Meridian.PM
        else -> Meridian.PM
    }
}

fun getDepartment(value: Int) : Department {
    return when(value){
        0 -> Department.DERMATOLOGY
        1 -> Department.ENT
        2 -> Department.OPHTHALMOLOGY
        3 -> Department.GENERAL
        else -> Department.GENERAL
    }
}

fun getBloodGroup(value: Int) : BloodGroup {
    return when(value){
        0 -> BloodGroup.APOSITIVE
        1 -> BloodGroup.ANEGATIVE
        2 -> BloodGroup.BPOSITIVE
        3 -> BloodGroup.BNEGATIVE
        4 -> BloodGroup.OPOSITIVE
        5 -> BloodGroup.ONEGATIVE
        6 -> BloodGroup.ABPOSITIVE
        7 -> BloodGroup.ABNEGATIVE
        else -> BloodGroup.OPOSITIVE
    }
}

fun getMedicineType(value: Int) : MedicineType{
    return when(value){
        0 -> MedicineType.TABLET
        1 -> MedicineType.DROPS
        2 -> MedicineType.SYRUP
        3 -> MedicineType.INHALER
        4 -> MedicineType.CREAM
        else -> MedicineType.TABLET
    }
}

fun isYes(value: String): Boolean = value.lowercase().replace(" ", "") == "yes"

fun getToday(): LocalDate = LocalDate.now()

fun generateId(holder: IdHolder): String{
    val prefix = when(holder){
        IdHolder.DOCTOR -> "DO"
        IdHolder.PATIENT -> "PA"
        IdHolder.CASE ->"CA"
        IdHolder.CONSULTATION ->"CO"
        IdHolder.MEDICINE -> "MD"
    }

    return prefix + UUID.randomUUID().toString().replace("-","")
}

fun getTime(hour: Int, minute: Int, meridian: Meridian): LocalTime?{
    try{
        if(meridian == Meridian.AM){
            if(hour == 12)
                return LocalTime.of(0, minute, 0)
            return LocalTime.of(hour, minute, 0)
        }
        else{
            if(hour == 12)
                return LocalTime.of(hour, minute, 0)
            return LocalTime.of(12 + hour, minute, 0)
        }
    }
    catch (e: DateTimeException){
        return null
    }
}

fun getDefaultTime(): LocalTime = LocalTime.of(12, 0, 0)

fun getModule(value: Int): Modules{
    return when(value){
        0 -> Modules.ADDDOCTOR
        1 -> Modules.ADDPATIENT
        2 -> Modules.HANDLECONSULTATION
        3 -> Modules.LISTDOCTORS
        4 -> Modules.LISTPATIENTS
        5 -> Modules.LISTCASES
        else -> Modules.EXIT
    }
}

fun getDoctorSelection(value: Int) : DoctorSelection{
    return when(value){
        0 -> DoctorSelection.ALL
        1 -> DoctorSelection.ID
        2 -> DoctorSelection.DEPARTMENT
        else -> DoctorSelection.ALL
    }
}

fun getPatientSelection(value: Int) : PatientSelection{
    return when(value){
        0 -> PatientSelection.ALL
        1 -> PatientSelection.ID
        2 -> PatientSelection.NAME
        else ->PatientSelection.ALL
    }
}

fun readFile(fileName: String) : List<String>{
    val file = "core/src/main/kotlin/com/krish/hms/data/$fileName"
    return File(file).readLines().drop(1)
}

fun writeFile(fileName: String, line: String){
    val file = "core/src/main/kotlin/com/krish/hms/data/$fileName"
    File(file).appendText(line)
}

fun readTime(field: String, uiHandler: UIHandler): LocalTime? {
    val hour = uiHandler.readData("$field hour").toInt()
    val minutes = uiHandler.readData("$field minutes").toInt()
    val meridian = getMeridian(uiHandler.readOptions(Meridian.values()))
    return getTime(hour, minutes, meridian)
}

