
package com.krish.hms.data.repository

import com.krish.hms.helper.*
import com.krish.hms.model.*
import com.krish.hms.ui.UIHandler
import java.time.LocalDate

class PatientRepository(val uiHandler: UIHandler) {
    private val patients = mutableMapOf<String, Patient>()

    init {
        loadPatients()
    }

    private fun loadPatients(){
        val patientFile = readFile("Patients")
        for(line in patientFile){
            val patient = Patient(line.split('|'))
            patients[patient.patientId] = patient
        }
    }

    fun readPatient(ssn: Int): Patient {
        val name = uiHandler.readData("Name")
        val age = uiHandler.readData("Age").toInt()
        val gender = getGender(uiHandler.readOptions(Gender.values()))
        val dob = uiHandler.readData("Date of Birth(dd-mm-yyyy)").getDate() ?: getToday().
        also { uiHandler.writeData("Invalid Date entered, so Today's date is assigned")}

        val address = uiHandler.readData("Address")
        val contact = uiHandler.readData("Contact Number")
        val bloodGroup = getBloodGroup(uiHandler.readOptions(BloodGroup.values()))
        val patientId = generateId(IdHolder.PATIENT)

        return Patient(name, age, gender, dob, address, contact, bloodGroup,
            ssn, patientId , getToday(), getToday())
    }

    fun addPatient(patient: Patient){

        patients[patient.patientId] = patient

        writeFile("Patients", patient.toString())
    }

    fun checkPatientExistence(ssn: Int) : Boolean{
        return patients.values.find { it.Ssn == ssn } != null
    }

    fun getPatientId(ssn: Int): String{
        return patients.values.find { it.Ssn == ssn }!!.patientId
    }

    fun isPatientIdExists(caseId: String) = patients.containsKey(caseId)

    fun updatePatientLastRegistered(patientId: String){
        if(isPatientIdExists(patientId))
            patients[patientId]!!.lastRegistered = getToday()
        else
            uiHandler.writeData("Patient does not exist")
    }

     fun getListOfPatients(patientSelection: PatientSelection): List<Patient>? {
         return when (patientSelection) {
             PatientSelection.ALL -> getAllPatients()

             PatientSelection.ID -> {
                 val patientId = uiHandler.readData("patient id")
                 getPatientById(patientId)
             }
             PatientSelection.NAME -> {
                 val name = uiHandler.readData("name")
                 getPatientsByName(name)
             }
         }
     }

    fun getPatientsByName(name: String): List<Patient>{
        return patients.values.filter { it.name.lowercase() == name.lowercase()}
    }

    fun getAllPatients() = patients.values.distinct()

    fun getPatientById(id: String): List<Patient>? {
        val patient = patients.values.find { it.patientId == id }
        return if(patient != null)
            listOf(patient)
        else null
    }
}