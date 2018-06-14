package com.goumkm.yoga.go_umkm

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.goumkm.yoga.go_umkm.adapter.NumberControl
import com.goumkm.yoga.go_umkm.adapter.list_grid
import com.goumkm.yoga.go_umkm.adapter.string_control
import kotlinx.android.synthetic.main.activity_lihat_toko.*

class LihatToko : BaseActivity() {
    lateinit var id_umkm :String
    lateinit var textNama: TextView
    lateinit var textDeskripsi : TextView
    lateinit var textJam : TextView
    lateinit var imageToko : ImageView
    lateinit var textHari : TextView
    lateinit var listItem: GridView
    var adapter: list_grid? = null
    val sc = string_control();
    val nc = NumberControl();

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null

    var hari = arrayOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    var namaProduk: Array<String>? = null
    var hargaProduk:Array<String>? = null
    var idProduk:Array<String>? = null
    var gambarProduk:Array<String>? = null

    val strNama = ArrayList<String>()
    val strHarga = ArrayList<String>()
    val strProduk = ArrayList<String>()
    val gbrItem = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_toko)
        try {
            id_umkm = intent.extras.getString("id_umkm")

        }catch (e : Exception){

        }
        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.title = "Lihat Toko"

        textNama = findViewById(R.id.text_nama)
        textDeskripsi = findViewById(R.id.text_deskripsi)
        textJam = findViewById(R.id.text_jam)
        textHari = findViewById(R.id.text_hari)
        imageToko = findViewById(R.id.image_toko)
        imageToko = findViewById(R.id.image_toko)
        listItem = findViewById(R.id.list_item)
        readDataUmkm(id_umkm)

        readUMKM(id_umkm)

    }

    fun readDataUmkm(id_umkm : String){
        showProgressDialog()

        myRef = database.getReference("umkm").child(id_umkm)
        myRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                hideProgressDialog()

                Log.w("Error", "Failed to read value.", p0!!.toException())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.value != null) {

                    textNama!!.text = p0.child("nama_umkm").value.toString()
                    textDeskripsi!!.text = p0.child("deskripsi").value.toString()
                    val jam_buka =  p0.child("jam_buka").value.toString()+".00 - "+ p0.child("jam_tutup").value.toString()+".00"
                    textJam!!.text = "Jam : "+jam_buka
                    var hari_buka = "";
                    if(p0.child("hari_buka").value.toString().equals("8") && p0.child("hari_tutup").value.toString().equals("8")){
                        hari_buka = "Buka setiap hari"
                    }else{
                        val buka = hari[p0.child("hari_buka").value.toString().toInt().minus(1)]
                        val tutup = hari[p0.child("hari_buka").value.toString().toInt().minus(1)]
                        hari_buka = "Buka : "+buka+" - "+tutup
                    }
                    textHari!!.text = hari_buka


                }
                hideProgressDialog()

            }

        })
    }
    fun readUMKM(id_umkm: String) {
        showProgressDialog()
        myRef = database.getReference("detail_produk").child(id_umkm)
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {
                    val datas = dataSnapshot.children
                    for (d in datas) {
                        readAllData(d.key.toString())
                    }
                } else {
                    //hideProgressDialog()
                }
                hideProgressDialog()
            }

            override fun onCancelled(error: DatabaseError) {
                hideProgressDialog()

                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }

    fun readAllData(id_produk: String){
        myRef = database.getReference("item").child(id_produk)
        myRef!!.keepSynced(true);


        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {

                        var imageUrl = dataSnapshot.child("image_path").getValue().toString()
                        strNama.add(dataSnapshot.child("nama_produk").getValue().toString())
                        strHarga.add(nc.getNumberFormat(java.lang.Double.parseDouble(dataSnapshot.child("harga_produk").getValue().toString())))
                        strProduk.add(dataSnapshot.key.toString())
                        gbrItem.add(imageUrl)


                    namaProduk = strNama.toArray(arrayOfNulls<String>(strNama.size))
                    hargaProduk = strHarga.toArray(arrayOfNulls<String>(strHarga.size))
                    idProduk = strProduk.toArray(arrayOfNulls<String>(strProduk.size))
                    gambarProduk = gbrItem.toArray(arrayOfNulls<String>(gbrItem.size))

                    try {

                        adapter = list_grid(applicationContext, namaProduk, hargaProduk, gambarProduk)
//                        layout_data!!.visibility = LinearLayout.VISIBLE
                        list_item!!.setAdapter(adapter)
                        list_item!!.setOnItemClickListener { parent, view, position, id ->
                            val intent = Intent(applicationContext, LihatProduk::class.java)
                            intent.putExtra("id_produk", strProduk.get(position))
                            startActivity(intent)
                        }
                    }catch (ex : Exception){

                    }

                }else{
                    //textKosong!!.visibility = TextView.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }
}
