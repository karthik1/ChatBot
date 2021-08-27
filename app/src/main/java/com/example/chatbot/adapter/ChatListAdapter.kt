package com.example.chatbot.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.databinding.LayoutBotItemBinding
import com.example.chatbot.databinding.LayoutSenderItemBinding
import com.example.chatbot.model.Chat


class ChatListAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val BOT = 100
    private val SENDER = 101
    lateinit var senderBinding: LayoutSenderItemBinding
    lateinit var botBinding: LayoutBotItemBinding
    var chatList: List<Chat> = ArrayList<Chat>()


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Chat>() {

        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        if (viewType == SENDER) {
            // self message
            senderBinding = LayoutSenderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return SendViewHolder(senderBinding)

        } else {
            // WatBot message
            botBinding = LayoutBotItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return BotViewHolder(botBinding)
        }

    }

    override fun getItemViewType(position: Int): Int {

        val chat = chatList.get(position)
        return if (chat.status.equals("recv")) {
            BOT
        } else SENDER

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat: Chat = chatList.get(position)
        when (holder) {
            is BotViewHolder -> {
                holder.botBinding?.messageTextView?.setText(chat.senderOrBotText)
            }
            is SendViewHolder -> {
                holder.senderBinding?.messageTextView?.setText(chat.senderOrBotText)
            }
        }
    }

//
//     var chatList: List<Chat>
//        get() = differ.currentList
//        set(value) = differ.submitList(value)

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun addSingleMsg(chat: Chat) {
//        val list = differ.currentList.toMutableList()
//        list.add(chat)
//        Log.d(" SIZE IN ADAPTER", "addSingleMsg: " + list.size)
//        differ.submitList(list)
//        chatList = differ.currentList.toMutableList()

    }

    fun submitList(list: List<Chat>) {

        chatList = list
        notifyDataSetChanged()

    }

    class SendViewHolder
        (
        val senderBinding: LayoutSenderItemBinding
    ) : RecyclerView.ViewHolder(senderBinding.root) {

    }

    class BotViewHolder
        (
        val botBinding: LayoutBotItemBinding
    ) : RecyclerView.ViewHolder(botBinding.root) {

    }
}