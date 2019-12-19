package a.miracle.androidlib.act

import a.miracle.androidlib.R
import a.miracle.androidlib.base.BaseAct
import a.miracle.androidlib.base.RBaseAdapter
import a.miracle.androidlib.base.RViewHolder
import a.miracle.androidlib.dialog.ZoomViewPagerDialog
import a.miracle.androidlib.widget.exp.simple.TestAct
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import kotlinx.android.synthetic.main.act_main.*
import kotlinx.android.synthetic.main.v_toolbar.*

class MainAct : BaseAct() {
    override fun getLayoutId(): Int {
        return R.layout.act_main
    }

    private val Zoom_View_Pager: Int = 1
    private val Tab_Layout: Int = 2
    private val Path_Anim: Int = 3
    private val Zxing: Int = 4
    private val Exp_RecyclerView: Int = 5
    private val Swipe_RecyclerView: Int = 6
    val zoomViewPagerDialog: ZoomViewPagerDialog by lazy {
        ZoomViewPagerDialog(this)
    }

    private var mMap = LinkedHashMap<String, Int>()

    init {
        mMap["BubbleImageView"] = R.layout.view_bubble
        mMap["RulerView"] = R.layout.view_ruler
        mMap["CodeView"] = R.layout.view_code
        mMap["ZoomViewPager"] = Zoom_View_Pager
        mMap["TabLayout"] = Tab_Layout
        mMap["PathAnim"] = Path_Anim
        mMap["Zxing"] = Zxing
        mMap["ExpRecyclerView"] = Exp_RecyclerView
        mMap["SwipeRecyclerView"] = Swipe_RecyclerView
    }

    override fun onAfterSetContentLayout(savedInstanceState: Bundle?) {
        iv_back.visibility = View.GONE

        val keys = mMap.keys

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MainAdapter()
        for (key in keys) {
            adapter.data.add(key)
        }
        adapter.setOnItemClickListener { parent, holder, view, position ->
            val key = adapter.data[position]
            mMap[key]?.let {
                when (it) {
                    Zoom_View_Pager -> {
                        zoomViewPagerDialog.show()
                    }
                    Tab_Layout -> {
                        startActivity(Intent(this, TabAct::class.java))
                    }
                    Path_Anim -> {
                        startActivity(Intent(this, PathAnimAct::class.java))
                    }
                    Zxing -> {
                        PermissionUtils.permission(PermissionConstants.CAMERA).callback(
                            object : PermissionUtils.FullCallback {
                                override fun onGranted(permissionsGranted: MutableList<String>?) {
                                    startActivity(Intent(applicationContext, ScanAct::class.java))
                                }

                                override fun onDenied(
                                    permissionsDeniedForever: MutableList<String>?,
                                    permissionsDenied: MutableList<String>?
                                ) {
                                    if (permissionsDeniedForever != null) {
                                        // denied forever
                                    } else {
                                        // denied
                                    }
                                }
                            }
                        ).request()
                    }
                    Exp_RecyclerView -> {
                        startActivity(Intent(this, TestAct::class.java))
                    }
                    Swipe_RecyclerView -> {
                        startActivity(Intent(this, SwipeAct::class.java))
                    }
                    else -> {
                        val bundle = Bundle()
                        bundle.putString(DemoAct.EXTRA_TITLE, key)
                        bundle.putInt(DemoAct.EXTRA_LAYOUT_ID, it)
                        val intent = Intent(this, DemoAct::class.java)
                        intent.putExtras(bundle)

                        startActivity(intent)
                    }
                }
            }
        }
        recyclerView.adapter = adapter
    }

    class MainAdapter : RBaseAdapter<String>() {
        override fun onCreateRViewHolder(parent: ViewGroup?, viewType: Int): RViewHolder {
            return RViewHolder(R.layout.item_main, parent)
        }

        override fun onBindRViewHolder(holder: RViewHolder?, position: Int, viewType: Int) {
            holder?.setText<TextView>(R.id.tv_title, mList[position])
        }
    }
}
