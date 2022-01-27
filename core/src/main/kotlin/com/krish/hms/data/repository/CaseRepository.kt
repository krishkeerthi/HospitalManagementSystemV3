
package com.krish.hms.data.repository

import com.krish.hms.helper.changeColor
import com.krish.hms.helper.getToday
import com.krish.hms.helper.readFile
import com.krish.hms.helper.writeFile
import com.krish.hms.model.Case
import com.krish.hms.model.LogLevel
import com.krish.hms.ui.UIHandler

class CaseRepository(val uiHandler: UIHandler) {
    val cases = mutableMapOf<String, Case>()

    init {
        loadCases()
    }

    private fun loadCases(){
        val caseFile = readFile("Cases")
        for(line in caseFile){
            val case = Case(line.split('|'))
            cases[case.caseId] = case
        }
    }

    fun addCase(case: Case){
        cases[case.caseId] = case
        writeFile("Cases", case.toString())
    }

    fun isCaseIdExists(caseId: String) = cases.containsKey(caseId)

    fun getCase(caseId: String) = cases[caseId]

    fun updateCaseLastVisit(caseId: String){
        if(isCaseIdExists(caseId))
            cases[caseId]!!.lastVisit = getToday()
        else
            uiHandler.writeData("Case does not exist".changeColor(LogLevel.ERROR))
    }

}