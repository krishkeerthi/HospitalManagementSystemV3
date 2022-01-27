
package com.krish.hms.data.repository

import com.krish.hms.helper.changeColor
import com.krish.hms.helper.isYes
import com.krish.hms.model.Case
import com.krish.hms.model.Consultation
import com.krish.hms.model.Department
import com.krish.hms.model.LogLevel
import com.krish.hms.ui.UIHandler
import java.time.LocalTime
import java.util.*

class HmsRepository(val uiHandler: UIHandler) {
    //All repositories
    val doctorRepository = DoctorRepository(uiHandler)
    val patientRepository = PatientRepository(uiHandler)
    val caseRepository = CaseRepository(uiHandler)
    val consultationRepository = ConsultationRepository(uiHandler)
    val medicineRepository = MedicineRepository(uiHandler)

    private val patientsCases = mutableMapOf<String, MutableList<String>>() // <patientId, ListOf<caseId>> patient's history of cases
    private val doctorsConsultations = mutableMapOf<String, MutableList<String>>() // <doctorId, ListOf<consultationId>> doctor's history of cases
    private val casesConsultations = mutableMapOf<String, MutableList<String>>() // caseId, ListOf<consultationId>> case's list of consultations
    private val doctorsPendingConsultations = mutableMapOf<String, Queue<String>>() // <doctorId, ListOf(consultationId)> doctor's current or pending consultations
    private val consultationsMedicines = mutableMapOf<String, MutableList<String>>() // <consultationId, ListOf(MedicationId) consultation's medications

    init {
        loadPatientsCases()
        loadConsultationsCasesDoctors()
        loadConsultationsMedicines()
    }

    private fun loadPatientsCases(){
        for(case in caseRepository.cases.values)
            addOrCreate(patientsCases, case.patientId, case.caseId)
    }

    private fun loadConsultationsMedicines(){
        for(medicine in medicineRepository.medicines.values)
            addOrCreate(consultationsMedicines, medicine.consultationId, medicine.medicineId)
    }

    private fun loadConsultationsCasesDoctors(){
        for(consultation in consultationRepository.consultations.values){
            addOrCreate(casesConsultations, consultation.caseId, consultation.consultationId)
            addOrCreate(doctorsConsultations, consultation.doctorId, consultation.consultationId)
        }
    }

    fun getPendingConsultations(doctorId: String): Int{
        return doctorsPendingConsultations[doctorId]?.size ?: 0
    }

    fun getConsultations(caseId: String) = casesConsultations[caseId]

    fun getMedicines(consultationId: String) = consultationsMedicines[consultationId]

    fun getFirstConsultation(doctorId: String): String? = doctorsPendingConsultations[doctorId]?.peek()

    fun removeConsultation(doctorId: String){
        doctorsPendingConsultations[doctorId]!!.remove()
    }

    fun manageConsultationsAndDoctors(doctorId: String, issue: String, caseId: String, department: Department, ssn: Int){
        //Create consultation
        val consultation = Consultation.createConsultation(caseId, doctorId, issue, department)
        consultationRepository.addConsultation(consultation)

        //Update case last visit date
        caseRepository.updateCaseLastVisit(caseId)

        //Update patient last visit date
        val patientId = patientRepository.getPatientId(ssn)
        patientRepository.updatePatientLastRegistered(patientId)

        addOrCreate(doctorsConsultations, doctorId, consultation.consultationId)
        addOrCreateQueue(doctorsPendingConsultations, doctorId, consultation.consultationId)
        addOrCreate(casesConsultations, caseId, consultation.consultationId)
    }

    fun generateCase(ssn: Int): String{
        val patientId = patientRepository.getPatientId(ssn)

        val case = Case.createCase(patientId)
        caseRepository.addCase(case)

        addOrCreate(patientsCases, patientId, case.caseId)
        return case.caseId
    }


    fun existingCase(caseId: String, ssn: Int): String {
        return if(caseRepository.isCaseIdExists(caseId))
            caseId
        else
            generateCase(ssn)
    }

    fun getCaseId(newCase: Boolean, ssn: Int): String{
        return if(newCase)
            generateCase(ssn)
        else{
            val id = uiHandler.readData("Case Id")
            existingCase(id, ssn)
        }
    }
    fun assignDoctor(ssn: Int, issue: String, time: LocalTime){

        val newCase = isYes(uiHandler.readData("yes or no for new case"))
        val caseId = getCaseId(newCase, ssn)

        val department = findDepartment(issue)
        val departmentDoctors = doctorRepository.getDepartmentDoctors(department)

        if(departmentDoctors.isEmpty())
            return uiHandler.writeData("No doctors available".changeColor(LogLevel.WARNING))
        else{
            var minNoOfConsultations = Int.MAX_VALUE
            var assignedDoctorId = ""

            for(doctor in departmentDoctors){
                val pendingConsultations = getPendingConsultations(doctor.doctorId)
                val consultationsHandlingTime = pendingConsultations * 15  // 15 minutes is considered as an average time for handling case
                val (consultationsHours, consultationsMinutes) = getHoursAndMinutes(consultationsHandlingTime)

                if(time.plusHours(consultationsHours.toLong()).plusMinutes(consultationsMinutes.toLong()).
                    isBefore(doctor.endTime.minusMinutes(14))){

                    if(pendingConsultations < minNoOfConsultations){
                        minNoOfConsultations = pendingConsultations
                        assignedDoctorId = doctor.doctorId
                    }
                }
            }

            if(assignedDoctorId != "")
                manageConsultationsAndDoctors(assignedDoctorId, issue, caseId, department, ssn)
            else
                uiHandler.writeData("No doctors available".changeColor(LogLevel.WARNING))
        }
    }

    private fun findDepartment(issue: String) : Department{
        issue.split(" ").forEach { word ->
            when(word){
                in mutableListOf("skin", "rashes", "spot") -> return Department.DERMATOLOGY.also { uiHandler.writeData(it.name)}
                in mutableListOf("eye", "vision", "sight") -> return Department.OPHTHALMOLOGY.also { uiHandler.writeData(it.name)}
                in mutableListOf("ear", "nose", "throat") -> return Department.ENT.also { uiHandler.writeData(it.name)}
            }
        }
        return Department.GENERAL.also { uiHandler.writeData(it.name)}
    }

    fun giveMedicines(doctorId: String){
        val consultationId = getFirstConsultation(doctorId)

        if(consultationId == null)
            return uiHandler.writeData("No consultations to handle".changeColor(LogLevel.WARNING))
        else{
            val assessment = uiHandler.readData("Assessment message")
            consultationRepository.addAssessment(consultationId, assessment)

            while(isYes(uiHandler.readData("Yes or No for medicine"))){
                val medicine = medicineRepository.readMedicine(consultationId)
                medicineRepository.addMedicine(medicine)

                addOrCreate(consultationsMedicines, consultationId, medicine.medicineId)
            }
            removeConsultation(doctorId)
        }

    }

    private fun getHoursAndMinutes(totalMinutes: Int): Pair<Int, Int>{
        val hours = totalMinutes/60
        val minutes = totalMinutes%60
        return Pair(hours, minutes)
    }


    private fun addOrCreate(map: MutableMap<String, MutableList<String>>, key: String, value: String){
        if(map.containsKey(key))
            map[key]?.add(value)
        else
            map[key] = mutableListOf(value)
    }

    private fun addOrCreateQueue(map: MutableMap<String, Queue<String>>, key: String, value: String){
        if(map.containsKey(key))
            map[key]?.add(value)
        else {
            map[key] = ArrayDeque()
            map[key]?.add(value)
        }
    }

}