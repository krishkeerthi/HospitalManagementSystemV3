
package com.krish.hms.model

import com.krish.hms.helper.generateId
import com.krish.hms.helper.getDepartment
import com.krish.hms.helper.getToday
import com.krish.hms.helper.toInt
import java.time.LocalDate

data class Consultation(
    val consultationId: String,
    val caseId: String,
    val doctorId: String,
    val issue: String,
    val department: Department,
    val visitDate: LocalDate,
    var assessment: String =""
) {

    constructor(fields: List<String>):
            this(
                fields[0],
                fields[1],
                fields[2],
                fields[3],
                getDepartment(fields[4].toInt()),
                LocalDate.parse(fields[5]),
                fields[6])

    override fun toString(): String {
        return "$consultationId|$caseId|$doctorId|$issue|${department.ordinal}|$visitDate|$assessment\n"
    }

    companion object{
        fun createConsultation(
            caseId: String,
            doctorId: String,
            issue: String,
            department: Department
        ): Consultation{
            return Consultation(generateId(IdHolder.CONSULTATION), caseId, doctorId, issue, department, getToday())
        }
    }
}