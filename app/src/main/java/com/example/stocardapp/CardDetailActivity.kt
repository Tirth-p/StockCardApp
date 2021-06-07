package com.example.stocardapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.example.stocardapp.models.DeleteResponse
import com.example.stocardapp.models.ShareResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class CardDetailActivity : AppCompatActivity() {

    var mAPIService: UserApi? = null
    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail2)

        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Card Details")
        val ibk = findViewById<ImageView>(R.id.imgBack)
        val btn_del = findViewById<Button>(R.id.btnCdel)
        var addPay = findViewById<Button>(R.id.btn_pay)
        ibk.setOnClickListener {
            startActivity(Intent(this, CardListActivity::class.java))
        }

       var cid= intent.getIntExtra("cardId", 0)

        addPay.setOnClickListener {
            val  intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("cardId",cid)
            startActivity(intent)
        }

        mAPIService = ApiUtils.apiService

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        token = "Bearer " + sharedPreference.getString("token", "defaultName")

        var cnm = intent.getStringExtra("cardNm")
        var cnum = intent.getStringExtra("cardNum")
        var crwd = intent.getStringExtra("cardRwd")
        var cdtl = intent.getStringExtra("cardDetl")
        var cdt = intent.getStringExtra("cardDt")
        var im = intent.getStringExtra("cardImg")

        var getNm = findViewById<EditText>(R.id.getcardNm)
        var getNum = findViewById<EditText>(R.id.getcardNum)
        var getRwd = findViewById<EditText>(R.id.getcardRwd)
        var getDtl = findViewById<EditText>(R.id.getcardDtl)
        var getDt = findViewById<EditText>(R.id.getcardDate)
        var cdIm = findViewById<ImageView>(R.id.cdImg)

        var u:Uri= Uri.parse(im)
        cdIm.load(im.toString())
        getNm.setText(cnm)
        getNum.setText(cnum)
        getRwd.setText(crwd)
        getDtl.setText(cdtl)
        getDt.setText(cdt)

        btn_del.setOnClickListener {
            var cid = intent.getIntExtra("cardId", 0)
            Log.d("cid", cid.toString())
            val map: MutableMap<String, RequestBody> = HashMap()
            map["id"] = toPart(cid.toString()) as RequestBody
            mAPIService!!.delCard(token!!, "CardDelete", map).enqueue(object :
                    Callback<DeleteResponse> {
                override fun onResponse(

                        call: Call<DeleteResponse>,
                        response: retrofit2.Response<DeleteResponse>
                ) {
                    Toast.makeText(this@CardDetailActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                    val i = (Intent(applicationContext, HomeActivity::class.java))
                    startActivity(i)
                }

                override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                    Toast.makeText(this@CardDetailActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
        }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.share_menu)
        {
            var cid= intent.getIntExtra("cardId", 0)
            Log.d("cid", cid.toString())
            val map: MutableMap<String, RequestBody> = HashMap()
            map["card_id"] = toPart(cid.toString()) as RequestBody

            mAPIService!!.codeGen(token!!, "ShareCode", map).enqueue(object :
                    Callback<ShareResponse> {
                override fun onResponse(
                        call: Call<ShareResponse>,
                        response: retrofit2.Response<ShareResponse>
                ) {
                    //Toast.makeText(this@CardDetailActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                    var shareIntent = Intent().apply {
                        this.action = Intent.ACTION_SEND
                        this.putExtra(Intent.EXTRA_TEXT, response.body()?.data!!.share_code)
                        this.type = "text/plain"
                    }
                    startActivity(shareIntent)
                }

                override fun onFailure(call: Call<ShareResponse>, t: Throwable) {
                    Toast.makeText(this@CardDetailActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
        else
        {
            return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CardDetailActivity, HomeActivity::class.java))
        finish()
    }
}



