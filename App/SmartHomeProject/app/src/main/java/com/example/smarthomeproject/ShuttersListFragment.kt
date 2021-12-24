package com.example.smarthomeproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthomeproject.Repositories.RepositoryStates
import com.example.smarthomeproject.Repositories.ShutterRepositoryBluetooth
import kotlinx.android.synthetic.main.fragment_list_switch_item.view.*

class ShuttersListFragment : Fragment() {

    private val MYDATA = arrayOf("Shutter 1")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_shutters_list, container, false)

        view.findViewById<RecyclerView>(R.id.recyclerViewShutters).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = ShuttersListAdapter(MYDATA, context)
        }

        view.findViewById<ImageView>(R.id.btnBackShutters).setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        return view
    }
}


class ShuttersListAdapter(private val myDataset: Array<String>, c: Context) : RecyclerView.Adapter<ShuttersListAdapter.MyViewHolder>() {

    private val _shutterRepo = ShutterRepositoryBluetooth()
    private val context = c

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_list_switch_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.title.text = myDataset[position]
        holder.itemView.layout.contentDescription = myDataset[position] + "shutter."
        holder.itemView.myswitch.contentDescription = "Turn on or off the " + myDataset[position] + "shutter."

        var isChecked = false
        when {
            holder.itemView.title.text == "Shutter 1" -> isChecked = RepositoryStates.isShutter1Open
        }
        holder.itemView.myswitch.isChecked = isChecked

        holder.itemView.myswitch.setOnClickListener {
            /*if (!MyUtilities.isNetworkAvailable(context)) {
                Log.d("Error", "No network available")
                // TODO reset switch change
            }*/
            if (holder.itemView.title.text == "Shutter 1") {
                if (RepositoryStates.isShutter1Open) {
                    _shutterRepo.shutter1Close()
                    
                } else {
                    _shutterRepo.shutter1Open()
                }
            }
        }
    }

    override fun getItemCount() = myDataset.size
}

