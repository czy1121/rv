package com.demo.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.demo.rv.R
import com.demo.rv.databinding.ActivityMainBinding
import com.demo.rv.databinding.ItemLink2Binding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.reezy.cosmo.pullrefresh.PullRefreshLayout
import me.reezy.cosmo.pullrefresh.simple.SimpleHeader
import me.reezy.cosmo.rv.animator.BaseItemAnimator
import me.reezy.cosmo.rv.animator.FadeInAnimator
import me.reezy.cosmo.rv.animator.FlipInXAnimator
import me.reezy.cosmo.rv.animator.FlipInYAnimator
import me.reezy.cosmo.rv.animator.ScaleInAnimator
import me.reezy.cosmo.rv.animator.SlideInXAnimator
import me.reezy.cosmo.rv.animator.SlideInYAnimator
import me.reezy.cosmo.rv.itemtype.*
import me.reezy.cosmo.rv.itemtype.adapter.ItemTypeAdapter
import me.reezy.cosmo.rv.loadmore.LoadMoreAdapter

class MainActivity : AppCompatActivity(R.layout.activity_main) {


    enum class Type(val animator: BaseItemAnimator) {
        FadeIn(FadeInAnimator()),
        ScaleIn(ScaleInAnimator()),
        Landing(ScaleInAnimator(1.5f)),

        ScaleInTop(ScaleInAnimator(pivotX = 0.5f, pivotY = 0f)),
        ScaleInBottom(ScaleInAnimator(pivotX = 0.5f, pivotY = 1f)),
        ScaleInLeft(ScaleInAnimator(pivotX = 0f, pivotY = 0.5f)),
        ScaleInRight(ScaleInAnimator(pivotX = 1f, pivotY = 0.5f)),

        FlipInTop(FlipInXAnimator(-90f)),
        FlipInBottom(FlipInXAnimator(90f)),
        FlipInLeft(FlipInYAnimator(-90f)),
        FlipInRight(FlipInYAnimator(90f)),


        FadeInLeft(SlideInXAnimator(-0.25f)),
        FadeInRight(SlideInXAnimator(0.25f)),
        FadeInTop(SlideInYAnimator(-0.25f)),
        FadeInBottom(SlideInYAnimator(0.25f)),

        SlideInLeft(SlideInXAnimator(-1f)),
        SlideInRight(SlideInXAnimator(1f)),
        SlideInTop(SlideInYAnimator(-1f)),
        SlideInBottom(SlideInYAnimator(1f)),

        OvershootInLeft(SlideInXAnimator(-1.0f).setInterceptor(OvershootInterpolator(2f), null)),
        OvershootInRight(SlideInXAnimator(1.0f).setInterceptor(OvershootInterpolator(2f), null)),
    }


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
            add(dataBindingType<ItemLink2Binding, Link2>(R.layout.item_link2) { holder, item ->
                holder.binding.item = item
            })
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.list.adapter = adapter
        binding.list.itemAnimator = Type.SlideInLeft.animator

        binding.refresh.setOnRefreshListener {
            lifecycleScope.launch {
                delay(1000)
                adapter.addList(list, true, true)
                binding.refresh.finish()
            }
        }

        var page = 1

        adapter.setOnLoadMoreListener {
            lifecycleScope.launch {
                delay(1000)
                adapter.addList(list.shuffled(), page++ != 5, false)
            }
        }

        adapter.startLoading(true)

        lifecycleScope.launch {
            delay(1000)
            adapter.addList(listOf(), false, true)
        }

        binding.spinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, Type.values().map { it.name })
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.list.itemAnimator = Type.values()[position].animator
                binding.list.itemAnimator?.addDuration = 500
                binding.list.itemAnimator?.removeDuration = 500
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // no-op
            }
        }

        var count = 1
        binding.btnAdd.setOnClickListener {
            val newList = adapter.currentList.toMutableList()
            newList.add(1, Link2("new item ${count++}"))
            adapter.addList(newList, true, true)
        }

        binding.btnRemove.setOnClickListener {
            val newList = adapter.currentList.toMutableList()
            newList.removeAt(1)
            adapter.addList(newList, true, true)
//            adapter.remove(1)
        }
    }
}