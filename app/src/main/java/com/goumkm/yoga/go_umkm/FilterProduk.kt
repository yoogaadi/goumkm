package com.goumkm.yoga.go_umkm

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.goumkm.yoga.go_umkm.adapter.NumberControl
import com.goumkm.yoga.go_umkm.adapter.list_grid

class FilterProduk : BaseActivity() {

    var jenis = "pencarian"
    var kategori = ""
    var str_cari : String? = null

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null

    var list_produk: GridView? = null
    var layout_data: LinearLayout? = null
    var adapter: list_grid? = null
    var text_search: EditText? = null
    var btn_search: ImageButton? = null

    var namaProduk: Array<String>? = null
    var hargaProduk:Array<String>? = null
    var idProduk:Array<String>? = null
    var gambarProduk:Array<String>? = null

    var textKosong : TextView? = null
    var loading : ProgressBar? = null


    val nc = NumberControl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_produk)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        list_produk = findViewById(R.id.list_produk)
        layout_data = findViewById(R.id.layout_data)
        loading = findViewById(R.id.progressBar)
        textKosong = findViewById(R.id.text_kosong)
        text_search = findViewById(R.id.text_search)
        btn_search = findViewById(R.id.btn_search)
        val text_carian =  findViewById<TextView>(R.id.text_carian)

        if(intent.hasExtra("kata_cari")){
            supportActionBar!!.title = "Cari Produk"
            jenis = "pencarian"
            str_cari = intent.extras.getString("kata_cari")
            text_carian.setText("Pencarian Produk : "+str_cari)
            getDataCari()

        }else if(intent.hasExtra("kategori")){
            jenis = "kategori"
            kategori = intent.extras.getString("kategori")
        }

        btn_search!!.setOnClickListener {
            if(!text_search!!.text.toString().equals("")){
                val intent = Intent(this, FilterProduk::class.java)
                intent.putExtra("kata_cari",text_search!!.text.toString())
                startActivity(intent)
            }else{
                Toast.makeText(this,"Masukkan kata yang akan dicari",Toast.LENGTH_SHORT).show()
            }

        }


    }
    fun getDataCari(){
        showProgressDialog()
        myRef = database.getReference("item")
        myRef!!.keepSynced(true);
        val strNama = ArrayList<String>()
        val strHarga = ArrayList<String>()
        val strProduk = ArrayList<String>()
        val gbrItem = ArrayList<String>()

        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loading!!.visibility = ProgressBar.GONE
                if (dataSnapshot.value != null) {

                    val datas = dataSnapshot.children
                    for (d in datas) {
                        if(d.child("nama_produk").getValue().toString().toLowerCase().contains(str_cari!!.toLowerCase())){
                            var imageUrl = d.child("image_path").getValue().toString()
                            strNama.add(d.child("nama_produk").getValue().toString())
                            strHarga.add(nc.getNumberFormat(java.lang.Double.parseDouble(d.child("harga_produk").getValue().toString())))
                            strProduk.add(d.key.toString())
                            gbrItem.add(imageUrl)
                        }else{
                        }
                    }

                    namaProduk = strNama.toArray(arrayOfNulls<String>(strNama.size))
                    hargaProduk = strHarga.toArray(arrayOfNulls<String>(strHarga.size))
                    idProduk = strProduk.toArray(arrayOfNulls<String>(strProduk.size))
                    gambarProduk = gbrItem.toArray(arrayOfNulls<String>(gbrItem.size))
                    loading!!.visibility = TextView.GONE

                    try {

                        adapter = list_grid(applicationContext, namaProduk, hargaProduk, gambarProduk)
                        layout_data!!.visibility = LinearLayout.VISIBLE
                        list_produk!!.setAdapter(adapter)
                        list_produk!!.setOnItemClickListener { parent, view, position, id ->
                            val intent = Intent(applicationContext, LihatProduk::class.java)
                            intent.putExtra("id_produk", strProduk.get(position))
                            startActivity(intent)
                        }
                    }catch (ex : Exception){

                    }
                    hideProgressDialog()
                }else{
                    hideProgressDialog()
                    loading!!.visibility = TextView.GONE

                    textKosong!!.visibility = TextView.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                hideProgressDialog()
                loading!!.visibility = TextView.GONE

                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                back()
                return false
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        back()
        super.onBackPressed()
    }

    fun back(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}
