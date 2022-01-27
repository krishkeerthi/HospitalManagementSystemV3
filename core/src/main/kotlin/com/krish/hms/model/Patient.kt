
package com.krish.hms.model

import com.krish.hms.helper.*
import java.time.LocalDate

class Patient(
    name: String,
    age: Int,
    gender: Gender,
    dob: LocalDate,
    address: String,
    contact: String,
    bloodGroup: BloodGroup,
    Ssn: Int,
    val patientId: String,
    val firstRegistered: LocalDate,
    var lastRegistered: LocalDate
    ) : Person(name, age, gender, dob, address, contact, bloodGroup, Ssn){

    constructor(fields: List<String>) :
            this(
                fields[1],
                fields[2].toInt(),
                getGender(fields[3].toInt()),
                LocalDate.parse(fields[4]),
                fields[5],
                fields[6],
                getBloodGroup(fields[7].toInt()),
                fields[8].toInt(),
                fields[0],
                LocalDate.parse(fields[9]),
                LocalDate.parse(fields[10]))

    override fun toString() = "$patientId|$name|$age|${gender.ordinal}|$dob|$address|$contact|${bloodGroup.ordinal}|" +
            "$Ssn|$firstRegistered|$lastRegistered\n"

}