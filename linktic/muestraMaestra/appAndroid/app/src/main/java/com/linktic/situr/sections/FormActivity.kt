package com.linktic.situr.sections

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.linktic.situr.BaseActivity
import com.linktic.situr.R
import okhttp3.*
import java.io.IOException
import android.widget.AdapterView
import com.linktic.situr.assets.AppPreferences
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.OkHttpClient
import kotlin.concurrent.schedule


class FormActivity : BaseActivity(), AdapterView.OnItemSelectedListener
{

    private val addresType_arr = arrayOf("Carrera", "Calle", "Transversal", "Avenida", "Avenida carrera", "Avenida calle", "Circular", "Circunvalar", "Diaginal", "Manzana", "Via")
    private val cat_arr = arrayOf("AGENCIA DE VIAJES", "ARRENDADORES DE VEHÍCULOS PARA TURISMO NACIONAL E INTERNACIONAL", "CONCESIONARIOS DE SERVICIOS TURÍSTICOS EN PARQUE", "EMPRESA DE TIEMPO COMPARTIDO Y MULTIPROPIEDAD", "EMPRESA DE TRANSPORTE TERRESTRE AUTOMOTOR", "EMPRESAS CAPTADORAS DE AHORRO PARA VIAJES Y DE SERVICIOS TURÍSTICOS", "ESTABLECIMIENTO DE ALOJAMIENTO Y HOSPEDAJE", "ESTABLECIMIENTO DE GASTRONOMÍA Y SIMILARES", "GUIA DE TURISMO", "OFICINA DE REPRESENTACION TURÍSTICA", "OPERADORES PROFESIONALES DE CONGRESOS, FERIAS Y CONVENCIONES", "PARQUES TEMATICOS", "USUARIOS OPERADORES, DESARROLLADORES E INDUSTRIALES EN ZONA FRACA")

    private val subcat_arr_1 = arrayOf("AGENCIAS DE VIAJES MAYORISTAS", "AGENCIAS DE VIAJES OPERADORAS", "AGENCIAS DE VIAJES Y DE TURISMO" )
    private val subcat_arr_2 = arrayOf("ARRENDADORES DE VEHÍCULOS PARA TURISMO NACIONAL E INTERNACIONAL" )
    private val subcat_arr_3 = arrayOf("CONCESIONARIOS DE SERVICIOS TURÍSTICOS EN PARQUE" )
    private val subcat_arr_4 = arrayOf("EMPRESA COMERCIALIZADORA DE TIEMPO COMPARTIDO Y MULTIPROPIEDAD", "EMPRESA PROMOTORA DE TIEMPO COMPARTIDO Y MULTIPROPIEDAD", "EMPRESA PROMOTORA Y COMERCIALIZADORA DE TIEMPO COMPARTIDO Y MULTIPROPIEDAD" )
    private val subcat_arr_5 = arrayOf("OPERADOR DE CHIVAS" )
    private val subcat_arr_6 = arrayOf("EMPRESAS CAPTADORAS DE AHORRO PARA VIAJES Y DE SERVICIOS TURÍSTICOS" )
    private val subcat_arr_7 = arrayOf("ALBERGUE (HOSPEDAJE NO PERMANENTE)", "ALOJAMIENTO RURAL (HOSPEDAJE NO PERMANENTE)", "APARTAHOTEL (HOSPEDAJE NO PERMANENTE)", "CENTRO VACACIONAL", "HOSTAL (HOSPEDAJE NO PERMANENTE)", "HOTEL", "REFUGIO (HOSPEDAJE NO PERMANENTE)", "VIVIENDA TURISTICA", "CAMPAMENTO" )
    private val subcat_arr_8 = arrayOf("BAR", "BAR Y RESTAURANTE" )
    private val subcat_arr_9 = arrayOf("GUIA DE TURISMO" )
    private val subcat_arr_10 = arrayOf("OFICINA DE REPRESENTACION TURÍSTICA" )
    private val subcat_arr_11 = arrayOf("OPERADORES PROFESIONALES DE CONGRESOS, FERIAS Y CONVENCIONES" )
    private val subcat_arr_12 = arrayOf("PARQUE TEMATICO" )
    private val subcat_arr_13 = arrayOf("USUARIOS OPERADORES, DESARROLLADORES E INDUSTRIALES EN ZONAS FRANCAS" )

    private val subcat_arr_all = arrayOf(subcat_arr_1 + subcat_arr_2 + subcat_arr_3 + subcat_arr_4 + subcat_arr_5 + subcat_arr_6 + subcat_arr_7 + subcat_arr_8 + subcat_arr_9 + subcat_arr_10 + subcat_arr_11 + subcat_arr_12 + subcat_arr_13)

    private var posCat = ""
    private var posSubCat = ""

    private lateinit var spinnerAddress:Spinner
    private lateinit var spinnerCat:Spinner
    private lateinit var spinnerSubCat:Spinner
    private lateinit var spinnerCityNew:Spinner

    val client = OkHttpClient()

    private val GALLERY = 1
    private val CAMERA = 2

    private var isChecked = false
    private var isNew = false

    private lateinit var label_citySpinner:TextView
    private lateinit var name_field:EditText
    private lateinit var address_field:EditText
    private lateinit var info_addres:EditText
    private lateinit var address_field_1:EditText
    private lateinit var address_field_2:EditText
    private lateinit var address_field_3:EditText

    private lateinit var loading_pb:ProgressBar
    private var photoToSave:File? = null

    var imgToSave:String = ""


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        AppPreferences.init(this)

        spinnerAddress = findViewById(R.id.spinnerAddress)//this.spinnerAddress
        spinnerCat = findViewById(R.id.spinnerCat)//this.spinnerCat
        spinnerSubCat = findViewById(R.id.spinnerSubCat)//this.spinnerCat
        spinnerCityNew = findViewById(R.id.spinnerCityNew)//this.spinnerCityNew
        //this.spinnerCat


        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)


        val aaAddress = ArrayAdapter(this, R.layout.custom_spinner_text, addresType_arr)
        aaAddress.setDropDownViewResource(R.layout.custom_spinner_dropdpwn_item)

        val aaCat = ArrayAdapter(this, R.layout.custom_spinner_text, cat_arr)
        aaCat.setDropDownViewResource(R.layout.custom_spinner_dropdpwn_item)

        val aaCity = ArrayAdapter(this, R.layout.custom_spinner_text, cityUser)
        aaCat.setDropDownViewResource(R.layout.custom_spinner_dropdpwn_item)

        spinnerAddress.setAdapter(aaAddress)
        spinnerCat.setAdapter(aaCat)
        spinnerCityNew.setAdapter(aaCity)




        val btnSave = findViewById<TextView>(R.id.btnSave)
        val btn_back = findViewById<ImageView>(R.id.btn_back)
        val btn_cam = findViewById<ImageView>(R.id.btn_cam)
        val check = findViewById<CheckBox>(R.id.check)

        name_field = findViewById(R.id.name_field)
        address_field = findViewById(R.id.address_field)
        info_addres = findViewById(R.id.info_addres)
        address_field_1 = findViewById(R.id.address_field_1)
        address_field_2 = findViewById(R.id.address_field_2)
        address_field_3 = findViewById(R.id.address_field_3)

        label_citySpinner = findViewById(R.id.label_citySpinner)

        loading_pb = findViewById(R.id.loading)





        //LISTENER CHECK
        check.setOnClickListener(View.OnClickListener { view ->
            toogleEnabled()

        })

        val onClickListener : View.OnClickListener = View.OnClickListener { view ->
            when(view.id)
            {
                R.id.btn_back -> gotoBack()
                R.id.btnSave -> openAlert()
            }
        }



        btn_back.setOnClickListener( onClickListener )
        btnSave.setOnClickListener( onClickListener )


        //ALERT PHOTO
        val onTakePhotos:View.OnClickListener = View.OnClickListener { view ->
            takePhotoFromCamera()

        }

        btn_cam.setOnClickListener(onTakePhotos)




        val bundle = intent.extras
        val data = bundle.getBoolean ("isNew")
        isNew = data

        if(!isNew)
        {
            spinnerCityNew.visibility = View.GONE
            label_citySpinner.visibility = View.GONE

            spinnerCat.isEnabled        = false
            spinnerSubCat.isEnabled     = false
            spinnerAddress.isEnabled    = false

            imgToSave = imgSelected

            spinnerCat.setSelection(catSelected.toInt()-1)
            val _subCatArr = subCatSelected.split("-")
            val _sCat = _subCatArr[1].toInt()-1


            val aaSubCat = ArrayAdapter(this, R.layout.custom_spinner_text, subcat_arr_all[0])
            aaSubCat.setDropDownViewResource(R.layout.custom_spinner_dropdpwn_item)
            spinnerSubCat.setAdapter(aaSubCat)



            spinnerSubCat.setSelection(_sCat)

            name_field.setText(nameSelected)
            address_field.setText(addressSelected)

            if(updateSelected=="1")
                btnSave.visibility = View.GONE


        }else{



            check.visibility = View.GONE
            name_field.setText("")
            address_field.setText("")

            spinnerCat.isEnabled        = true
            spinnerSubCat.isEnabled     = true
            spinnerAddress.isEnabled    = true

            address_field.isEnabled     = true
            name_field.isEnabled        = true
            info_addres.isEnabled       = true
            address_field_1.isEnabled   = true
            address_field_2.isEnabled   = true
            address_field_3.isEnabled   = true

            idSelected                  = ""
            rntSelected                 = ""
            nameSelected                = ""
            addressSelected             = ""
            statusSelected              = ""


            stateSelected               = stateUser
            citySelected                = ""




        }

        val allInfo_txt:TextView = findViewById(R.id.allInfo_txt)
        allInfo_txt.text =
                rntSelected+"\n"+
                statusSelected +"\n"+
                stateSelected +"\n"+
                citySelected


        Timer("SettingUp", false).schedule(1000) { setSpinner() }



    }

    private fun setSpinner()
    {
        spinnerCat.setOnItemSelectedListener(this)
    }



    private fun toogleEnabled()
    {
        isChecked = !isChecked

        name_field.isEnabled        = isChecked
        info_addres.isEnabled       = isChecked
        address_field_1.isEnabled   = isChecked
        address_field_2.isEnabled   = isChecked
        address_field_3.isEnabled   = isChecked
        spinnerAddress.isEnabled    = isChecked
        spinnerCat.isEnabled        = isChecked
        spinnerSubCat.isEnabled     = isChecked



    }



    //TAKE PHOTO
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        val thumbnail = data!!.extras!!.get("data") as Bitmap
        saveImage(thumbnail)
        Toast.makeText(this, "¡Imágen guardada!", Toast.LENGTH_SHORT).show()

    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {

            imgToSave = (Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"

            val f = File(wallpaperDirectory, imgToSave)
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()
            savePhoto(f, imgToSave)

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/MuestraMaestra"
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }


    private fun saveMe(_myLat:Double, _myLon:Double)
    {
        loading_pb.visibility = View.VISIBLE


        val news_txt:EditText = findViewById(R.id.news_txt)
        val newsInfo = news_txt.text

        var isCheckNum:String
        if(isChecked)
            isCheckNum = "1"
        else
            isCheckNum = "0"

        val _date = getCurrentDateTime()
        val dateInString = _date.toString("yyyy-MM-dd")


        val json = """
            {
                "idanterior":"${idSelected}",
                "rnt":"${rntSelected}",
                "nombre":"${nameSelected}",
                "direccion":"${addressSelected}",
                "categoria":"${posCat}",
                "subcategoria":"${posSubCat}",
                "latitud":"${_myLat}",
                "longitud":"${_myLon}",
                "departamento":"${stateSelected}",
                "ciudad":"${citySelected}",
                "novedad":"${newsInfo}",
                "correcto":"${isCheckNum}",
                "foto":"${imgToSave}",
                "fecha_censo":"${dateInString}"

            }
            """.trimIndent()





        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        val request = Request.Builder()
                .url(path + serviceSaveForm + idUser)
                .post(body)
                .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException)
            {
                //loading_pb.visibility = View.GONE
                println(e)
                runOnUiThread {
                    visited_places.add(idSelected)
                    AppPreferences.spJsonSaved += json +"|"
                    AppPreferences.spPlaceVisited = visited_places.toString()
                    openAlertAfterSave(false)
                }
            }
            override fun onResponse(call: Call?, response: Response) {
                if (!response.isSuccessful) {
                    System.err.println("Response not successful")
                    return
                }


                val json = response.body()!!.string()
                runOnUiThread {
                    infoLog(json)
                }
            }
            //override fun onResponse(call: Call, response: Response) = gotoLog(response)
        })


    }


    private fun savePhoto(_photo:File, _name:String)
    {
        val MEDIA_TYPE_PNG = MediaType.parse("image/png");

        val req =  MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("userid", idUser)
                .addFormDataPart("file",_name+".png", RequestBody.create(MEDIA_TYPE_PNG, _photo)).build();


        val request = Request.Builder()
                .url(path + serviceSavePhoto)
                .post(req)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException)
            {
                //loading_pb.visibility = View.GONE
                println(e)
                runOnUiThread {
                }
            }
            override fun onResponse(call: Call?, response: Response) {
                if (!response.isSuccessful) {
                    System.err.println("Response not successful")
                    return
                }


                val json = response.body()!!.string()
                runOnUiThread {

                }
            }
            //override fun onResponse(call: Call, response: Response) = gotoLog(response)
        })


    }



    private fun infoLog(_str:String)
    {
        loading_pb.visibility = View.GONE
        val jsonString = StringBuilder(_str)

        val parser = Parser()
        val json = parser.parse(jsonString) as JsonObject

        if(json["status"].toString() == "Successful")
        {
            openAlertAfterSave(true)
        }
        else
        {

            openAlertAfterSave(false)
        }

    }


    private fun gotoBack()
    {
        val i = Intent(this, MapsActivity::class.java)
        startActivity(i)

        finish()
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long)
    {

        posCat = (position+1).toString()


        var useIt = arrayOf("")
        if(position==0)
            useIt = subcat_arr_1
        if(position==1)
            useIt = subcat_arr_2
        if(position==2)
            useIt = subcat_arr_3
        if(position==3)
            useIt = subcat_arr_4
        if(position==4)
            useIt = subcat_arr_5
        if(position==5)
            useIt = subcat_arr_6
        if(position==6)
            useIt = subcat_arr_7
        if(position==7)
            useIt = subcat_arr_8
        if(position==8)
            useIt = subcat_arr_9
        if(position==9)
            useIt = subcat_arr_10
        if(position==10)
            useIt = subcat_arr_11
        if(position==11)
            useIt = subcat_arr_12
        if(position==12)
            useIt = subcat_arr_13


        val aaSubCat = ArrayAdapter(this, R.layout.custom_spinner_text, useIt)
        aaSubCat.setDropDownViewResource(R.layout.custom_spinner_dropdpwn_item)

        spinnerSubCat.setAdapter(aaSubCat)
        spinnerSubCat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                posSubCat = posCat +"-"+(position+1)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

    }

    private fun openAlert()
    {
        loading_pb.visibility = View.GONE

        val builder = android.app.AlertDialog.Builder(this@FormActivity)
        builder.setTitle("Espera")
        builder.setMessage("¿Quieres usar tus cordenadas actuales?")

        builder.setPositiveButton("USAR"){dialog, which ->
            saveMe(myLat, myLon)
        }
        builder.setNegativeButton("NO USAR"){dialog, which ->
            saveMe(0.0,0.0)
        }

        val dialog: android.app.AlertDialog = builder.create()
        dialog.show()
    }
    private fun openAlertAfterSave(isSaved:Boolean)
    {
        loading_pb.visibility = View.GONE

        val builder = android.app.AlertDialog.Builder(this@FormActivity)
        builder.setTitle("¡Bien!")
        if(isSaved)
        {
            builder.setMessage("El formulario fue guardado con exito")
        }else{
            builder.setMessage("El formulario no fue guardado, está en la sección Sincronizar")
        }

        builder.setNeutralButton("OK"){dialog, which ->
            dialog.dismiss()
            val i = Intent(this, MapsActivity::class.java)
            startActivity(i)

            finish()
        }

        runOnUiThread {
            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}
