package jp.techacademy.yuki.naito.memoapp
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.memo_search_detail.*
import java.util.*
import kotlin.collections.ArrayList

class MemoSearchDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var customerItems = arrayOf("全て")
    private var sendItems: ArrayList<String> = ArrayList()
    private var customer_id = -1
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var title = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memo_search_detail)
        customer_text.setTextSize(20F)
        search_button.setOnClickListener(this)
        cancel_button.setOnClickListener(this)
 /*       date_text.setTextSize(20F)
        date_button.setTextSize(20F)
        date_button.setTextColor(Color.BLACK)
        date_button.setOnClickListener(mOnDateClickListener)
        val calendar = Calendar.getInstance()
        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
        mHour = calendar.get(Calendar.HOUR_OF_DAY)
        mMinute = calendar.get(Calendar.MINUTE)*/
        title_text.setTextSize(20F)
        title_text.setTextColor(Color.BLACK)
        var mRealm = Realm.getDefaultInstance()
        val customer_list = mRealm.where(Customer::class.java).findAll()
        for(customer in customer_list){
            customerItems += customer.name
        }
        // Spinnerの取得
        val customers = findViewById<Spinner>(R.id.customers)
        // Adapterの生成
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, customerItems)
        // AdapterをSpinnerのAdapterとして設定
        customers.adapter = adapter
        customers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            // 項目が選択された時に呼ばれる
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var customer = parent?.selectedItem as String
                var mCustomer = mRealm.where(Customer::class.java).equalTo("name", customer).findFirst()
                if(mCustomer != null)
                    customer_id = mCustomer!!.id
            }

            // 基本的には呼ばれないが、何らかの理由で選択されることなく項目が閉じられたら呼ばれる
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

/*
    private val mOnDateClickListener = View.OnClickListener {
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mYear = year
                mMonth = month
                mDay = dayOfMonth
                val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
                date_button.text = dateString
                date = dateString
                Log.d("日付", date.toString())
            }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }


    private val mOnDoneClickListener = View.OnClickListener {
        //addMemo()
        finish()
    }*/

    override fun onClick(v: View) {
        if(v == search_button ) {
            title = title_edit_text.text.toString()
            Log.d("検索します。", "検索します。")
            Log.d("顧客番号", customer_id.toString())
            Log.d("タイトル", title)
            sendItems.add(customer_id.toString())
            sendItems.add(title)
            val intent = Intent(this, MainActivity::class.java)
            intent.putStringArrayListExtra("SEND_VALUES", sendItems)
            startActivity(intent)
        }

        if(v == cancel_button){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}