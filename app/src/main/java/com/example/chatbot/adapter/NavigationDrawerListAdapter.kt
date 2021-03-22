package com.example.chatbot.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.TypedArrayUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.R
import com.example.chatbot.databinding.DrawerMenuItemBinding
import com.example.chatbot.model.NavigationAdapterItemModel


class NavigationDrawerListAdapter(private val interaction: Interaction? = null,val context:Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

     public var selected_item = 0
    lateinit var senderBinding: DrawerMenuItemBinding
    var chatList: List<NavigationAdapterItemModel> = ArrayList<NavigationAdapterItemModel>()


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NavigationAdapterItemModel>() {

        override fun areItemsTheSame(
            oldItem: NavigationAdapterItemModel,
            newItem: NavigationAdapterItemModel
        ): Boolean {
            return oldItem.windowNum == newItem.windowNum
        }

        override fun areContentsTheSame(
            oldItem: NavigationAdapterItemModel,
            newItem: NavigationAdapterItemModel
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        senderBinding = DrawerMenuItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ChatWindowViewHolder(
            senderBinding,
            interaction
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatWindowViewHolder -> {
                if(position == selected_item)
                {
                    holder.mBinding.parent.setBackgroundColor(ContextCompat.getColor(context,
                        R.color.green_light))
                }
                else
                {
                    holder.mBinding.parent.setBackgroundColor(ContextCompat.getColor(context,
                        R.color.green_dark))
                }
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<NavigationAdapterItemModel>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }

    class ChatWindowViewHolder
        (
        val mBinding: DrawerMenuItemBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(item: NavigationAdapterItemModel) = with(mBinding.root) {
            itemView.setOnClickListener {
                interaction?.onNavigationItemSelected(adapterPosition, item)
            }

            mBinding.textView2.setText(item.label)
            if(item.windowNum != 0)
            mBinding.textView3.setText(item.windowNum.toString())
            else
                mBinding.textView3.setText("")

        }
    }

    interface Interaction {
        fun onNavigationItemSelected(position: Int, item: NavigationAdapterItemModel)
    }
}