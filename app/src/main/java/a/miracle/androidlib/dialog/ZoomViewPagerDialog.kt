package a.miracle.androidlib.dialog

import a.miracle.androidlib.R
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_zoom_view_pager.*


class ZoomViewPagerDialog(val mContext: Context) : BottomSheetDialog(mContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_zoom_view_pager, null)
        setContentView(view)
        iv_close.setOnClickListener { dismiss() }

        val list = listOf<View>(
            View.inflate(context, R.layout.item_card, null),
            View.inflate(context, R.layout.item_card, null),
            View.inflate(context, R.layout.item_card, null),
            View.inflate(context, R.layout.item_card_not, null)
        )

        viewPager.adapter = ItemAdapter(list)
        tabLayout.setViewPager(viewPager)


        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.attributes.apply {
            gravity = Gravity.BOTTOM
        }
        val parent = view.parent as ViewGroup
        parent.setBackgroundResource(android.R.color.transparent)
    }

    private class ItemAdapter(views: List<View>?) : PagerAdapter(){
        private var currentPosition: Int = 0
        private var positionValue: Array<Boolean?>? = null
        private var views: List<View>? = null

        init {
            this.views = views
            this.views?.let {
                positionValue = arrayOfNulls(it.size)
                for (index in it.indices){
                    positionValue!![index] = false
                }
            }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return views!!.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(views!![position], 0)  //添加页卡
            return views!![position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(views!![position])   //删除页卡
        }

        // 跳转到每个页面都要执行的方法
        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            // 把这个position赋值到一个全局变量，通过这个就会知道滑动到哪个页面了
            currentPosition = position
            // 因为这个方法每次跳转会调用多次，在这里设置一个标示，只执行一次就可以了
            if (!positionValue!![position]!!) {
                // 绑定数据

                positionValue!![position] = true
            }
        }
    }
}