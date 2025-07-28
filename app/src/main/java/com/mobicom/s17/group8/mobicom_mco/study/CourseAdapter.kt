package com.mobicom.s17.group8.mobicom_mco.study

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobicom.s17.group8.mobicom_mco.databinding.ListItemCourseBinding

class CourseAdapter(
    private val onCourseClicked: (Course) -> Unit,
    private val onDeleteClicked: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private var courses: List<Course> = emptyList()
    private var isEditMode: Boolean = false

    inner class CourseViewHolder(val binding: ListItemCourseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ListItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun getItemCount() = courses.size

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.binding.apply {
            tvCourseName.text = course.name
            tvDeckCount.text = "${course.deckCount} decks"

            val color = ContextCompat.getColor(root.context, course.colorResId)
            viewColor.setBackgroundColor(color)

            btnDelete.visibility = if (isEditMode) View.VISIBLE else View.GONE

            btnDelete.setOnClickListener {
                onDeleteClicked(course)
            }

            root.setOnClickListener {
                if (!isEditMode) { // Only navigate if not in edit mode
                    onCourseClicked(course)
                }
            }
        }
    }

    fun submitList(newCourses: List<Course>) {
        courses = newCourses
        notifyDataSetChanged()
    }

    fun setEditMode(isEditing: Boolean) {
        isEditMode = isEditing
        notifyDataSetChanged()
    }
}