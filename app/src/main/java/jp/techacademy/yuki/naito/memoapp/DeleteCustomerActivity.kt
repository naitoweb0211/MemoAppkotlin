package jp.techacademy.yuki.naito.memoapp

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import io.realm.Realm
import jp.techacademy.yuki.naito.memoapp.InputMemoActivity.Companion.EXTRA_CUSTOMER_NAME
import kotlinx.android.synthetic.main.activity_delete_customer.*
import kotlinx.android.synthetic.main.activity_input_customer.add_customer
import kotlinx.android.synthetic.main.activity_input_customer.name_edit_text
import java.util.*

class DeleteCustomerActivity : AppCompatActivity(), View.OnClickListener {
    private var mCustomer: Customer? = null
    var memo_id = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_customer)
        val intent = intent
        var customer = intent.getStringExtra(EXTRA_CUSTOMER_NAME)
        memo_id = intent.getIntExtra(EXTRA_MEMO, -1)
        val realm = Realm.getDefaultInstance()

        if(!customer.equals("デフォルト")) {
            name_edit_text.setText(customer)
            mCustomer = realm.where(Customer::class.java).equalTo("name", customer).findFirst()
        }
        delete_customer.setOnClickListener(this)
        cancel_button.setOnClickListener(this)
        realm.close()
    }

    override fun onClick(v: View) {
        if (v == delete_customer) {
            var intent = intent
            val customer = intent.getStringExtra(EXTRA_CUSTOMER_NAME)
            deleteCustomer(customer.toString())
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


    private fun deleteCustomer(customer: String) {
        Log.d("顧客名", customer)
        val realm = Realm.getDefaultInstance()
        val target = realm.where(Customer::class.java)
            .equalTo("name",customer)
            .findAll()
        realm.beginTransaction()
        target.deleteFromRealm(0)
        realm.commitTransaction()
        realm.close()
        intent = Intent(this, InputMemoActivity::class.java)
        startActivity(intent)
    }
}