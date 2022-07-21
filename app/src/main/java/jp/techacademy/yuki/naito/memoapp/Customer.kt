package jp.techacademy.yuki.naito.memoapp

import java.io.Serializable
import java.util.Date
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

public open class Customer: RealmObject(), Serializable {
    var name: String = ""      // 名前

    // idをプライマリーキーとして設定
    @PrimaryKey
    var id: Int = 0
}