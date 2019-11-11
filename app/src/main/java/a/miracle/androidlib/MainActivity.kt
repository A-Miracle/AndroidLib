package a.miracle.androidlib

import a.miracle.androidlib.base.BaseAct
import a.miracle.androidlib.base.RBaseAdapter
import a.miracle.androidlib.base.RViewHolder
import a.miracle.lib_utils.StatusBarUtil
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.v_toolbar.*
import java.util.*
import kotlin.collections.LinkedHashMap

class MainActivity : BaseAct() {

    private var mMap = LinkedHashMap<String, Int>()

    init {
        mMap["BubbleImageView"] = R.layout.view_bubble
        mMap["RulerView"] = R.layout.view_ruler
        mMap["CodeView"] = R.layout.view_code
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv_back.visibility = View.GONE

        val keys = mMap.keys

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MainAdapter()
        for (key in keys) {
            adapter.data.add(key)
        }
        adapter.setOnItemClickListener { parent, holder, view, position ->
            val key = adapter.data[position]

            val bundle = Bundle()
            bundle.putString(DemoAct.EXTRA_TITLE, key)
            mMap[key]?.let { bundle.putInt(DemoAct.EXTRA_LAYOUT_ID, it) }
            val intent = Intent(this, DemoAct::class.java)
            intent.putExtras(bundle)

            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private class MainAdapter : RBaseAdapter<String>() {
        override fun onCreateRViewHolder(parent: ViewGroup?, viewType: Int): RViewHolder {
            return RViewHolder(R.layout.item_main, parent)
        }

        override fun onBindRViewHolder(holder: RViewHolder?, position: Int, viewType: Int) {
            holder?.setText<TextView>(R.id.tv_title, mList[position])
        }
    }
}
