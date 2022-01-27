
package com.krish.hms.data.repository

import com.krish.hms.helper.*
import com.krish.hms.model.*
import com.krish.hms.ui.UIHandler

class DoctorRepository(val uiHandler: UIHandler) {
    private val doctors = mutableMapOf<String, Doctor>()

    init {
        loadDoctors()
    }

    private fun loadDoctors(){
        val doctorFile = readFile("Doctors")
        for(line in doctorFile){
            val doctor = Doctor(line.split('|'))
            doctors[doctor.doctorId] = doctor
        }
    }

    fun readDoctor(ssn: Int): Doctor {
        val name = uiHandler.readData("Name")
        val age = uiHandler.readData("Age").toInt()
        val gender = getGender(uiHandler.readOptions(Gender.values()))
        val dob = uiHandler.readData("Date of Birth(dd-mm-yyyy)").getDate() ?: getToday().
        also { uiHandler.writeData("Invalid Date entered, so Today's date is assigned".changeColor(LogLevel.ERROR))}

        val address = uiHandler.readData("Address")
        val contact = uiHandler.readData("Contact Number")
        val bloodGroup = getBloodGroup(uiHandler.readOptions(BloodGroup.values()))
        val doctorId = generateId(IdHolder.DOCTOR)
        val department = getDepartment(uiHandler.readOptions(Department.values()))
        val experience = uiHandler.readData("years of experience").toInt()

        val startTime = readTime("start", uiHandler) ?: getDefaultTime().
        also { uiHandler.writeData("Invalid time entered, so default time(12:00pm) is assigned".changeColor(LogLevel.ERROR)) }

        val endTime = readTime("end", uiHandler) ?: getDefaultTime().
        also { uiHandler.writeData("Invalid time entered, so default time(12:00pm) is assigned".changeColor(LogLevel.ERROR)) }

        return Doctor(name, age, gender, dob, address, contact, bloodGroup,
            ssn, doctorId, department, experience, startTime, endTime)
    }

    fun addDoctor(doctor: Doctor){

        doctors[doctor.doctorId] = doctor

        writeFile("Doctors", doctor.toString())
    }

    fun checkDoctorExistence(ssn: Int) : Boolean{
        return doctors.values.find { it.Ssn == ssn } != null
    }

    fun getDepartmentDoctors(department: Department): List<Doctor>{
        return doctors.values.filter { it.department == department }
    }

    fun getListOfDoctors(doctorSelection: DoctorSelection): List<Doctor>? {
        return when (doctorSelection) {
            DoctorSelection.ALL -> getAllDoctors()

            DoctorSelection.ID -> {
                val doctorId = uiHandler.readData("doctor id")
                getDoctorById(doctorId)
            }

            DoctorSelection.DEPARTMENT -> {
                val department = getDepartment(uiHandler.readOptions(Department.values()))
                getDoctorsByDepartment(department)
            }
        }
    }

    fun getDoctorById(id: String): List<Doctor>? {
        val doctor = doctors.values.find { it.doctorId == id }
        return if(doctor != null)
            listOf(doctor)
        else null
    }

    fun getDoctorsByDepartment(department: Department): List<Doctor>{
        return doctors.values.filter { it.department == department}
    }

    fun getAllDoctors() = doctors.values.distinct()

}