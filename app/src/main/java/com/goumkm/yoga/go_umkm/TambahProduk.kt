package com.goumkm.yoga.go_umkm

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.goumkm.yoga.go_umkm.controller.control_produk

import android.R.attr.bitmap
import android.app.Activity
import android.content.Context
import android.provider.MediaStore
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v4.app.ActivityCompat
import android.widget.*
import java.io.File
import java.io.IOException
import android.provider.MediaStore.Images
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class TambahProduk : BaseActivity() {

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null
    var storage = FirebaseStorage.getInstance("gs://data-skripsi.appspot.com")
    var storageRef : StorageReference? = null
    var path :String? = ""

    var bitmap : Bitmap? = null

    private val PERMISSION_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 99
    var textProduk: EditText? = null
    var textHarga: EditText? = null
    var textDeskripsi: EditText? = null
    var gambarnya: ImageView? = null
    var ditampilkan: Switch? = null
    var id_umkm : String? = null
    var btnSimpan: Button? = null

    var id_produk : String? = null


    var isEdit : Boolean = false
    var lokasiGambar : String? = null
    var gantiGambar : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_produk)

        storageRef = storage.reference

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setTitle("Tambah Produk");

        if(!checkPermission()){
            requestPermission()
        }

        textProduk =  findViewById<EditText>(R.id.text_nama)
        textHarga =  findViewById<EditText>(R.id.text_harga)
        textDeskripsi =  findViewById<EditText>(R.id.text_deskripsi)
        ditampilkan =  findViewById<Switch>(R.id.ditampilkan)
        gambarnya =  findViewById<ImageView>(R.id.gambarnya)
        btnSimpan =  findViewById<Button>(R.id.btn_simpan)

        val btn_pilih = findViewById<Button>(R.id.btn_pilih)

        btn_pilih.setOnClickListener {
            showFileChooser()
        }

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        readUMKM(mAuth!!.currentUser!!.uid)

        edit()

        btnSimpan!!.setOnClickListener {
            if(textProduk!!.text.toString() != "" &&
                    textHarga!!.text.toString() != "" &&
                    textDeskripsi!!.text.toString() != "") {

                    uploadToFirebase()
            }
        }
    }

    fun edit(){
        if(intent.hasExtra("id_produk")){
            id_produk = intent.extras.getString("id_produk")
            supportActionBar!!.setTitle("Ubah data produk")
            isEdit = true;
            showProgressDialog("Loading")

            myRef = database.getReference("item").child(id_produk)
            myRef!!.keepSynced(true);
            myRef!!.addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(data: DataSnapshot) {
                    if (data.value != null) {

                        textProduk!!.setText(data.child("nama_produk").getValue(String::class.java))
                        textHarga!!.setText(data.child("harga_produk").getValue(Int::class.java).toString())
                        textDeskripsi!!.setText(data.child("deskripsi").getValue(String::class.java))
                        lokasiGambar = data.child("image_path").getValue(String::class.java)
                        try {
                            gambarnya!!.visibility = ImageView.VISIBLE
                            Picasso.get()
                                    .load(data.child("image_path").getValue(String::class.java))
                                    .placeholder(R.drawable.baseline_photo_camera_black_36dp)
                                    .error(R.drawable.baseline_photo_camera_black_36dp)
                                    .resize(300, 300)
                                    .centerCrop()
                                    .into(gambarnya)
                        } catch (e: Exception) {

                        }

                    }else{

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
                    id_umkm = dataSnapshot.child("id_umkm").getValue(String::class.java)

                }else{

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
    }

    fun back(){
        val intent = Intent(this,DaftarProduk::class.java)
        startActivity(intent)
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, " Please allow this permission in App Settings.", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            return false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted Successfully! ", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission Denied :( ", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            try {
                //Getting the Bitmap from Gallery
                //bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                gambarnya!!.setImageBitmap(bitmap)
                gambarnya!!.visibility = ImageView.VISIBLE

                val tempUri = getImageUri(applicationContext,bitmap!! )
                val finalFile = File(getRealPathFromURI(tempUri))
                path = finalFile.toString()
                //Toast.makeText(this, finalFile.toString()+" data", Toast.LENGTH_SHORT).show()


            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    fun uploadToFirebase() {
        val file: Uri = Uri.fromFile(File(path))
        val riversRef: StorageReference = storageRef!!.child("gambar_produk/" + file.getLastPathSegment());
        val uploadTask = riversRef.putFile(file);
        uploadTask.addOnFailureListener {
            simpanData()
        }
        uploadTask.addOnSuccessListener {
            riversRef.downloadUrl.addOnSuccessListener {
                lokasiGambar = it.toString()
                simpanData()
            }
        }
    }
    fun simpanData(){
        val produk = control_produk()
        var status : Int

        if(ditampilkan!!.isSelected){
            status = 1;
        }else{
            status = 0;
        }

        produk.nama_produk = textProduk!!.text.toString();
        produk.harga_produk = java.lang.Double.parseDouble(textHarga!!.text.toString());
        produk.deskripsi = textDeskripsi!!.text.toString()
        produk.status = status
        produk.image_path = lokasiGambar

        if(isEdit){
            myRef = database.getReference("item").child(id_produk)
        }else{
            myRef = database.getReference("item").push()
            id_produk = myRef!!.key

        }
        myRef!!.setValue(produk)
        myRef = database.getReference("detail_produk").child(id_umkm)
        myRef!!.child(id_produk).setValue(true)

        setToast("Data berhasil disimpan")
        back()
    }
}
