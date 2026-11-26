package com.example.hik_chitato

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hik_chitato.data.model.Booking
import com.example.hik_chitato.data.model.SalonService
import com.example.hik_chitato.databinding.ItemBookingBinding

class BookingAdapter(
    private var bookingList: List<Booking>,
    private var serviceMap: Map<Long, SalonService> = emptyMap(),
    private val onEditClick: (Booking) -> Unit,
    private val onDeleteClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val item = bookingList[position]
        val serviceName = serviceMap[item.serviceId]?.name ?: "Layanan #${item.serviceId}"

        holder.binding.tvBookingService.text = serviceName
        holder.binding.tvBookingStatus.text = item.status
        holder.binding.tvBookingDatetime.text = "📅 ${item.bookingDate} | ⏰ ${item.bookingTime}"
        holder.binding.tvBookingNotes.text = if (!item.notes.isNullOrEmpty()) "Catatan: ${item.notes}" else "Catatan: -"

        holder.binding.btnEditBooking.setOnClickListener {
            onEditClick(item)
        }

        holder.binding.btnDeleteBooking.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = bookingList.size

    fun updateData(newList: List<Booking>, newServiceMap: Map<Long, SalonService>) {
        bookingList = newList
        serviceMap = newServiceMap
        notifyDataSetChanged()
    }
}
