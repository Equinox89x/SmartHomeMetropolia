package com.example.smarthomeproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smarthomeproject.Repositories.RepositoryStates
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_db.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DBFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val frag =  inflater.inflate(R.layout.fragment_db, container, false)

        frag.findViewById<ImageView>(R.id.btnBackLights).setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        frag.findViewById<MaterialButton>(R.id.btnSave).setOnClickListener {
            CheckedValidations()
        }

        return frag
    }

    private fun validateHomeName(): Boolean {
        val homeInput = lblName.editText?.text.toString()

        if (homeInput.isEmpty()) {
            lblName.setError("Field can't be empty")
            return false
        } else if (homeInput.length > 50) {
            lblName.setError("Username too long")
            return false
        } else {
            lblName.setError(null)
            return true
        }
    }

    private fun validateCountry(): Boolean {
        val countryInput = lblCountry.editText?.text.toString()

        if (countryInput.isEmpty()) {
            lblCountry.setError("Field can't be empty")
            return false
        } else if (countryInput.length > 50) {
            lblCountry.setError("Username too long")
            return false
        } else {
            lblCountry.setError(null)
            return true
        }
    }

    private fun validateCity(): Boolean {
        val cityInput = lblCity.editText?.text.toString()

        if (cityInput.isEmpty()) {
            lblCity.setError("Field can't be empty")
            return false
        } else {
            lblCity.setError(null)
            return true
        }
    }

    private fun validateStreet(): Boolean {
        val streetInput = lblStreet.editText?.text.toString()

        if (streetInput.isEmpty()) {
            lblStreet.setError("Field can't be empty")
            return false
        } else {
            lblStreet.setError(null)
            return true
        }
    }

    private fun validateStreetNumber(): Boolean {
        val streetNumberInput = lblStreetNr.editText?.text

        if (streetNumberInput.toString().isEmpty()) {
            lblStreetNr.setError("Field can't be empty")
            return false
        }
        else {
            lblStreetNr.setError(null)
            return true
        }
    }

    private fun validatePostalCode(): Boolean {
        val postcodeInput = lblPostcode.editText?.text

        if (postcodeInput.toString().isEmpty()) {
            lblStreet.setError("Field can't be empty")
            return false
        }
        else {
            lblStreet.setError(null)
            return true
        }
    }

    private fun CheckedValidations(){

        if(validateHomeName() && validateCountry() && validateCity()
            && validateStreet() && validateStreetNumber() && validatePostalCode()){
                doAsync {
                    val db = HomeDB.get(context!!)
                    val home = lblName.editText?.text.toString()
                    val postcode = lblPostcode.editText?.text.toString()
                    val streetNumber = lblStreetNr.editText?.text.toString()
                    val city = lblCity.editText?.text.toString()
                    val country = lblCountry.editText?.text.toString()
                    val street = lblStreet.editText?.text.toString()
                    if(RepositoryStates.isUnset) {
                        db.homeDao().insert(Home(1.toLong(), home, country, city, street, streetNumber, postcode))

                        uiThread {
                            Toast.makeText(context!!, "Succesfully updated your info, " + home, Toast.LENGTH_SHORT).show()
                        }
                        activity!!.supportFragmentManager.popBackStack()

                    }
                    else{
                        db.homeDao().update(Home(1.toLong(), home, country, city, street, streetNumber, postcode))

                        uiThread {
                            Toast.makeText(context!!, "Succesfully updated your info, " + home , Toast.LENGTH_SHORT).show()
                        }
                        activity!!.supportFragmentManager.popBackStack()
                    }

                }
            }
        else{
            Toast.makeText(context!!, "One or multiple validations were incorrect", Toast.LENGTH_LONG).show()
        }
    }
}
