package jp.techacademy.yuki.naito.memoapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input.*
import java.util.*
class InputMemoActivity : AppCompatActivity(), View.OnClickListener {
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var mMemo: Memo? = null
    private var customerItems = arrayOf("デフォルト")
    private var customer  = ""
    private var mCustomer: Customer? = null
    private var title = ""
    private var content = ""
    var memo_id = 0

    private val mOnDateClickListener = View.OnClickListener {
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mYear = year
                mMonth = month
                mDay = dayOfMonth
                val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
                date_button.text = dateString
            }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private val mOnTimeClickListener = View.OnClickListener {
        val timePickerDialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                mHour = hour
                mMinute = minute
                val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)
                times_button.text = timeString
            }, mHour, mMinute, false)
        timePickerDialog.show()
    }

    private val mOnDoneClickListener = View.OnClickListener {
        addMemo()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // ActionBarを設定する
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setBackgroundColor(Color.LTGRAY)
        tcustomer.setTextSize(20F)
        tcustomer.setTextColor(Color.WHITE)
        add_customer.setTextSize(15F)
        var mRealm = Realm.getDefaultInstance()
        val customer_list = mRealm.where(Customer::class.java).findAll()
        for(customer in customer_list){
            customerItems += customer.name
        }
        mRealm = Realm.getDefaultInstance()
        var intent = intent
        memo_id = intent.getIntExtra(EXTRA_MEMO, -1)
        if(memo_id != -1)
            mMemo = mRealm.where(Memo::class.java).equalTo("id", memo_id).findFirst()
        // UI部品の設定
        date_button.setOnClickListener(mOnDateClickListener)
        times_button.setOnClickListener(mOnTimeClickListener)
        add_memo_button.setOnClickListener(mOnDoneClickListener)
        add_customer.setOnClickListener(this)
        delete_customer.setOnClickListener(this)
        cancel_button.setOnClickListener(this)
        // Spinnerの取得
        val customers = findViewById<Spinner>(R.id.customers)
        // Adapterの生成
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, customerItems)
        // AdapterをSpinnerのAdapterとして設定
        customers.adapter = adapter
        // 選択肢の各項目のレイアウト
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        customers.setBackgroundColor(Color.GRAY)
        // OnItemSelectedListenerの実装
        customers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            // 項目が選択された時に呼ばれる
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                customer = parent?.selectedItem as String
                mCustomer = mRealm.where(Customer::class.java).equalTo("name", customer).findFirst()
            }

            // 基本的には呼ばれないが、何らかの理由で選択されることなく項目が閉じられたら呼ばれる
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        if (mMemo == null) {
            // 新規作成の場合
            val calendar = Calendar.getInstance()
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)
        } else {
            // 更新の場合
            customers.setSelection(mMemo!!.customer!!.id+1)
            title_edit_text.setText(mMemo!!.title)
            content_edit_text.setText(mMemo!!.contents)
            val calendar = Calendar.getInstance()
            calendar.time = mMemo!!.date
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)

            val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
            val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)

            date_button.text = dateString
            times_button.text = timeString
        }
    }

    private fun addMemo() {
        val realm = Realm.getDefaultInstance()
        if (mMemo == null) {
            // 新規作成の場合
            mMemo = Memo()
            val memoRealmResults = realm.where(Memo::class.java).findAll()

            val identifier: Int =
                if (memoRealmResults.max("id") != null) {
                    memoRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mMemo!!.id = identifier
            title = title_edit_text.text.toString()
            content = content_edit_text.text.toString()
        }
        title = title_edit_text.text.toString()
        content = content_edit_text.text.toString()
        var memo = Memo()
        if(mMemo != null)
            memo.id = mMemo!!.id
        memo.customer = mCustomer
        memo.title = title
        memo.contents = content
        val calendar = GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute)
        val date = calendar.time
        memo.date = date
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(memo)
        realm.commitTransaction()
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        realm.close()
    }

    override fun onClick(v: View) {
        if (v == add_customer) {
            val intent = Intent(this, InputCustomerActivity::class.java)
            intent.putExtra(Companion.EXTRA_CUSTOMER_NAME, customer)
            intent.putExtra(EXTRA_MEMO, memo_id)
            startActivity(intent)
        }

        if (v == delete_customer) {
            val intent = Intent(this, DeleteCustomerActivity::class.java)
            intent.putExtra(Companion.EXTRA_CUSTOMER_NAME, customer)
            intent.putExtra(EXTRA_MEMO, memo_id)
            startActivity(intent)
        }

        if(v == add_memo_button) {
            addMemo()
        }

        if(v == cancel_button) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_CUSTOMER_NAME = "jp.techacademy.yuki.naito.memoapp.CUSTOMER_NAME"
    }
}
