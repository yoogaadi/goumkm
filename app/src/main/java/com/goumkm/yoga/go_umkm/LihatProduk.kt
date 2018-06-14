package com.goumkm.yoga.go_umkm

import android.content.Intent
import android.media.Rating
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.goumkm.yoga.go_umkm.adapter.NumberControl
import com.goumkm.yoga.go_umkm.controller.control_rating
import com.squareup.picasso.Picasso
import java.util.Arrays.asList
import com.stepstone.apprating.AppRatingDialog
import java.util.*
import com.stepstone.apprating.listener.RatingDialogListener
import kotlinx.android.synthetic.main.activity_lihat_produk.*
import java.sql.Timestamp


class LihatProduk : BaseActivity(), RatingDialogListener {


    var textNama : TextView? = null;
    var textDeskripsi : TextView? = null;
    var textHarga : TextView? = null;
    var imageProduk : ImageView? = null;
    var textRating : TextView? = null;
    var text_toko : TextView? = null;
    var RatingBar : RatingBar? = null;
    var id_rating : String? = null

    var jmlRating : Int? = 0
    var totalRating : Int? = 0
    var hasilRating : Double? = 0.0

    var rated : Int? = 0
    var commentRating : String? = null

    var hasRated : Boolean? = false

    lateinit var id_produk: String;

    val nc = NumberControl();

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_produk)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.title = "Lihat Produk"
        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        textNama = findViewById(R.id.text_nama)
        textDeskripsi= findViewById(R.id.text_deskripsi)
        textHarga= findViewById(R.id.text_harga)
        textRating= findViewById(R.id.text_rating)
        text_toko= findViewById(R.id.text_toko)
        RatingBar= findViewById(R.id.rating_bar)
        imageProduk= findViewById(R.id.imageProduk)

        readRating()

        val btnRating = findViewById<Button>(R.id.btn_rating)
        btnRating!!.setOnClickListener {
            showDialog()
        }


        id_produk = intent.extras.getString("id_produk")
        readData(id_produk)
        isRated()
        readUMKM(id_produk)

    }

    fun readUMKM(id_produk: String) {
        showProgressDialog()
        myRef = database.getReference("detail_produk")
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {
                    val datas = dataSnapshot.children
                    for (d in datas) {
                        if(d.child(id_produk).value.toString().equals("true"))
                        {
                            val id_umkm = d.key.toString()
                            readDataUmkm(id_umkm)
                        }
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

    fun readDataUmkm(id_umkm : String){
        showProgressDialog()

        myRef = database.getReference("umkm").child(id_umkm)
        myRef!!.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                hideProgressDialog()

                Log.w("Error", "Failed to read value.", p0!!.toException())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.value != null) {

                    text_toko!!.text = p0.child("nama_umkm").value.toString()
                    text_toko!!.setOnClickListener {
                        val intent = Intent(applicationContext,LihatToko::class.java)
                        intent.putExtra("id_umkm",id_umkm)
                        startActivity(intent)
                    }

                }
                hideProgressDialog()

            }

        })
    }

    fun readData(id_produk : String) {
        showProgressDialog("Loading")
        myRef = database.getReference("item").child(id_produk)
        myRef!!.keepSynced(true);
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.value != null) {

                    Picasso.get()
                            .load(dataSnapshot.child("image_path").getValue().toString())
                            .placeholder(R.drawable.baseline_photo_camera_black_36dp)
                            .error(R.drawable.baseline_photo_camera_black_36dp)
                            .resize(400, 200)
                            .centerCrop()
                            .into(imageProduk)

                    textNama!!.setText(dataSnapshot.child("nama_produk").value.toString())
                    val harga = java.lang.Double.parseDouble(dataSnapshot.child("harga_produk").value.toString())
                    textHarga!!.setText(nc.getNumberFormat(harga))
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

    override fun onNegativeButtonClicked() {

    }

    override fun onNeutralButtonClicked() {

    }



    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        if(!comment.equals("")) {
            if(hasRated!!){
                myRef = database.getReference("ratings").child(id_rating)

            }else{
                myRef = database.getReference("ratings").push()

            }
            val timestamp = Timestamp(System.currentTimeMillis())

            val rating = control_rating()
            rating.comment = comment
            rating.id_produk = id_produk
            rating.id_user = mAuth!!.currentUser!!.uid
            rating.timestamp = timestamp.time
            rating.rating = rate
            myRef!!.setValue(rating)

            setToast("Rating tersimpan")
            readRating()
        }else{
            setToast("Harap isi rating")
        }
    }

    fun isRated(){
        myRef = database.getReference("ratings")
        myRef!!.keepSynced(true)
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.value != null) {
                    val datas = dataSnapshot.children
                    for (d in datas) {

                        val id_prod = d.child("id_produk").value.toString()
                        val id_use = d.child("id_user").value.toString()
                        if(id_produk.equals(id_prod) && mAuth!!.currentUser!!.uid.equals(id_use)){
                            id_rating = d.key.toString()
                            rated = d.child("rating").value.toString().toInt()
                            commentRating = d.child("comment").value.toString()
                            hasRated = true
                            btn_rating.text = "Ubah Rating"
                        }
                    }

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

    fun readRating(){
        jmlRating = 0
        totalRating = 0
        hasilRating = 0.0
        showProgressDialog("Loading")
        myRef = database.getReference("ratings")
        myRef!!.keepSynced(true)
        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {
                    val datas = dataSnapshot.children
                    for (d in datas) {
                        val rate = d.child("rating").value.toString().toInt()

                        val id_prod = d.child("id_produk").value.toString()
                        if(id_produk.equals(id_prod)){
                            jmlRating = jmlRating!!.plus(1)
                            totalRating = totalRating!!.plus(rate)
                        }
                    }

                    if(jmlRating!! > 0){
                        hasilRating = totalRating!!.div(jmlRating!!.toDouble())
                        RatingBar!!.rating = hasilRating!!.toFloat()
                        val nc = NumberControl();
                        text_rating.setText(nc.getNumberFormatNoCurrency(hasilRating!!,1))
                    }else{

                    }




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



    override fun onBackPressed() {
        back()
        super.onBackPressed()
    }

    fun back(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    private fun showDialog() {
        var rate : Int? = 2;
        var comm : String? = " ";
        if(hasRated!!){
            comm = commentRating!!
            rate = rated!!
        }
        AppRatingDialog.Builder()
                .setPositiveButtonText("Simpan")
                .setNegativeButtonText("Batal")
                .setNeutralButtonText("Lain Kali")
                .setNumberOfStars(5)
//                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(rate!!)
                .setTitle("Berikan Rating")
                .setDescription("Berikan rating serta feedback")
                .setDefaultComment(comm!!)
                .setStarColor(R.color.colorAccent)
//                .setNoteDescriptionTextColor(R.color.noteDescriptionTextColor)
//                .setTitleTextColor(R.color.titleTextColor)
//                .setDescriptionTextColor(R.color.contentTextColor)
                .setHint("Berikan deskripsi rating disini")
//                .setHintTextColor(R.color.hintTextColor)
//                .setCommentTextColor(R.color.commentTextColor)
                .setCommentBackgroundColor(R.color.grey_100)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(this)
                .show()
    }
}
