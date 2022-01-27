
import com.krish.hms.helper.getModule
import com.krish.hms.model.Modules
import com.krish.hms.ui.UIHandler
import com.krish.hms.ui.ViewModel

fun main() {

    val uiHandler = UIHandler()
    val viewModel = ViewModel(uiHandler)

    uiHandler.writeData("Welcome to Hospital Management System")
    while(true){
        when(getModule(uiHandler.readOptions(Modules.values()))){
            Modules.ADDDOCTOR -> viewModel.addDoctor()

            Modules.ADDPATIENT -> viewModel.addPatient()

            Modules.HANDLECONSULTATION -> viewModel.handleConsultation()

            Modules.LISTDOCTORS -> viewModel.listDoctors()

            Modules.LISTPATIENTS -> viewModel.listPatients()

            Modules.LISTCASES -> viewModel.listCases()

            Modules.EXIT -> break
        }
    }
    uiHandler.writeData("Thank you")
}