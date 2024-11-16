package Adapters

import Models.Program
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nexatech.staffsyncv3.R

class ProgramAdapter(private val programs: List<Program>) : RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {

    class ProgramViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectTextView: TextView = itemView.findViewById(R.id.subjectTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val linkTextView: TextView = itemView.findViewById(R.id.linkTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_program, parent, false)
        return ProgramViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val program = programs[position]
        holder.subjectTextView.text = program.subject
        holder.descriptionTextView.text = program.description
        holder.linkTextView.text = program.link
    }

    override fun getItemCount(): Int {
        return programs.size
    }
}
