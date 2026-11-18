package com.example.hik_chitato

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hik_chitato.databinding.ItemOnboardingPageBinding

class OnboardingPagerAdapter(
    private val items: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingPagerAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(val binding: ItemOnboardingPageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvOnboardingTag.text = item.tagText
        holder.binding.tvOnboardingTitle.text = item.title
        holder.binding.tvOnboardingDesc.text = item.description
        holder.binding.imgOnboarding.setImageResource(item.imageResId)
    }

    override fun getItemCount(): Int = items.size
}
