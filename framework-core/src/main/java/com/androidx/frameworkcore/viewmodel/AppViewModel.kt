package com.androidx.frameworkcore.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidx.frameworkcore.app.AndroidApplication
import com.androidx.frameworkcore.http.state.Resource
import com.androidx.frameworkcore.viewmodel.repository.AbsRepository
import kotlinx.coroutines.withContext
import kotlin.reflect.full.createInstance

internal class AppViewModel: ViewModel() {
    @Suppress("UNCHECKED_CAST")
    suspend fun <Params,Result> execute(repositoryName: String, params: Params?):LiveData<Resource<Result>>{
        return withContext(viewModelScope.coroutineContext) {
            val repository =
                AndroidApplication.mRepositoryHashMap[repositoryName]?.createInstance() as AbsRepository<*, Params, Result>
            repository.execute(params)
        }
    }

}