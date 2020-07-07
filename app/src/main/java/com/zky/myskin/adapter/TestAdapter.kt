package com.zky.myskin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zky.myskin.R

/**
 * Created by lk
 * Date 2020/7/7
 * Time 15:57
 * Detail:
 */
class TestAdapter(private val data:List<String>) :RecyclerView.Adapter<TestAdapter.TestAdapterHodler> (){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestAdapterHodler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, null)
        return TestAdapterHodler(view)

    }

    override fun getItemCount(): Int {
        return if(data.isNullOrEmpty()) 0 else data.size
    }

    override fun onBindViewHolder(holder: TestAdapterHodler, position: Int) {
        val bean=  data[position]
        holder.atvText.text=bean
    }


    inner class TestAdapterHodler(itemView: View):RecyclerView.ViewHolder(itemView){
        var atvText = itemView.findViewById(R.id.atv_text) as TextView

    }
}