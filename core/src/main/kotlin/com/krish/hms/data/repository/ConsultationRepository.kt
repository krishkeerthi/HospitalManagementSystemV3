
package com.krish.hms.data.repository

import com.krish.hms.helper.changeColor
import com.krish.hms.helper.readFile
import com.krish.hms.helper.writeFile
import com.krish.hms.model.Consultation
import com.krish.hms.model.LogLevel
import com.krish.hms.ui.UIHandler

class ConsultationRepository(val uiHandler: UIHandler) {
    val consultations = mutableMapOf<String, Consultation>()

    init {
        loadConsultations()
    }

    private fun loadConsultations(){
        val consultationFile = readFile("Consultations")
        for(line in consultationFile){
            val consultation = Consultation(line.split('|'))
            consultations[consultation.consultationId] = consultation
        }
    }

    fun addConsultation(consultation: Consultation){
        consultations[consultation.consultationId] = consultation
    }

    fun getConsultation(consultationId: String) = consultations[consultationId]

    fun addAssessment(consultationId: String, assessment: String){
        val consultation = consultations[consultationId] ?:
        return uiHandler.writeData("Consultation id does not exists".changeColor(LogLevel.ERROR))
        consultation.assessment = assessment

        writeFile("Consultations", consultation.toString())
    }

}