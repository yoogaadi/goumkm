package com.goumkm.yoga.go_umkm

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.goumkm.yoga.go_umkm.adapter.NumberControl
import com.goumkm.yoga.go_umkm.adapter.list_three_item
import android.R.attr.name
import android.content.DialogInterface
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.AdapterView
import android.view.ContextMenu
import android.view.View


class DaftarProduk : BaseActivity() {

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

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_produk)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setTitle("Daftar Produk");

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()


        textKosong = findViewById(R.id.text_kosong)
        loading = findViewById(R.id.loading)
        fab = findViewById(R.id.fab)

        readUMKM(mAuth!!.currentUser!!.uid)


        fab!!.setOnClickListener {
            val intent = Intent(this,TambahProduk::class.java)
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
        myRef = database.getReference("detail_produk").child(id_umkm)
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {
                    val datas = dataSnapshot.children
                    for (d in datas) {
                        readProduk(d.key)
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

    fun readProduk(id_produk : String?){
        myRef = database.getReference("item").child(id_produk)
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {

                    strNama.add(dataSnapshot.child("nama_produk").value.toString())
                    val harga = java.lang.Double.parseDouble(dataSnapshot.child("harga_produk").value.toString())
                    strHarga.add(nc.getNumberFormat(harga))

                    descProduk.add("Ditampilkan")
                    gbrProduk.add(dataSnapshot.child("image_path").value.toString())
                    if(nomor < 10){
                        nmrProduk.add("0"+nomor)

                    }else{
                        nmrProduk.add(""+nomor)
                    }
                    strIdProduk.add(dataSnapshot.key)
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
                registerForContextMenu(lv)

                hideProgressDialog()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                hideProgressDialog()

                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Pilih menu")
        menu.add(0, v.id, 0, "Ubah Data")
        menu.add(0, v.id, 1, "Hapus Data")
        menu.add(0, v.id, 2, "Batal")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        if (item.order == 0) {
            val intent = Intent(applicationContext,TambahProduk::class.java)
            intent.putExtra("id_produk",idProduk!![info.position])
            startActivity(intent)
        } else if (item.order == 1) {
            deleteProduk(idProduk!![info.position])
        }else if(item.order == 2){
            return true
        }
        return true
    }

    fun deleteProduk(id_prod : String){

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
