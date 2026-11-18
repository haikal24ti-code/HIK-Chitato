package com.example.hik_chitato

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.hik_chitato.databinding.ItemSalonServiceBinding

class CustomAdapter(
    private val context: Context,
    private var serviceList: List<SalonService>,
    private val onItemClick: ((SalonService) -> Unit)? = null
) : BaseAdapter() {

    private var filteredList: List<SalonService> = serviceList

    override fun getCount(): Int = filteredList.size

    override fun getItem(position: Int): SalonService = filteredList[position]

    override fun getItemId(position: Int): Long = filteredList[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemSalonServiceBinding
        val view: View

        if (convertView == null) {
            binding = ItemSalonServiceBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = convertView.tag as ItemSalonServiceBinding
        }

        val item = getItem(position)
        binding.tvServiceName.text = item.name
        binding.tvServiceDesc.text = item.description
        binding.tvServicePrice.text = item.price
        binding.imgServiceIcon.setImageResource(item.iconResId)

        binding.cardServiceItem.setOnClickListener {
            onItemClick?.invoke(item)
        }

        return view
    }

    fun filter(query: String?) {
        val q = query?.trim()?.lowercase().orEmpty()
        filteredList = if (q.isEmpty()) {
            serviceList
        } else {
            serviceList.filter {
                it.name.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.price.lowercase().contains(q)
            }
        }
        notifyDataSetChanged()
    }

    fun updateData(newList: List<SalonService>) {
        serviceList = newList
        filteredList = newList
        notifyDataSetChanged()
    }
}
