package com.goumkm.yoga.go_umkm

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.CompoundButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.goumkm.yoga.go_umkm.controller.control_umkm


class TambahUMKM : BaseActivity(), AdapterView.OnItemSelectedListener {

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null

    var jam = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24")
    var hari = arrayOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    var kategori = arrayOf("Kerajinan", "Makanan Olahan Ikan", "Makanan Olahan", "Batik dan Bordir", "Susu", "Sapi", "Kelinci", "Olahan Limbah", "Peternakan")

    var spinnerJamBuka: Spinner? = null
    var spinnerJamTutup:Spinner? = null
    var spinnerHariBuka:Spinner? = null
    var spinnerHariTutup:Spinner? = null
    var spinnerKluster:Spinner? = null

    var layoutBuka:LinearLayout? = null

    var switchHari : Switch? = null

    var textNamaUMKM: EditText? = null
    var textAlamatUMKM:EditText? = null
    var textTelpUMKM:EditText? = null
    var textDeskripsiUMKM:EditText? = null

    var btnSimpan:Button? = null

    var jamBuka : String? = "1";
    var jamTutup : String? = "1";
    var hariBuka : String? = "0";
    var hariTutup : String? = "0";
    var selectedKategori : String? = "0";

    var isEdit : Boolean = false
    var id_umkm : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_umkm)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setTitle("Tambah UMKM")

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        spinnerJamBuka =  findViewById<Spinner>(R.id.spinner_jam_buka)
        spinnerJamTutup =  findViewById<Spinner>(R.id.spinner_jam_tutup)
        spinnerHariBuka =  findViewById<Spinner>(R.id.spinner_hari_buka)
        spinnerHariTutup =  findViewById<Spinner>(R.id.spinner_hari_tutup)
        spinnerKluster = findViewById<Spinner>(R.id.spinner_klaster)
        textNamaUMKM =  findViewById<EditText>(R.id.text_nama)
        textAlamatUMKM =  findViewById<EditText>(R.id.text_alamat)
        textTelpUMKM =  findViewById<EditText>(R.id.text_telepon)
        textDeskripsiUMKM =  findViewById<EditText>(R.id.text_deskripsi)
        btnSimpan =  findViewById<Button>(R.id.btn_simpan)
        switchHari =  findViewById<Switch>(R.id.switch_setiap_hari)
        layoutBuka =  findViewById<LinearLayout>(R.id.layout_buka)

        val switchState = switchHari!!.isChecked

        if(switchState){
            layoutBuka!!.visibility = LinearLayout.GONE
        }

        switchHari!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            var kata : String = "Checked"
            if(isChecked){
                hariBuka = "8"
                hariTutup = "8"
                layoutBuka!!.visibility = LinearLayout.GONE
            }else{
                layoutBuka!!.visibility = LinearLayout.VISIBLE
            }

        })

        val jamAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, jam)
        val hariAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, hari)
        val klusterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kategori)
        spinnerJamBuka!!.adapter = jamAdapter
        spinnerJamTutup!!.adapter = jamAdapter
        spinnerHariBuka!!.adapter = hariAdapter
        spinnerHariTutup!!.adapter = hariAdapter
        spinnerHariTutup!!.adapter = hariAdapter
        spinnerKluster!!.adapter = klusterAdapter

        spinnerJamBuka!!.setOnItemSelectedListener(this)
        spinnerJamTutup!!.setOnItemSelectedListener(this)
        spinnerHariBuka!!.setOnItemSelectedListener(this)
        spinnerHariTutup!!.setOnItemSelectedListener(this)

        edit()

        btnSimpan!!.setOnClickListener {
            if(textNamaUMKM!!.text.toString() != "" &&
                    textAlamatUMKM!!.text.toString() != "" &&
                    textTelpUMKM!!.text.toString() != "" &&
                    textDeskripsiUMKM!!.text.toString() != ""){
                if(isEdit) {
                    hariBuka = "8"
                    hariTutup = "8"
                }
                val switchState = switchHari!!.isChecked
                val id_user = mAuth!!.currentUser!!.uid
                val umkm = control_umkm()
                umkm!!.nama_umkm = textNamaUMKM!!.text.toString()
                umkm!!.alamat = textAlamatUMKM!!.text.toString()
                umkm!!.telepon = textTelpUMKM!!.text.toString()
                umkm!!.deskripsi = textDeskripsiUMKM!!.text.toString()
                umkm!!.jam_buka = Integer.parseInt(jamBuka)
                umkm!!.jam_tutup = Integer.parseInt(jamTutup)
                umkm!!.hari_buka = Integer.parseInt(hariBuka)
                umkm!!.hari_tutup = Integer.parseInt(hariTutup)
                umkm!!.klaster = Integer.parseInt(selectedKategori)

                if(isEdit){
                    myRef = database.getReference("umkm").child(id_umkm)

                }else{
                    myRef = database.getReference("umkm").push()
                    id_umkm = myRef!!.key;
                }

                myRef!!.setValue(umkm)


                myRef = database.getReference("users").child(id_user).child("umkm")

                myRef!!.child("id_umkm").setValue(id_umkm)

                setToast("Data berhasil disimpan")
                back();



            }else{
                setToast("Harap lengkapi semua data")
            }
        }

    }

    fun edit(){
        if(intent.hasExtra("edit")){
            supportActionBar!!.setTitle("Ubah data UMKM")
            isEdit = intent.extras.getBoolean("edit")
            id_umkm =  intent.extras.getString("id_umkm")

            showProgressDialog("Loading")
            myRef = database.getReference("umkm").child(id_umkm)
            myRef!!.keepSynced(true)
            myRef!!.addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(data: DataSnapshot) {

                    if (data.value != null) {
                        textNamaUMKM!!.setText(data.child("nama_umkm").value.toString())
                        textAlamatUMKM!!.setText(data.child("alamat").value.toString())
                        textTelpUMKM!!.setText(data.child("telepon").value.toString())
                        textDeskripsiUMKM!!.setText(data.child("deskripsi").value.toString())
                        jamBuka = data.child("jam_buka").value.toString().toInt().minus(1).toString()
                        jamTutup = data.child("jam_tutup").value.toString().toInt().minus(1).toString()
                        hariBuka = data.child("hari_buka").value.toString()
                        hariTutup = data.child("hari_tutup").value.toString()
                        selectedKategori =data.child("klaster").value.toString()

                        if(hariBuka.equals("8") && hariTutup.equals("8")){
                            layoutBuka!!.visibility = LinearLayout.GONE
                            switchHari!!.isChecked = true
                            hariBuka = "8";
                            hariTutup = "8";

                        }else{
                            spinnerHariBuka!!.setSelection(hariBuka!!.toInt())
                            spinnerHariTutup!!.setSelection(hariTutup!!.toInt())
                        }

                        spinnerJamBuka!!.setSelection(jamBuka!!.toInt())
                        spinnerJamTutup!!.setSelection(jamTutup!!.toInt())

                        spinnerKluster!!.setSelection(selectedKategori!!.toInt())


                    }
                    hideProgressDialog()


                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    hideProgressDialog()

                    Log.w("Error", "Failed to read value.", error.toException())
                }
            })

            spinnerJamBuka
            spinnerJamTutup
            spinnerHariBuka
            spinnerHariTutup
            spinnerKluster

            btnSimpan
            switchHari
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if(parent!!.id == R.id.spinner_jam_buka){
            jamBuka = jam[position];
        }
        if(parent!!.id == R.id.spinner_jam_tutup){
            jamTutup = jam[position];
        }
        if(parent!!.id == R.id.spinner_hari_buka){
            hariBuka = position.toString();
        }
        if(parent!!.id == R.id.spinner_hari_tutup){
            hariTutup = position.toString();
        }
        if(parent!!.id == R.id.spinner_klaster){
            selectedKategori = position.toString();
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                ShowDialog()
                return false
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        ShowDialog()
    }

    fun back(){
        val intent = Intent(this,UmkmActivity::class.java)
        startActivity(intent)
    }

    fun ShowDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("Batal")
        builder.setMessage("Apakah anda ingin membatalkan?")
        builder.setPositiveButton("Iya", DialogInterface.OnClickListener { dialog, id ->
            back()
        })
                .setNegativeButton("Batal", DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })

        builder.create().show()
    }
}
