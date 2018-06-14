package com.goumkm.yoga.go_umkm

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.goumkm.yoga.go_umkm.adapter.NumberControl
import com.goumkm.yoga.go_umkm.adapter.list_three_item
import com.goumkm.yoga.go_umkm.adapter.string_control

class DaftarPromosi : BaseActivity() {

    var lv : ListView? = null;
    var textKosong : TextView? = null;
    var adapter: list_three_item? = null
    val nc = NumberControl()
    var loading : ProgressBar? = null;
    var fab : FloatingActionButton? = null;
    var id_umkm : String? = null

    var namaProduk: Array<String>? = null
    var hargaProduk:Array<String>? = null
    var idProduk:Array<String>? = null
    var gambarProduk:Array<String>? = null
    var deskripsiProduk:Array<String>? = null
    var nomorProduk:Array<String>? = null


    val strNama = ArrayList<String>()
    val strHarga = ArrayList<String>()
    val strIdProduk = ArrayList<String>()
    val descProduk = ArrayList<String>()
    val nmrProduk = ArrayList<String>()
    val gbrProduk = ArrayList<String>()

    var nomor = 1;
    var sc = string_control()
    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_produk)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setTitle("Daftar Promosi");

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()


        textKosong = findViewById(R.id.text_kosong)
        loading = findViewById(R.id.loading)
        fab = findViewById(R.id.fab)

        readUMKM(mAuth!!.currentUser!!.uid)


        fab!!.setOnClickListener {
            val intent = Intent(this,TambahPromosi::class.java)
            startActivity(intent)
        }

    }

    fun readUMKM(id_user : String){
        showProgressDialog("Loading")
        myRef = database.getReference("users").child(id_user).child("umkm")
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                loading!!.visibility = ProgressBar.GONE
                if (dataSnapshot.value != null) {
                    id_umkm = dataSnapshot.child("id_umkm").getValue(String::class.java)
                    readData(id_umkm)

                }else{
                    textKosong!!.visibility = TextView.VISIBLE;
                    //hideProgressDialog()
                }
                hideProgressDialog()



            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                hideProgressDialog()

                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }

    fun readData(id_umkm : String?){
        myRef = database.getReference("detail_promosi").child(id_umkm)
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {
                    val datas = dataSnapshot.children
                    for (d in datas) {
                        readPromosi(d.key)
                    }

                    //lv!!.visibility = ListView.VISIBLE
                    //strNama.add(dataSnapshot.toString());

                }else{
                    textKosong!!.visibility = LinearLayout.VISIBLE;
                }

                namaProduk = strNama.toArray(arrayOfNulls<String>(strNama.size))
                //val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, namaProduk);
                hideProgressDialog()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                hideProgressDialog()

                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }

    fun readPromosi(id_promosi : String?){
        myRef = database.getReference("promosi").child(id_promosi)
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {

                    strNama.add(dataSnapshot.child("nama_promosi").value.toString())
                    var tanggal = sc.getWaktuByTimestamp(dataSnapshot.child("tanggal_mulai").value.toString().toInt())+" Sampai " +
                            sc.getWaktuByTimestamp(dataSnapshot.child("tanggal_selesai").value.toString().toInt())
                    strHarga.add(tanggal)
                    descProduk.add("Ditampilkan")
                    gbrProduk.add(dataSnapshot.child("gambar_promosi").value.toString())
                    if(nomor < 10){
                        nmrProduk.add("0"+nomor)

                    }else{
                        nmrProduk.add(""+nomor)
                    }
                    nomor++
                    textKosong!!.visibility = TextView.GONE;
                    //lv!!.visibility = ListView.VISIBLE
                    //strNama.add(dataSnapshot.toString());

                }else{
                    textKosong!!.visibility = LinearLayout.VISIBLE;
                }

                namaProduk = strNama.toArray(arrayOfNulls<String>(strNama.size))
                hargaProduk = strHarga.toArray(arrayOfNulls<String>(strHarga.size))
                idProduk = strIdProduk.toArray(arrayOfNulls<String>(strIdProduk.size))
                gambarProduk = gbrProduk.toArray(arrayOfNulls<String>(gbrProduk.size))
                deskripsiProduk = descProduk.toArray(arrayOfNulls<String>(descProduk.size))
                nomorProduk = nmrProduk.toArray(arrayOfNulls<String>(nmrProduk.size))
                adapter = list_three_item(applicationContext, namaProduk,hargaProduk,deskripsiProduk,gambarProduk);
                lv = findViewById(R.id.list_item)
                lv!!.adapter = adapter
                lv!!.visibility = ListView.VISIBLE

                hideProgressDialog()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                hideProgressDialog()

                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                val intent = Intent(this,UmkmActivity::class.java)
                startActivity(intent)
                return false
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this,UmkmActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }



}
