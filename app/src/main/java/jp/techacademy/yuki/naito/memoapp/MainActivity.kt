package jp.techacademy.yuki.naito.memoapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.Sort
import jp.techacademy.yuki.naito.memoapp.R
import jp.techacademy.yuki.naito.memoapp.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_input.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

const val EXTRA_MEMO = "jp.techacademy.yuki.naito.memoapp.MEMO"


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mMemoAdapter: MemoAdapter
    private lateinit var mRealm: Realm
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var mMemo: Memo? = null
    private var customer_id = -1

        private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            //reloadListView()
        }
    }

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
        //addMemo()
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar.setBackgroundColor(Color.LTGRAY)
        toolbar.setTitleTextColor(Color.BLACK)
        setSupportActionBar(binding.toolbar)
        search_button.setBackgroundColor(Color.LTGRAY)
        search_button.setTextSize(14F)
        search_text_button.setOnClickListener(this)
/*
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)*/

        binding.fab.setOnClickListener { view ->
  /*          Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
            val intent = Intent(this, InputMemoActivity::class.java)
            startActivity(intent)
        }

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)

        // ListViewの設定
        mMemoAdapter = MemoAdapter(this)

        // ListViewをタップしたときの処理
        listView1.setOnItemClickListener { parent, _, position, _ ->
            // 入力・編集する画面に遷移させる
            val memo = parent.adapter.getItem(position) as Memo
            val intent = Intent(this, InputMemoActivity::class.java)
            intent.putExtra(EXTRA_MEMO, memo.id)
            startActivity(intent)
        }

        // ListViewを長押ししたときの処理
        listView1.setOnItemLongClickListener { parent, _, position, _ ->
            // タスクを削除する
            val task = parent.adapter.getItem(position) as Memo

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this)

            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            builder.setPositiveButton("OK"){_, _ ->
                val results = mRealm.where(Memo::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                reloadListView()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }

        // アプリ起動時に表示テスト用のタスクを作成する
        //addMemoForTest()

        reloadListView()
    }

    override fun onClick(v: View) {
        if (v == search_text_button) {
            Log.d("クリックされました", "クリックされました")
            intent = Intent(this, MemoSearchDetailActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadListView() {
        var memoRealmResults: RealmResults<Memo>
        var intent = intent
        var getItems = intent.getStringArrayListExtra("SEND_VALUES")
        if(getItems != null) {
            customer_id = getItems.get(0).toInt()
            var title = getItems.get(1)
        }
        mRealm = Realm.getDefaultInstance()
        Log.d("顧客番号", customer_id.toString())
        if(customer_id != -1){
            if(!getItems!!.get(1).equals("")) {
                memoRealmResults =
                    mRealm.where(Memo::class.java).equalTo("customer.id", customer_id)
                        .equalTo("title", getItems!!.get(1)).findAll()
            }else{
                memoRealmResults =
                    mRealm.where(Memo::class.java).equalTo("customer.id", customer_id).findAll()
            }
        }else if(getItems != null){
            if(!getItems!!.get(1).equals("")) {
                memoRealmResults =
                    mRealm.where(Memo::class.java).equalTo("title", getItems!!.get(1).toString()).findAll()
            }else{
                mRealm = Realm.getDefaultInstance()
                // Realmデータベースから、「すべてのデータを取得して新しい日時順に並べた結果」を取得
                memoRealmResults =
                    mRealm.where(Memo::class.java).findAll().sort("date", Sort.DESCENDING)
            }
        }else{
            mRealm = Realm.getDefaultInstance()
            // Realmデータベースから、「すべてのデータを取得して新しい日時順に並べた結果」を取得
            memoRealmResults =
                mRealm.where(Memo::class.java).findAll().sort("date", Sort.DESCENDING)
        }
        // 上記の結果を、TaskListとしてセットする
        mMemoAdapter.mMemoList = mRealm.copyFromRealm(memoRealmResults)

        // TaskのListView用のアダプタに渡す
        listView1.adapter = mMemoAdapter

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mMemoAdapter.notifyDataSetChanged()
    }
    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }

    private fun addMemoForTest() {
        val memo = Memo()
        memo.customer = Customer()
        memo.title = "作業"
        memo.contents = "プログラムを書いてPUSHする"
        memo.date = Date()
        memo.id = 0
        mRealm.beginTransaction()
        mRealm.copyToRealmOrUpdate(memo)
        mRealm.commitTransaction()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items taddo the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun addMemo() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()

        if (mMemo == null) {
            // 新規作成の場合
            mMemo = Memo()

            val taskRealmResults = realm.where(Memo::class.java).findAll()

            val identifier: Int =
                if (taskRealmResults.max("id") != null) {
                    taskRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mMemo!!.id = identifier
        }
        var customer = Customer()
        val title = title_edit_text.text.toString()
        val content = content_edit_text.text.toString()
        mMemo!!.customer = customer
        mMemo!!.title = title
        mMemo!!.contents = content
        val calendar = GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute)
        val date = calendar.time
        mMemo!!.date = date

        realm.copyToRealmOrUpdate(mMemo!!)
        realm.commitTransaction()

        realm.close()
    }
}