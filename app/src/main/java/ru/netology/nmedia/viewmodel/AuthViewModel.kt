package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.AuthApi
import ru.netology.nmedia.auth.AppAuth
import java.io.IOException

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val auth = AppAuth.getInstance()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean>
        get() = _success

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?>
        get() = _error

    fun login(login: String, pass: String) {
        if (login.isBlank() || pass.isBlank()) {
            _error.value = "Введите логин и пароль"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = false

            try {
                val response = AuthApi.service.authenticate(
                    login = login.trim(),
                    pass = pass
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        auth.setAuth(body.id, body.token)
                        _success.value = true
                    } else {
                        _error.value = "Сервер вернул пустой ответ"
                    }
                } else {
                    _error.value = "Не удалось войти. Проверьте логин и пароль."
                }
            } catch (e: IOException) {
                _error.value = "Не удалось подключиться к серверу"
            } catch (e: Exception) {
                _error.value = "Ошибка авторизации: ${e.message ?: "неизвестная ошибка"}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun consumeSuccess() {
        _success.value = false
    }

    fun consumeError() {
        _error.value = null
    }
}