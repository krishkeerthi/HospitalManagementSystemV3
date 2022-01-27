
package com.krish.hms.ui

import com.krish.hms.data.repository.HmsRepository
import com.krish.hms.helper.*
import com.krish.hms.model.DoctorSelection
import com.krish.hms.model.LogLevel
import com.krish.hms.model.PatientSelection

class ViewModel(val uiHandler: UIHandler) : Modules {

    val hmsRepository = HmsRepository(uiHandler)

    override fun addDoctor() {
        val ssn = uiHandler.readData("SSN").toInt()

        if(!hmsRepository.doctorRepository.checkDoctorExistence(ssn)){
            val doctor= hmsRepository.doctorRepository.readDoctor(ssn)
            hmsRepository.doctorRepository.addDoctor(doctor)
            uiHandler.writeData("Doctor registered successfully")
        }
        else
            uiHandler.writeData("Doctor is already working here".changeColor(LogLevel.WARNING))
    }

    override fun addPatient() {
        val ssn = uiHandler.readData("SSN").toInt()

        if(!hmsRepository.patientRepository.checkPatientExistence(ssn)){
            val patient= hmsRepository.patientRepository.readPatient(ssn)
            hmsRepository.patientRepository.addPatient(patient)
            uiHandler.writeData("Patient registered successfully")
        }

        val inTime = readTime("patient in", uiHandler) ?: getDefaultTime().
        also { uiHandler.writeData("Invalid time entered, so default time(12:00pm) is assigned".changeColor(LogLevel.ERROR)) }

        val issue = uiHandler.readData("issue")

        hmsRepository.assignDoctor(ssn, issue, inTime)

    }

    override fun handleConsultation() {
        val doctorId = uiHandler.readData("Doctor Id")

        hmsRepository.giveMedicines(doctorId)
    }

    override fun listDoctors() {
        val doctorSelection = getDoctorSelection(uiHandler.readOptions(DoctorSelection.values()))

        val doctors = hmsRepository.doctorRepository.getListOfDoctors(doctorSelection)

        if(doctors != null && doctors.isNotEmpty()){
            uiHandler.writeData("Name  | Age | Gender | Department | years of Experience  | Avail time start  | Avail time end")
            for(doctor in doctors)
                uiHandler.writeData("${doctor.name} ${doctor.age}  ${doctor.gender.name.lowercase()} ${doctor.department.name.lowercase()} " +
                        "${doctor.yearsOfExperience} ${doctor.startTime}  ${doctor.endTime}")
        }
        else
            uiHandler.writeData("No data found".changeColor(LogLevel.ERROR))
    }

    override fun listPatients() {
        val patientSelection = getPatientSelection(uiHandler.readOptions(PatientSelection.values()))

        val patients = hmsRepository.patientRepository.getListOfPatients(patientSelection)

        if(patients != null && patients.isNotEmpty()){
            uiHandler.writeData("Name   |   Age   |  Gender  | First visit  | Last visit")
            for(patient in patients)
                uiHandler.writeData("${patient.name} ${patient.age} ${patient.gender.name.lowercase()}" +
                        "  ${patient.firstRegistered}  ${patient.lastRegistered}")
        }
        else
            uiHandler.writeData("No data found".changeColor(LogLevel.ERROR))
    }

    override fun listCases() {
        val caseId = uiHandler.readData("Case id")

        val case = hmsRepository.caseRepository.getCase(caseId)
        if(case == null)
            return uiHandler.writeData("Case does not exist".changeColor(LogLevel.ERROR))
        else{
            uiHandler.writeData("Case id   |  Patient id  |  First visit  |  Last Visit")
            uiHandler.writeData("${case.caseId}  ${case.patientId}  ${case.firstVisit}  ${case.lastVisit}")
        }

        val consultations = hmsRepository.getConsultations(caseId)
        if(consultations == null)
            return uiHandler.writeData("No consultations available".changeColor(LogLevel.ERROR))
        else{
            for(consultationId in consultations){
                val consultation = hmsRepository.consultationRepository.getConsultation(consultationId)
                if(consultation != null){
                    uiHandler.writeData("Consultation id   |  Doctor id   |  Department   | Issue   |  Visit date |  Assessment")
                    uiHandler.writeData("${consultation.consultationId} ${consultation.doctorId} ${consultation.department.name.lowercase()} " +
                            "${consultation.issue} ${consultation.visitDate}  ${consultation.assessment}")

                    val medicines = hmsRepository.getMedicines(consultationId)
                    if(medicines != null){
                        uiHandler.writeData("Medicine name  |  Medicine type  |  count   | days  | morning | Afternoon | Evening")
                        for(medicineId in medicines){
                            val medicine = hmsRepository.medicineRepository.getMedicine(medicineId)

                            if(medicine != null)
                                uiHandler.writeData("${medicine.medicineName}  ${medicine.medicineType.name.lowercase()} " +
                                        "${medicine.count} ${medicine.days} ${medicine.morning}  ${medicine.afternoon}  ${medicine.night}")
                        }
                    }
                    else
                        uiHandler.writeData("No medicine available for this consultation".changeColor(LogLevel.WARNING))
                }
            }
        }
    }
}