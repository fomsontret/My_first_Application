package ru.netology.media.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


class SingleLiveEvent<T> : MutableLiveData<T>() {
    // FIXME: упрощённый вердикт, пока не понятно Atomic's
    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        require(!hasActiveObservers()) {
            error("Multiple observers registered but only one will be notified of change")
        }

        super.observe(owner) { it ->
            if (pending) {
                pending = false
                observer.onChanged(it)
            }
        }
    }

    override fun setValue(t: T?) {
        pending = true
        super.setValue(t)
    }
}