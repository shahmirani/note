package ir.shahmirani.note.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.shahmirani.note.databinding.EachNoteItemBinding

class NoteAdapter(private val list : MutableList<NoteData>) :RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
    private var listener:NoteAdapterClicksInterface?=null
    fun setListener(listener:NoteAdapterClicksInterface){
        this.listener = listener
    }
    inner class NoteViewHolder(val binding :EachNoteItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = EachNoteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.noteTask.text = this.task

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }
                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)
                }
            }
        }
    }
    interface NoteAdapterClicksInterface{
        fun onDeleteTaskBtnClicked(noteData: NoteData)
        fun onEditTaskBtnClicked(noteData: NoteData)
    }
}