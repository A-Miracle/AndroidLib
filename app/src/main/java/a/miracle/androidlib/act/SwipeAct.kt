package a.miracle.androidlib.act

import a.miracle.androidlib.R
import a.miracle.androidlib.base.BaseAct
import a.miracle.androidlib.base.RBaseAdapter
import a.miracle.androidlib.base.RViewHolder
import a.miracle.androidlib.utils.ToastUtils
import a.miracle.lib_utils.DisplayUtil.dp2px
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yanzhenjie.recyclerview.*
import kotlinx.android.synthetic.main.act_swipe.*
import kotlinx.android.synthetic.main.v_toolbar.*

class SwipeAct : BaseAct() {

    override fun getLayoutId(): Int {
        return R.layout.act_swipe
    }

    override fun onAfterSetContentLayout(savedInstanceState: Bundle?) {

        iv_back.setOnClickListener { finish() }
        tv_title.text = "SwipeRecyclerView"

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setSwipeMenuCreator(swipeMenuCreator)
        recyclerView.setOnItemMenuClickListener(mMenuItemClickListener)
        val adapter = MainAct.MainAdapter()
        adapter.data.addAll(listOf("1", "2", "3", "4", "5"))
        recyclerView.adapter = adapter
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private val swipeMenuCreator: SwipeMenuCreator = object : SwipeMenuCreator {
        override fun onCreateMenu(
            swipeLeftMenu: SwipeMenu,
            swipeRightMenu: SwipeMenu,
            position: Int
        ) {
            val width = dp2px(68f)
            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            run {
                val addItem: SwipeMenuItem =
                    SwipeMenuItem(applicationContext).setBackground(R.drawable.selector_purple)
                        .setImage(R.mipmap.ic_action_add)
                        .setWidth(width)
                        .setHeight(height)
                swipeLeftMenu.addMenuItem(addItem) // 添加菜单到左侧。
                val closeItem: SwipeMenuItem =
                    SwipeMenuItem(applicationContext).setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_close)
                        .setWidth(width)
                        .setHeight(height)
                swipeLeftMenu.addMenuItem(closeItem) // 添加菜单到左侧。
            }
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            run {
                val deleteItem: SwipeMenuItem =
                    SwipeMenuItem(applicationContext).setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height)
                swipeRightMenu.addMenuItem(deleteItem) // 添加菜单到右侧。
                val addItem: SwipeMenuItem =
                    SwipeMenuItem(applicationContext).setBackground(R.drawable.selector_purple)
                        .setText("添加")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height)
                swipeRightMenu.addMenuItem(addItem) // 添加菜单到右侧。
            }
        }
    }

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private val mMenuItemClickListener =
        OnItemMenuClickListener { menuBridge, position ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction // 左侧还是右侧菜单。
            val menuPosition = menuBridge.position // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                ToastUtils.show("第$position; 右侧菜单第$menuPosition")
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                ToastUtils.show("第$position; 左侧菜单第$menuPosition")
            }
        }
}
