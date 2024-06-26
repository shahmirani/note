package ir.shahmirani.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ir.shahmirani.note.databinding.FragmentHomeBinding
import ir.shahmirani.note.utils.NoteAdapter
import ir.shahmirani.note.utils.NoteData


class HomeFragment : Fragment(), AddNotePopupFragment.DialogNextBtnClickListener,
    NoteAdapter.NoteAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var NavController:NavController
    private lateinit var binding: FragmentHomeBinding
    private  var popupFragment: AddNotePopupFragment? = null
    private lateinit var adapter: NoteAdapter
    private lateinit var mList:MutableList<NoteData>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun registerEvents(){
        binding.addBtnHome.setOnClickListener {
            if (popupFragment != null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            popupFragment = AddNotePopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager,
              AddNotePopupFragment.TAG
            )

        }
    }


    private fun init(view:View){
        NavController = Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        databaseRef =FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = NoteAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnappshot in snapshot.children){
                    val noteTask = taskSnappshot.key?.let {
                        NoteData(it , taskSnappshot.value.toString())
                    }
                    if (noteTask !=null){
                        mList.add(noteTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }



    override fun onSaveTask(note: String, noteEt: TextInputEditText) {
        databaseRef.push().setValue(note).addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(context,"note saved successfully :) ",Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
            }
            noteEt.text =null
            popupFragment!!.dismiss()
        }

    }

    override fun onUpdateTask(noteData: NoteData, noteEt: TextInputEditText) {
        val map = HashMap<String ,Any>()
        map[noteData.taskId]=noteData.task
        databaseRef.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context,"Updated successfully !!",Toast.LENGTH_SHORT).show()
                noteEt.text = null
            }else{
                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
            }
            popupFragment!!.dismiss()
        }
    }

    override fun onDeleteTaskBtnClicked(noteData: NoteData) {
       databaseRef.child(noteData.taskId).removeValue().addOnCompleteListener {
           if (it.isSuccessful){
               Toast.makeText(context,"Deleted successfully !!",Toast.LENGTH_SHORT).show()
           }else{
               Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
           }
       }
    }

    override fun onEditTaskBtnClicked(noteData: NoteData) {
        if (popupFragment != null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()

        popupFragment = AddNotePopupFragment.newInstance(noteData.taskId , noteData.task)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager , AddNotePopupFragment.TAG)

    }


}