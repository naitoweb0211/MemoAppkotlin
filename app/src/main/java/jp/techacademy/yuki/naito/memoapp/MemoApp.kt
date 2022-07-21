package jp.techacademy.yuki.naito.memoapp

import android.app.Application
import io.realm.Realm

class MemoApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}