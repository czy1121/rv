package com.demo.app

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.demo.app.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.reezy.cosmo.pullrefresh.PullRefreshLayout
import me.reezy.cosmo.pullrefresh.simple.SimpleHeader
import me.reezy.cosmo.rv.itemtype.*
import me.reezy.cosmo.rv.itemtype.adapter.ItemTypeAdapter
import me.reezy.cosmo.rv.loadmore.LoadMoreAdapter

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    init {
        PullRefreshLayout.setDefaultHeaderFactory {
            SimpleHeader(it.context)
        }
    }

    private val binding by lazy { ActivityMainBinding.bind(findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) }

//    private val adapter = SingleTypeAdapter(bindingType<Link>(R.layout.item_link))


    private val list = listOf(
        Link1(image = "https://loremflickr.com/600/300/d?lock=1", text = "一", desc = "木大木大木大木大"),
        Link2(image = "https://loremflickr.com/600/300/d?lock=4", text = "一二三四", desc = "木大木大木大木大 Link2"),
        Link2(image = "https://loremflickr.com/600/300/d?lock=5", text = "一二三四五", desc = "木大木大木大木大 Link2"),
        Link1(image = "https://loremflickr.com/600/300/d?lock=2", text = "一二", desc = "木大木大木大木大"),
        Link1(image = "https://loremflickr.com/600/300/d?lock=3", text = "一二三", desc = "木大木大木大木大"),

        Link2(image = "https://loremflickr.com/600/300/d?lock=6", text = "一二三四五六", desc = "木大木大木大木大 Link2"),
        Link2(image = "https://loremflickr.com/600/300/d?lock=7", text = "一二三四五六七", desc = "木大木大木大木大 Link2"),
        Link1(image = "https://loremflickr.com/600/300/d?lock=2", text = "一二", desc = "木大木大木大木大"),
        Link1(image = "https://loremflickr.com/600/300/d?lock=3", text = "一二三", desc = "木大木大木大木大"),
        Link2(image = "https://loremflickr.com/600/300/d?lock=6", text = "一二三四五六", desc = "木大木大木大木大 Link2"),
//        Link2(image = "https://loremflickr.com/600/300/d?lock=7", text = "一二三四五六七", desc = "木大木大木大木大 Link2"),
    )

    private val adapter2 by lazy {
        ItemTypeAdapter<Any>().setup {
            add(bindingType<Link1>(R.layout.item_link))
            add(bindingType<Link2>(R.layout.item_link2))
        }
    }
    private val adapter by lazy {
        LoadMoreAdapter().setup {
            add(bindingType<Link1>(R.layout.item_link))
            add(bindingType<Link2>(R.layout.item_link2))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.list.adapter = adapter


        binding.list.itemAnimator = null
        binding.refresh.setOnRefreshListener {
            lifecycleScope.launch {
                delay(1000)
                adapter.addList(list, true, true)
            }
        }

        adapter.startLoading(true)

        adapter.addList(listOf(), false, true)

        var page = 1

        adapter.setOnLoadMoreListener {
            lifecycleScope.launch {
                delay(1000)
                adapter.addList(list.shuffled(), page++ != 5, false)
            }
        }
    }
}