package com.hfad.runningtracker.Adapters

import android.icu.util.Calendar
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.runningtracker.R
import com.hfad.runningtracker.db.Run
import com.hfad.runningtracker.db.RunDAO
import com.hfad.runningtracker.db.RunningDatabase
import com.hfad.runningtracker.other.TrackingUtility
import kotlinx.android.synthetic.main.fragment_run.view.*
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class RunAdapter:RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    private lateinit var list:List<RunningDatabase>
    fun setFilteredList(filteredList: List<RunningDatabase>) {
        this.list = filteredList
        notifyDataSetChanged()
    }

    val diffCallBack=object:DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode()==newItem.hashCode()
        }
    }

    val differ=AsyncListDiffer(this,diffCallBack)

    fun submitList(list:List<Run>)=differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_run,
            parent,false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run=differ.currentList[position]
        holder.itemView.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context,R.anim.anim_1))

        holder.itemView.apply {
            //holder.itemView.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context,R.anim.anim_1))
            Glide.with(this).load(run.img).into(ivRunImage)

           // holder.itemview.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context,R.anim.anim_2))

            val calendar=Calendar.getInstance().apply {
                timeInMillis=run.timestamp
            }
            val dateFormat=SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text=dateFormat.format(calendar.time)

            "${run.avgSpeedInKMH} km/h".also {
                tvAvgSpeed.text = it
            }
            "${run.distanceInMetres / 1000f}km".also {
                tvDistance.text = it
            }
            tvTime.text = TrackingUtility.getFormattedStopwatchTime(run.timeInMillis)
            "${run.caloriesBurned} kcal".also {
                tvCalories.text = it
            }

            //holder.itemView.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context,R.anim.anim_1))
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}