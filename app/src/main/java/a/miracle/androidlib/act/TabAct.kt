package a.miracle.androidlib.act

import a.miracle.androidlib.R
import a.miracle.androidlib.adapter.ItemAdapter
import a.miracle.androidlib.base.BaseAct
import a.miracle.androidlib.widget.MaxHeightRecyclerView
import a.miracle.lib_widget.azlist.AZItemEntity
import a.miracle.lib_widget.azlist.AZWaveSideBarView
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.hmy.popwindow.PopWindow
import kotlinx.android.synthetic.main.act_tab.*
import kotlinx.android.synthetic.main.v_toolbar.*
import java.util.*

/**
 * Created by c.tao on 2019/11/22
 */
class TabAct : BaseAct() {
    override fun getLayoutId(): Int {
        return R.layout.act_tab
    }

    var mCurrentTab: Int = 0
    private lateinit var mAdapter: ItemAdapter

    fun getCurrentTab(): Int {
        return mCurrentTab
    }

    override fun onAfterSetContentLayout(savedInstanceState: Bundle?) {
        tv_title.text = "TabLayout"
        iv_back.setOnClickListener { finish() }
        iv_more.setOnClickListener {
            mPopWindowScot.show(cv)
            iv_more.setImageResource(R.mipmap.ic_more_open)
        }

        val titles = arrayOf("Deals", "Drinks", "Starter", "Main")

        tabLayout.setTitleNotViewPager(*titles)
        tabLayout.setSelectedIndicatorColors(
            Color.parseColor("#FFFF2525"),
            Color.parseColor("#FF080808")
        )

        tabLayout.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mCurrentTab = position
                mAdapter.notifyDataSetChanged()
            }
        })

        initPopWindow()

        val hashSet = TreeSet<String>()
        mDateList.clear()
        mDateList.addAll(fillData(titles, hashSet))
        mBarList.setLetters(ArrayList(hashSet))
        mAdapter.notifyDataSetChanged()
    }


    private lateinit var mPopWindowScot: PopWindow
    private lateinit var mBarList: AZWaveSideBarView
    private val mDateList = ArrayList<AZItemEntity<String>>()

    private fun initPopWindow() {
        // 分类列表的 popwindow
        val customView2 = View.inflate(this, R.layout.popwindow_store_scot, null)
        var mScotRecyclerView: MaxHeightRecyclerView = customView2.findViewById(R.id.recycler_list)
        mBarList = customView2.findViewById(R.id.bar_list)
        customView2.findViewById<View>(R.id.pop_iv_scot)
            .setOnClickListener { mPopWindowScot.dismiss() }

        mAdapter = ItemAdapter(mDateList, this)
        mAdapter.setOnItemListener { _, position ->
            tabLayout.currentSelectItem = position
            mPopWindowScot.dismiss()
        }

        mScotRecyclerView.layoutManager = LinearLayoutManager(this)
        mScotRecyclerView.adapter = mAdapter

        mBarList.setOnLetterChangeListener { letter ->
            val position = mAdapter.getSortLettersFirstPosition(letter)
            if (position != -1) {
                if (mScotRecyclerView.layoutManager is LinearLayoutManager) {
                    (mScotRecyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
                } else {
                    (mScotRecyclerView.layoutManager as RecyclerView.LayoutManager).scrollToPosition(position)
                }
            }
        }

        mPopWindowScot = PopWindow.Builder(this)
            .setStyle(PopWindow.PopWindowStyle.PopDown)
            .setView(customView2)
            .create()
        mPopWindowScot.setPopWindowDismissListener {
            iv_more.setImageResource(R.mipmap.ic_more_close)
        }
    }

    private fun fillData(date: Array<String>, mHashSet: TreeSet<String>): List<AZItemEntity<String>> {
        val sortList = ArrayList<AZItemEntity<String>>()
        for (aDate in date) {
            val item = AZItemEntity<String>()
            item.value = aDate
            //取第一个首字母
            val letters = aDate.substring(0, 1).toUpperCase()
            mHashSet.add(letters)
            // 正则表达式，判断首字母是否是英文字母
            if (letters.matches("[A-Z]".toRegex())) {
                item.setSortLetters(letters)
            } else {
                item.setSortLetters("#")
            }
            sortList.add(item)
        }
        return sortList

    }
}