package com.goumkm.yoga.go_umkm.fragment


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

import com.goumkm.yoga.go_umkm.R
import com.goumkm.yoga.go_umkm.LihatProduk
import com.goumkm.yoga.go_umkm.adapter.NumberControl
import com.goumkm.yoga.go_umkm.adapter.list_grid
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.sqrt
import com.google.firebase.storage.StorageReference
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.goumkm.yoga.go_umkm.FilterProduk
import com.goumkm.yoga.go_umkm.adapter.valueComparator


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentHome.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentHome : BaseFragment() {

    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase
    private var mAuth: FirebaseAuth? = null
    var list_item: GridView? = null
    var list_produk: GridView? = null
    var layout_data: LinearLayout? = null
    var layout_rekomendasi: LinearLayout? = null
    var adapter: list_grid? = null
    var text_search: EditText? = null
    var btn_search: ImageButton? = null

    var namaProduk: Array<String>? = null
    var hargaProduk:Array<String>? = null
    var idProduk:Array<String>? = null
    var gambarProduk:Array<String>? = null

    var namaProduk2: Array<String>? = null
    var hargaProduk2:Array<String>? = null
    var idProduk2:Array<String>? = null
    var gambarProduk2:Array<String>? = null

    var textKosong : TextView? = null
    var loading : ProgressBar? = null

    var arrayProduk = arrayOf<String>()

    val rataRataRating: HashMap<String, Double>? = HashMap()
    lateinit var detailRating: HashMap<String, Int>
    val userRating: HashMap<String, Map<String,Int>>? = HashMap()

    val dataSimilarity : HashMap<HashMap<String,String>,Double>? = HashMap()

    var storage = FirebaseStorage.getInstance("gs://data-skripsi.appspot.com")
    var storageRef : StorageReference? = null

    lateinit var btnKuesioner : Button

    val nc = NumberControl()
    var gambar = arrayOf(R.drawable.baseline_home_black_24dp, R.drawable.baseline_home_black_24dp, R.drawable.baseline_home_black_24dp, R.drawable.baseline_home_black_24dp, R.drawable.baseline_home_black_24dp,R.drawable.baseline_home_black_24dp, R.drawable.baseline_home_black_24dp,R.drawable.baseline_home_black_24dp, R.drawable.baseline_home_black_24dp)

    var kategori = arrayOf("Kerajinan", "Makanan Olahan Ikan", "Makanan Olahan", "Batik dan Bordir", "Susu", "Sapi", "Kelinci", "Olahan Limbah", "Peternakan")

    fun FragmentHome() {

    }


    fun newInstance(): FragmentHome{
        val args: Bundle = Bundle()
        val fragment = com.goumkm.yoga.go_umkm.fragment.FragmentHome()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        storageRef = storage.reference

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        list_item = view.findViewById(R.id.list_grid)
        list_produk = view.findViewById(R.id.list_produk)
        layout_data = view.findViewById(R.id.layout_data)
        layout_rekomendasi = view.findViewById(R.id.layout_rekomendasi)
        loading = view.findViewById(R.id.progressBar)
        textKosong = view.findViewById(R.id.text_kosong)
        text_search = view.findViewById(R.id.text_search)
        btn_search = view.findViewById(R.id.btn_search)
        btnKuesioner = view.findViewById(R.id.kuesioner)

        btn_search!!.setOnClickListener {
            if(!text_search!!.text.toString().equals("")){
                val intent = Intent(context, FilterProduk::class.java)
                intent.putExtra("kata_cari",text_search!!.text.toString())
                startActivity(intent)
            }else{
                Toast.makeText(context,"Masukkan kata yang akan dicari",Toast.LENGTH_SHORT).show()
            }

        }

        btnKuesioner.setOnClickListener {
            val url = "https://goo.gl/forms/KcwCYelRC5D9tsyg1";
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        val ai = 0;


        readAllItem()

        readAllData()

        return view
    }

    fun rataRataRating(){
        myRef = database.getReference("users")
        myRef!!.keepSynced(true);

        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loading!!.visibility = ProgressBar.GONE
                if (dataSnapshot.value != null) {
                    val datas = dataSnapshot!!.children
                    for (d in datas) {
                        val id_usr = d.key
                        val Ref = database.getReference("ratings")

                        Ref.addValueEventListener(object  : ValueEventListener{

                            override fun onDataChange(p0: DataSnapshot?) {

                                detailRating = HashMap()

                                val datas = p0!!.children
                                var jmlRating = 0
                                var rating = 0
                                var rataRata = 0.0

                                for (d in datas) {
                                    if(d.child("id_user").value.toString().equals(id_usr)){
                                        rating = rating.plus(d.child("rating").value.toString().toInt())
                                        jmlRating = jmlRating.plus(1)
                                        detailRating.put(d.child("id_produk").value.toString(),d.child("rating").value.toString().toInt())
                                    }
                                }
                                if(jmlRating > 0){
                                    rataRata = rating.toDouble()/jmlRating.toDouble()

                                }
                                userRating!!.put(id_usr,detailRating)
                                rataRataRating!!.put(id_usr,rataRata)


                                adjustedCosineSimilarity()
//                                for(data in userRating!!){
//                                    println("datanya2 = "+data.key+" = "+data.value)
//                                }
                            }
                            override fun onCancelled(p0: DatabaseError?) {

                            }
                        })
                    }

                }else{

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }

    fun adjustedCosineSimilarity(){
        for (a in 0..arrayProduk.size.minus(1)) {
            for (o in 0..arrayProduk.size.minus(1)) {
                val dataItemSimilar: HashMap<String, String>? = HashMap()
                var sigmaRatingUser = 0.0
                var akarItemI = 0.0
                var akarItemJ = 0.0
                var isNull = true;
                if(o > a){
                    for(data in userRating!!){
                        if(data.value.containsKey(arrayProduk[a]) && data.value.containsKey(arrayProduk[o])){
                            isNull = false
                            var rataRata = rataRataRating!!.getValue(data.key)
                            val valueRatingItemI = data.value.getValue(arrayProduk[a])
                            val valueRatingItemJ = data.value.getValue(arrayProduk[o])

                            sigmaRatingUser = sigmaRatingUser.plus((valueRatingItemI - rataRata) * (valueRatingItemJ - rataRata))
                            akarItemI = akarItemI.plus(Math.pow((valueRatingItemI - rataRata),2.0))
                            akarItemJ = akarItemJ.plus(Math.pow((valueRatingItemJ - rataRata),2.0))
                        }
                    }
                    if(!isNull) {
                        val total = sigmaRatingUser / (sqrt(akarItemI) * sqrt(akarItemJ))
                        if(total > 0.5){
                            dataItemSimilar!!.put(arrayProduk[a],arrayProduk[o])
                            dataSimilarity!!.put(dataItemSimilar,total)
                            println("data similar : "+arrayProduk[a]+" dan "+arrayProduk[o]+" = "+total)
                        }
                    }
                }
            }
        }
        weighedSum()
    }

    fun weighedSum(){
        val recommendedProduct = HashMap<String,Double>()

        val id_user = mAuth!!.currentUser!!.uid

        for (a in 0..arrayProduk!!.size.minus(1)){
            var himpunanItem = 0.0
            var himpunanSimilar = 0.0
            for(usrRating in userRating!!){

                if(usrRating.key.equals(id_user) && !usrRating.value.containsKey(arrayProduk[a])) {
                    for(dataSimilar in dataSimilarity!!){
                        if(dataSimilar.key.values.contains(arrayProduk[a]) || dataSimilar.key.keys.contains(arrayProduk[a])){
                            for(data in usrRating!!.value.keys){
                                if(dataSimilar.key.values.contains(data) || dataSimilar.key.keys.contains(data)) {
                                    println("dat : "+dataSimilar.key.values.toString() + " = " + dataSimilar.key.keys.toString() + " : " + arrayProduk[a])
                                    //disini sudah ketahuan item apa saja yang menjadi rekomendasi
                                    himpunanItem = himpunanItem.plus(usrRating.value.getValue(data) * dataSimilar.value)
                                    himpunanSimilar = himpunanSimilar.plus(dataSimilar.value)
                                }
                            }
                        }
                    }
                }
            }
            if(himpunanItem>0){
                recommendedProduct!!.put(arrayProduk[a],himpunanItem.div(himpunanSimilar))
                println(arrayProduk[a]+" : "+himpunanItem/himpunanSimilar)
            }
        }
        if(recommendedProduct.size > 0){
            readData(Sorting(recommendedProduct))

        }
    }

    fun Sorting(unsorted : HashMap<String,Double>) : ArrayList<String>{
        val data = ArrayList<String>()
        val comparator = valueComparator(unsorted)
        val result = TreeMap<String, Double>(comparator)
        result.putAll(unsorted)
        for (sorted in result){
            data.add(sorted.key.toString())
            //println("Datanya : "+sorted.key+" : "+sorted.value)
        }
        return data!!
    }

    fun readAllItem(){
        myRef = database.getReference("item")
        myRef!!.keepSynced(true);
        val arrItem = ArrayList<String>()

        myRef!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loading!!.visibility = ProgressBar.GONE
                if (dataSnapshot.value != null) {

                    val datas = dataSnapshot.children
                    for (d in datas) {
                        arrItem.add(d.key.toString())

                    }
                    arrayProduk = arrItem.toArray(arrayOfNulls<String>(arrItem.size))
                    rataRataRating()
                }else{
                    textKosong!!.visibility = TextView.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }

    fun readData(recommended : ArrayList<String>){
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
                        for (recom in recommended){
                            if(d.key.toString().equals(recom)){
                                var imageUrl = d.child("image_path").getValue().toString()
                                strNama.add(d.child("nama_produk").getValue().toString())
                                strHarga.add(nc.getNumberFormat(java.lang.Double.parseDouble(d.child("harga_produk").getValue().toString())))
                                strProduk.add(d.key.toString())
                                gbrItem.add(imageUrl)
                            }
                        }
                    }

                    namaProduk = strNama.toArray(arrayOfNulls<String>(strNama.size))
                    hargaProduk = strHarga.toArray(arrayOfNulls<String>(strHarga.size))
                    idProduk = strProduk.toArray(arrayOfNulls<String>(strProduk.size))
                    gambarProduk = gbrItem.toArray(arrayOfNulls<String>(gbrItem.size))

                    try {

                        adapter = list_grid(context, namaProduk, hargaProduk, gambarProduk)
                        layout_rekomendasi!!.visibility = LinearLayout.VISIBLE
                        list_item!!.setAdapter(adapter)
                        setListViewHeightBasedOnChildren(list_item!!)
                        list_item!!.setOnItemClickListener { parent, view, position, id ->
                            val intent = Intent(context, LihatProduk::class.java)
                            intent.putExtra("id_produk", strProduk.get(position))
                            startActivity(intent)
                        }
                    }catch (ex : Exception){

                    }

                }else{
                    textKosong!!.visibility = TextView.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }
    fun readAllData(){
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
                        var imageUrl = d.child("image_path").getValue().toString()
                        strNama.add(d.child("nama_produk").getValue().toString())
                        strHarga.add(nc.getNumberFormat(java.lang.Double.parseDouble(d.child("harga_produk").getValue().toString())))
                        strProduk.add(d.key.toString())
                        gbrItem.add(imageUrl)

                    }

                    namaProduk2 = strNama.toArray(arrayOfNulls<String>(strNama.size))
                    hargaProduk2 = strHarga.toArray(arrayOfNulls<String>(strHarga.size))
                    idProduk2 = strProduk.toArray(arrayOfNulls<String>(strProduk.size))
                    gambarProduk2 = gbrItem.toArray(arrayOfNulls<String>(gbrItem.size))

                    try {

                        adapter = list_grid(context, namaProduk2, hargaProduk2, gambarProduk2)
                        layout_data!!.visibility = LinearLayout.VISIBLE
                        list_produk!!.setAdapter(adapter)
                        list_produk!!.setOnItemClickListener { parent, view, position, id ->
                            val intent = Intent(context, LihatProduk::class.java)
                            intent.putExtra("id_produk", strProduk.get(position))
                            startActivity(intent)
                        }
                    }catch (ex : Exception){

                    }

                }else{
                    textKosong!!.visibility = TextView.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })
    }


}
