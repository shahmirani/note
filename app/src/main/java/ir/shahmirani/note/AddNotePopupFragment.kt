package ir.shahmirani.note

import android.location.GnssAntennaInfo.Listener
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import ir.shahmirani.note.databinding.FragmentAddNotePopupBinding
import ir.shahmirani.note.utils.NoteData


class AddNotePopupFragment : DialogFragment() {
    private lateinit var binding: FragmentAddNotePopupBinding
    private lateinit var Listener : DialogNextBtnClickListener
    private var noteData :NoteData? = null

    fun setListener(Listener : DialogNextBtnClickListener){
        this.Listener =Listener
    }
    companion object{
        const val TAG ="AddNotePopupFragment"
        @JvmStatic
        fun newInstance(taskId:String,task:String)=AddNotePopupFragment().apply {
            arguments =Bundle().apply {
                putString("taskId",taskId)
                putString("task",task)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddNotePopupBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){
            noteData =NoteData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString())
            binding.noteEt.setText(noteData?.task)
        }
        registerEvents()
    }
    private fun registerEvents(){
        binding.noteNextBtn.setOnClickListener {
            val noteTask = binding.noteEt.text.toString()
            if (noteTask.isNotEmpty()) {
                if (noteData == null){
                    Listener.onSaveTask(noteTask,binding.noteEt)
                }else{
                    noteData?.task = noteTask
                    Listener.onUpdateTask(noteData!! , binding.noteEt)
                }

            } else {
                Toast.makeText(context,"please type some task" , Toast.LENGTH_SHORT).show()
            }
        }
        binding.noteClose.setOnClickListener {
            dismiss()
        }
    }
    interface DialogNextBtnClickListener{
        fun onSaveTask(note : String,noteEt:TextInputEditText)
        fun onUpdateTask(noteData: NoteData,noteEt:TextInputEditText)
    }

}