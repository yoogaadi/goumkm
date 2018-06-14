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
import com.goumkm.yoga.go_umkm.adapter.NoScrollListView
import com.goumkm.yoga.go_umkm.adapter.list_with_image

class UmkmActivity : BaseActivity() {
    internal lateinit var adapter: list_with_image
    internal lateinit var lv: NoScrollListView

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null
    var layoutKosong : LinearLayout? = null
    var btnProduk : LinearLayout? = null
    var btnPromosi : LinearLayout? = null
    var layoutUMKM : ScrollView? = null

    var namaUMKM : TextView? = null
    var deskripsiUMKM : TextView? = null
    var textEdit : TextView? = null

    internal var gambar = IntArray(2)
    internal var namaMenu = arrayOf("Daftar Produk", "Daftar Promosi")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_umkm)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.title = "UMKM Saya"

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        layoutKosong = findViewById<LinearLayout>(R.id.layout_empty)
        layoutUMKM = findViewById<ScrollView>(R.id.layout_umkm)
        namaUMKM = findViewById<TextView>(R.id.text_nama)
        deskripsiUMKM = findViewById<TextView>(R.id.text_deskripsi)
        textEdit = findViewById(R.id.text_edit)
        //btnPromosi = findViewById<LinearLayout>(R.id.btn_promosi)

        lv = findViewById(R.id.list_menu)

        gambar[0] = R.drawable.ic_style_black_24dp
        gambar[1] = R.drawable.ic_assignment_black_24dp

        adapter = list_with_image(this, namaMenu, gambar)
        lv.adapter = adapter
        lv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val intent: Intent
            if (i == 0) {
                val intent = Intent(this,DaftarProduk::class.java)
                startActivity(intent)
            } else if (i == 1) {
                val intent = Intent(this,DaftarPromosi::class.java)
                startActivity(intent)
            }
        }

//        btnProduk!!.setOnClickListener {
//            val intent = Intent(this,DaftarProduk::class.java)
//            startActivity(intent)
//        }


        val btnBuat = findViewById<Button>(R.id.btn_buat)
        val id_user = mAuth!!.currentUser!!.uid
        readUMKM(id_user)

        btnBuat.setOnClickListener {
            val intent = Intent(this,TambahUMKM::class.java)
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
                if (dataSnapshot.value != null) {

                    val id_umkm = dataSnapshot.child("id_umkm").getValue(String::class.java)
                    textEdit!!.setOnClickListener {
                        val intent = Intent(applicationContext,TambahUMKM::class.java)
                        intent.putExtra("id_umkm",id_umkm)
                        intent.putExtra("edit",true)
                        startActivity(intent)

                    }
                    readData(id_umkm)

                }else{
                    layoutKosong!!.visibility = LinearLayout.VISIBLE;
                    hideProgressDialog()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                hideProgressDialog()

                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }

    fun readData(id_umkm : String?){
        myRef = database.getReference("umkm").child(id_umkm)
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {
                    layoutUMKM!!.visibility = LinearLayout.VISIBLE;

                    namaUMKM!!.text = dataSnapshot.child("nama_umkm").getValue(String::class.java)
                    deskripsiUMKM!!.text = dataSnapshot.child("deskripsi").getValue(String::class.java)

                }else{
                    layoutKosong!!.visibility = LinearLayout.VISIBLE;
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var `in`: Intent? = null
        when (item.itemId) {

            android.R.id.home -> {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                return false
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}
