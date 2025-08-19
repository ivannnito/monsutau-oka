package edu.chapman.monsutauoka.ui.third

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GammaViewModel() : ViewModel() {

    private val io: ExecutorService = Executors.newSingleThreadExecutor()

    private val _text = MutableLiveData("")
    val text: LiveData<String> = _text

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private var currentSha: String? = null

    // ⚠️⚠️⚠️ NEVER CHECK IN A SECRET!!! ⚠️⚠️⚠️
    private val githubToken = "lol"
    private val owner = "tdupont750"
    private val repo = "monsutau-save"
    private val path = "pika.txt"
    private val branch: String? = "main"

    private val service = GithubService(githubToken, owner, repo, path, branch)

    fun load() {
        _busy.postValue(true)
        _error.postValue(null)
        io.execute {
            try {
                val res = service.loadFile()
                currentSha = res.sha
                _text.postValue(res.text)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Load failed")
            } finally {
                _busy.postValue(false)
            }
        }
    }

    fun save(currentText: String) {
        _busy.postValue(true)
        _error.postValue(null)
        io.execute {
            try {
                val newSha = service.saveFile(currentText, currentSha)
                currentSha = newSha
                _text.postValue(currentText)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Save failed")
            } finally {
                _busy.postValue(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        io.shutdownNow()
    }
}