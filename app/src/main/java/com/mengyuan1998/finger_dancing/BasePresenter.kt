package com.mengyuan1998.finger_dancing

abstract class BasePresenter<V : com.mengyuan1998.finger_dancing.BaseView>(open val view: V) {

    abstract fun create()

    abstract fun start()

    abstract fun stop()

}