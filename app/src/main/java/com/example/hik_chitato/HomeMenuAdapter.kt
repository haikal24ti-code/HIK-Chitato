package com.example.hik_chitato

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hik_chitato.databinding.ItemHomeMenuBinding

class HomeMenuAdapter(
    private val menuList: List<HomeMenuItem>,
    private val onItemClick: (HomeMenuItem) -> Unit
) : RecyclerView.Adapter<HomeMenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(val binding: ItemHomeMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemHomeMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuList[position]
        holder.binding.tvMenuTitle.text = item.title
        holder.binding.tvMenuSubtitle.text = item.subtitle
        holder.binding.imgMenuIcon.setImageResource(item.iconResId)

        holder.binding.cardMenuItem.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = menuList.size
}
