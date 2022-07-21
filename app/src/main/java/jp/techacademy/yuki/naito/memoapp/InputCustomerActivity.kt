package jp.techacademy.yuki.naito.memoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import io.realm.Realm
import jp.techacademy.yuki.naito.memoapp.InputMemoActivity.Companion.EXTRA_CUSTOMER_NAME
import kotlinx.android.synthetic.main.activity_input_customer.*
import kotlinx.android.synthetic.main.activity_input_customer.add_customer
import java.util.*

class InputCustomerActivity : AppCompatActivity(), View.OnClickListener {
    private var mCustomer: Customer? = null
    var customer: String = ""
    var memo_id = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_customer)
        Log.d("顧客追加画面に来ました", "顧客追加画面に来ました")
        var intent = intent
        customer = intent.getStringExtra(EXTRA_CUSTOMER_NAME).toString()
        memo_id = intent.getIntExtra(EXTRA_MEMO, -1)
        val realm = Realm.getDefaultInstance()
        realm.close()
        if(!customer.equals("デフォルト")) {
            name_edit_text.setText(customer)
            mCustomer = realm.where(Customer::class.java).equalTo("name", customer).findFirst()
        }
        add_customer.setOnClickListener(this)
        cancel_button.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v == add_customer) {
            customer = name_edit_text.text.toString()
            addCustomer()
            intent = Intent(this, InputMemoActivity::class.java)
            intent.putExtra(EXTRA_MEMO, memo_id)
            startActivity(intent)
        }

        if(v == cancel_button) {
            intent = Intent(this, InputMemoActivity::class.java)
            intent.putExtra(EXTRA_MEMO, memo_id)
            startActivity(intent)
        }
    }

    private fun addCustomer() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val customerRealmResults = realm.where(Customer::class.java).findAll()
        if (mCustomer == null) {
            // 新規作成の場合
            mCustomer = Customer()

            val identifier: Int =
                if (customerRealmResults.max("id") != null) {
                    customerRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mCustomer!!.id = identifier
        }
        var name = name_edit_text.text.toString()
        mCustomer!!.name = name
        realm.copyToRealmOrUpdate(mCustomer!!)
        realm.commitTransaction()
        realm.close()
    }
}