package jp.techacademy.yuki.naito.memoapp

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.util.*

class MemoAdapter(context: Context): BaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    var mMemoList= mutableListOf<Memo>()

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return mMemoList.size
    }

    override fun getItem(position: Int): Any {
        return mMemoList[position]
    }

    override fun getItemId(position: Int): Long {
        return mMemoList[position].id.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null)

        val textView1 = view.findViewById<TextView>(android.R.id.text1)
        val textView2 = view.findViewById<TextView>(android.R.id.text2)

        textView1.text = mMemoList[position].title

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE)
        val date = mMemoList[position].date
        textView2.text = simpleDateFormat.format(date)

        return view
    }
}