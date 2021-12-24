package com.example.smarthomeproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthomeproject.Repositories.*
import kotlinx.android.synthetic.main.fragment_list_switch_item.view.*


class LightsListFragment : Fragment() {

    private val MYDATA = arrayOf("Livingroom", "Bedroom", "Garage", "Kitchen")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lights_list, container, false)

        view.findViewById<RecyclerView>(R.id.recyclerViewLights).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = LightsListAdapter(MYDATA)
        }

        view.findViewById<ImageView>(R.id.btnBackLights).setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        return view
    }
}


class LightsListAdapter(private val myDataset: Array<String>) : RecyclerView.Adapter<LightsListAdapter.MyViewHolder>() {

    private val kitchenlightOn= LightRepository.service.postKitchenLight(LightRepository.Model.Kitchen("on"))
    private val bedroomlightOn= LightRepository.service.postBedroomLight(LightRepository.Model.Bedroom("on"))
    private val garageLightOn= LightRepository.service.postGarageLight(LightRepository.Model.Garage("on"))
    private val livingroomlightOn= LightRepository.service.postLivingroomLight(LightRepository.Model.Livingroom("on"))
    private val bedroomLightOff= LightRepository.service.postBedroomLight(LightRepository.Model.Bedroom("off"))
    private val garageLightOff= LightRepository.service.postGarageLight(LightRepository.Model.Garage("off"))
    private val livingroomLightOff= LightRepository.service.postLivingroomLight(LightRepository.Model.Livingroom("off"))
    private val kitchenLightOff= LightRepository.service.postKitchenLight(LightRepository.Model.Kitchen("off"))

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list_switch_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.title.text = myDataset[position]
        holder.itemView.layout.contentDescription = myDataset[position] + "light."
        holder.itemView.myswitch.contentDescription = "Turn on or off the " + myDataset[position] + "light."

        var isChecked = false
        when {
            holder.itemView.title.text == "Kitchen" -> isChecked = RepositoryStates.isKitchenLedOn
            holder.itemView.title.text == "Garage" -> isChecked = RepositoryStates.isGarageLedOn
            holder.itemView.title.text == "Bedroom" -> isChecked = RepositoryStates.isBedroomLedOn
            holder.itemView.title.text == "Livingroom" -> isChecked = RepositoryStates.isLivingroomLedOn
        }
        holder.itemView.myswitch.isChecked = isChecked

        holder.itemView.myswitch.setOnClickListener{
            if(holder.itemView.title.text == "Kitchen") {
                if (RepositoryStates.isKitchenLedOn) {
                    kitchenLightOff.clone().enqueue(callbackKitchen)
                    RepositoryStates.isKitchenLedOn = false
                } else {
                    kitchenlightOn.clone().enqueue(callbackKitchen)
                    RepositoryStates.isKitchenLedOn = true
                }
            }

            if(holder.itemView.title.text == "Garage") {
                if (RepositoryStates.isGarageLedOn) {
                    garageLightOff.clone().enqueue(callbackGarage)
                    RepositoryStates.isGarageLedOn = false

                } else {
                    garageLightOn.clone().enqueue(callbackGarage)
                    RepositoryStates.isGarageLedOn = true
                }
            }

            if(holder.itemView.title.text == "Bedroom") {
                if (RepositoryStates.isBedroomLedOn) {
                    bedroomLightOff.clone().enqueue(callbackBedroom)
                    RepositoryStates.isBedroomLedOn = false
                } else {
                    bedroomlightOn.clone().enqueue(callbackBedroom)
                    RepositoryStates.isBedroomLedOn = true
                }
            }

            if(holder.itemView.title.text == "Livingroom") {
                if (RepositoryStates.isLivingroomLedOn) {
                    livingroomLightOff.clone().enqueue(callbackLivingroom)
                    RepositoryStates.isLivingroomLedOn = false
                } else {
                    livingroomlightOn.clone().enqueue(callbackLivingroom)
                    RepositoryStates.isLivingroomLedOn = true
                }
            }
        }
    }
    override fun getItemCount() = myDataset.size
}
