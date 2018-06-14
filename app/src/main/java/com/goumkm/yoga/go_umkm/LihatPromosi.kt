package com.goumkm.yoga.go_umkm

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.goumkm.yoga.go_umkm.adapter.string_control
import com.squareup.picasso.Picasso

class LihatPromosi : BaseActivity() {
    lateinit var id_promosi :String
    lateinit var textNama:TextView
    lateinit var textDeskripsi :TextView
    lateinit var textTanggal :TextView
    lateinit var imagePromosi :ImageView

    val sc = string_control();

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_promosi)

        id_promosi = intent.extras.getString("id_promosi")
        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.title = "Lihat Promosi"

        textNama = findViewById(R.id.text_nama)
        textDeskripsi = findViewById(R.id.text_deskripsi)
        textTanggal = findViewById(R.id.text_tanggal)
        imagePromosi = findViewById(R.id.image_promosi)


        readData(id_promosi)

    }
    fun readData(id_promosi : String) {
        showProgressDialog("Loading")
        myRef = database.getReference("promosi").child(id_promosi)
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.value != null) {
                    try {
                        Picasso.get()
                                .load(dataSnapshot.child("gambar_promosi").getValue().toString())
                                .placeholder(R.drawable.baseline_photo_camera_black_36dp)
                                .error(R.drawable.baseline_photo_camera_black_36dp)
                                .into(imagePromosi)
                    }catch (e:Exception){
                        setToast(e.toString())
                    }
                    var tanggal = sc.getWaktuByTimestamp(dataSnapshot.child("tanggal_mulai").value.toString().toInt())+" Sampai " +
                            sc.getWaktuByTimestamp(dataSnapshot.child("tanggal_selesai").value.toString().toInt())
                    textNama!!.setText(dataSnapshot.child("nama_promosi").value.toString())
                    textTanggal!!.setText(tanggal)
                    textDeskripsi!!.setText(dataSnapshot.child("deskripsi").value.toString())

                } else {

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
