package a.miracle.androidlib.widget.exp.simple


import a.miracle.androidlib.R
import a.miracle.androidlib.base.BaseAct
import a.miracle.androidlib.base.RViewHolder
import a.miracle.androidlib.utils.ToastUtils
import a.miracle.androidlib.widget.exp.adapter.SEGHelper
import a.miracle.androidlib.widget.exp.listener.OnBindViewHolder
import a.miracle.androidlib.widget.exp.listener.SectionStateChangeListener
import a.miracle.androidlib.widget.exp.models.Item
import a.miracle.androidlib.widget.exp.models.Section
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.act_test.*
import kotlinx.android.synthetic.main.v_toolbar.*

/**
 * Created by c.tao on 2019/11/21
 */
class TestAct : BaseAct() {
    override fun getLayoutId(): Int {
        return R.layout.act_test
    }

    override fun onAfterSetContentLayout(savedInstanceState: Bundle?) {
        iv_back.setOnClickListener { finish() }
        tv_title.text = "ExpRecyclerView"
        val helper = SEGHelper(
            recyclerView,
            GridLayoutManager(this, 1),
            OnBindViewHolder { viewHolder, item, items, linsenter ->
                when (item.layoutId) {
                    // 每个 Section 可单独处理事件
                    R.layout.item_section1 -> {
                        onSection(item, viewHolder, linsenter)
                    }
                    R.layout.item_section2 -> {
                        onSection(item, viewHolder, linsenter)
                    }
                    R.layout.item_section3 -> {
                        onSection(item, viewHolder, linsenter)
                    }
                    else ->{ // Item 事件
                        viewHolder.itemView.setOnClickListener {
                            if (item.name.isNotEmpty())
                                ToastUtils.show(item.name)
                        }
                    }
                }
            })

        var items = arrayListOf(
            Item(R.layout.item_card_not, "S1 Item 1", 0),
            Item(R.layout.item_card, "S1 Item 2", 1),
            Item(R.layout.item_card_not, "S1 Item 3", 2),
            Item(R.layout.item_view_20)
        )
        var section = Section(R.layout.item_section1, "S1", 0)
        helper.addSection(section.name, section, items)

        items = arrayListOf(
            Item(R.layout.item_card, "S2 Item 1", 0),
            Item(R.layout.item_card_not, "S2 Item 2", 1),
            Item(R.layout.item_card, "S2 Item 3", 2),
            Item(R.layout.item_view_20)
        )
        section = Section(R.layout.item_section2, "S2", 1)
        helper.addSection(section.name, section, items)

        items = arrayListOf(
            Item(R.layout.item_card, "S3 Item 1", 0),
            Item(R.layout.item_card_not, "S3 Item 2", 1),
            Item(R.layout.item_view_20)
        )
        section = Section(R.layout.item_section3, "S3", 2)
        helper.addSection(section.name, section, items)

        helper.notifyDataSetChanged()
    }

    private fun onSection(
        item: Item?,
        viewHolder: RViewHolder,
        linsenter: SectionStateChangeListener
    ) {
        item as Section
        val imageView = viewHolder.getView<ImageView>(R.id.iv_arrow)
        viewHolder.itemView.setOnClickListener {
            if (item.isExpanded) {
                imageView.setImageResource(R.mipmap.ic_arrow_top)
            } else {
                imageView.setImageResource(R.mipmap.ic_arrow_bottom)
            }
            linsenter.onSectionStateChanged(item, !item.isExpanded)
        }
    }
}
