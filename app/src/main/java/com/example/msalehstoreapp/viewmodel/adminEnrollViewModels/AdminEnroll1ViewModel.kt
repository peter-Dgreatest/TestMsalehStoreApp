package com.example.msalehstoreapp.viewmodel.adminEnrollViewModels

import android.app.Application
import android.opengl.Visibility
import android.view.View
import androidx.lifecycle.*
import com.example.msalehstoreapp.database.dao.BranchDAO
import com.example.msalehstoreapp.database.entities.BranchDB
import com.example.msalehstoreapp.repository.EnrollRepository
import kotlinx.coroutines.*

class AdminEnroll1ViewModel(
    val database: BranchDAO,
    application: Application
) : ViewModel() {

    val _navigateToNextFragment = MutableLiveData<Boolean>()

    private val _branches = MutableLiveData<Array<BranchDB>>()
    val branches: LiveData<Array<BranchDB>>
        get()= _branches
    //get() = _branches.map { it.  }

    val branchList = Transformations.map(branches,{
        time -> time.map { it.branch_name }
    })


    var enrollRepository : EnrollRepository




    val jobDescription = listOf<String>("branch_manager","chiefadmin","supervisor","dispatcher")
    val designation = listOf<String>("branch manager","chiefadmin","supervisor")

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob+ Dispatchers.Main)

  //  val toShowJobDesc = MutableLiveData<Int>()

    init {
        _navigateToNextFragment.value=false
        enrollRepository = EnrollRepository(database,application)
        viewModelScope.launch {

            _branches.value = enrollRepository.getBranches()
            //enrollRepository=job
            branchList.value?.forEach { println() }
        }
    }

//    fun toggleShowJob() {
//        var input ="supervisor"//:String
//        if(input=="supervisor")
//        toShowJobDesc.value =
//            if (toShowJobDesc.value!!.equals(View.VISIBLE))
//                 View.GONE
//            else
//                View.VISIBLE
//    }

    fun navigateToNextFragment(){
        _navigateToNextFragment.value=true;
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}