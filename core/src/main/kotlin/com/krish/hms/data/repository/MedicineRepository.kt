
package com.krish.hms.data.repository

import com.krish.hms.helper.*
import com.krish.hms.model.IdHolder
import com.krish.hms.model.Medicine
import com.krish.hms.model.MedicineType
import com.krish.hms.ui.UIHandler
import kotlin.text.toInt

class MedicineRepository(val uiHandler: UIHandler) {
    val medicines = mutableMapOf<String, Medicine>()

    init {
        loadMedicines()
    }

    private fun loadMedicines(){
        val medicineFile = readFile("Medicines")
        for(line in medicineFile){
            val medicine = Medicine(line.split("|"))
            medicines[medicine.medicineId] = medicine
        }
    }

    fun readMedicine(consultationId: String): Medicine{
        val medicineId = generateId(IdHolder.MEDICINE)
        val name = uiHandler.readData("Medicine Name")
        val type = getMedicineType(uiHandler.readOptions(MedicineType.values()))
        val count = uiHandler.readData("Count of usage").toInt()
        val days = uiHandler.readData("No of days to continue").toInt()
        val morning = isYes(uiHandler.readData("Yes or No for morning"))
        val afternoon = isYes(uiHandler.readData("Yes or No for afternoon"))
        val night = isYes(uiHandler.readData("Yes or No for night"))

        return Medicine(medicineId, consultationId, name, type, count, days, morning, afternoon, night)
    }

    fun getMedicine(medicineId: String) = medicines[medicineId]

    fun addMedicine(medicine: Medicine){

        medicines[medicine.medicineId] = medicine

        writeFile("Medicines", medicine.toString())
    }
}