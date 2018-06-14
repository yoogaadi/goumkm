package com.goumkm.yoga.go_umkm

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.goumkm.yoga.go_umkm.adapter.string_control
import com.goumkm.yoga.go_umkm.controller.control_produk
import com.goumkm.yoga.go_umkm.controller.control_promosi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class TambahPromosi : BaseActivity(), DatePickerDialog.OnDateSetListener {
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val bulan= month.plus(1)
        val tanggal =  sc.getTimestamp(year.toString()+"-"+bulan+"-"+dayOfMonth+" 00:00:00")
        setToast(bulan.toString())
        if(isMulai){
           tanggalMulai = tanggal.toInt()
            btnMulai!!.text = dayOfMonth.toString()+"/"+bulan+"/"+year;
        }else{
            tanggalSelesai = tanggal.toInt()
            btnSelesai!!.text = dayOfMonth.toString()+"/"+bulan+"/"+year;

        }
        //setToast(tanggal)
    }



    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null
    var storage = FirebaseStorage.getInstance("gs://data-skripsi.appspot.com")
    var storageRef : StorageReference? = null
    var path :String? = ""

    var bitmap : Bitmap? = null
    var sc = string_control();

    private val PERMISSION_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 99
    var textPromosi: EditText? = null
    var textDeskripsi: EditText? = null
    var gambarnya: ImageView? = null
    var btnMulai: Button? = null
    var btnSelesai: Button? = null

    var tanggalMulai : Int? = 0
    var tanggalSelesai : Int? = 0

    var ditampilkan: Switch? = null
    var id_umkm : String? = null
    var btnSimpan: Button? = null

    var isMulai = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_promosi)
        storageRef = storage.reference

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setTitle("Tambah Promosi");

        if(!checkPermission()){
            requestPermission()
        }

        textPromosi =  findViewById<EditText>(R.id.text_nama)
        textDeskripsi =  findViewById<EditText>(R.id.text_deskripsi)
        ditampilkan =  findViewById<Switch>(R.id.ditampilkan)
        gambarnya =  findViewById<ImageView>(R.id.gambarnya)
        btnSimpan =  findViewById<Button>(R.id.btn_simpan)
        btnMulai =  findViewById<Button>(R.id.btn_mulai)
        btnSelesai =  findViewById<Button>(R.id.btn_selesai)

        val btn_pilih = findViewById<Button>(R.id.btn_pilih)

        btnMulai!!.setOnClickListener {
            val c : Calendar = Calendar.getInstance();
            var mYear = c.get(Calendar.YEAR);
            var mMonth = c.get(Calendar.MONTH);
            var mDay = c.get(Calendar.DAY_OF_MONTH);

            isMulai = true

            val datePickerDialog : DatePickerDialog = DatePickerDialog(
                    this, this, mYear, mMonth, mDay)
            datePickerDialog.show()
        }
        btnSelesai!!.setOnClickListener {
            val c : Calendar = Calendar.getInstance();
            var mYear = c.get(Calendar.YEAR);
            var mMonth = c.get(Calendar.MONTH);
            var mDay = c.get(Calendar.DAY_OF_MONTH);

            isMulai = false

            val datePickerDialog : DatePickerDialog = DatePickerDialog(
                    this, this, mYear, mMonth, mDay)
            datePickerDialog.show()
        }

        btn_pilih.setOnClickListener {
            showFileChooser()
        }

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        readUMKM(mAuth!!.currentUser!!.uid)

        btnSimpan!!.setOnClickListener {
            if(textPromosi!!.text.toString() != "" &&
                    tanggalMulai!! != 0 &&
                    tanggalSelesai!! != 0 &&
                    textDeskripsi!!.text.toString() != "") {



                uploadToFirebase()




            }
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
        val intent = Intent(this,DaftarPromosi::class.java)
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
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
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
        val riversRef: StorageReference = storageRef!!.child("gambar_promosi/" + file.getLastPathSegment());
        val uploadTask = riversRef.putFile(file);
        uploadTask.addOnFailureListener {
            setToast("gabisa")
        }
        uploadTask.addOnSuccessListener {
            riversRef.downloadUrl.addOnSuccessListener {
                val promosi = control_promosi()
                var status : Int

                if(ditampilkan!!.isSelected){
                    status = 1;
                }else{
                    status = 0;
                }


                val image_location : String = it.toString()

                promosi.nama_promosi= textPromosi!!.text.toString();
                promosi.deskripsi = textDeskripsi!!.text.toString()
                promosi.tanggal_mulai = tanggalMulai
                promosi.tanggal_selesai = tanggalSelesai
                promosi.status = status
                promosi.gambar_promosi = image_location


                myRef = database.getReference("promosi").push()
                myRef!!.setValue(promosi)
                val id_produk = myRef!!.key
                myRef = database.getReference("detail_promosi").child(id_umkm)
                myRef!!.child(id_produk).setValue(true)

                setToast("Data berhasil disimpan")
                back()
            }

        }
    }
}
