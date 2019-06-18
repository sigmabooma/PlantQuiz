package com.example.plantquiz.controller

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.plantquiz.model.Plant
import com.example.plantquiz.R
import com.example.plantquiz.model.DownloadingObject
import com.example.plantquiz.model.ParsePlantUtility
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var cameraButton: Button? = null
    private var photoGalleryButton: Button? = null
    private var imageTaken: ImageView? = null
    @Suppress("PrivatePropertyName")
    private val OPEN_CAMERA_BUTTON_REQUEST_ID = 1000
    @Suppress("PrivatePropertyName")
    private val OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID = 2000

    var correctAnswerIndex = 0
    var correctPlant: Plant? = null
    var numberOfTimesUserAnsweredCorrectly = 0
    var numberOfTimesUserAnsweredInCorrectly = 0


//    private var button1: Button? = null
//    private var button2: Button? = null
//    private var button3: Button? = null
//    private var button4: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setProgressBar(false)
        displayUIWidgets(false)
        YoYo.with(Techniques.Pulse).duration(700).repeat(5).playOn(btnNextPlant)

        /* Toast.makeText(this, "THE ONCREATE METHOD IS CALLED", Toast.LENGTH_SHORT).show()

         val myPlant = Plant("", "", "", "","","", 0,0)
         //   Plant("koelreuteria", "paniculata", "", "golden rain tree","koelreuteria_paniculata_branch.JPG",
         //   "Branch of koelreuteria paniculata", 3 ,24)
         myPlant.plantName = "Wadas Memory Magnolia"
         var nameOfPlant = myPlant.plantName */

        /* var flower = Plant()
         var tree = Plant()
         var collectionOfPlant: ArrayList<Plant> = ArrayList()
         collectionOfPlant.add(flower)
         collectionOfPlant.add(tree)
         */
        cameraButton = findViewById(R.id.btnOpenCamera)
        photoGalleryButton = findViewById(R.id.btnOpenPhotoGallery)
        imageTaken = findViewById(R.id.imgTaken)

//        button1 = findViewById(R.id.button1)
//        button2 = findViewById(R.id.button2)
//        button3 = findViewById(R.id.button3)
//        button4 = findViewById(R.id.button4)

        cameraButton?.setOnClickListener {
            Toast.makeText(this, "the camera button is clicked", Toast.LENGTH_SHORT).show()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, OPEN_CAMERA_BUTTON_REQUEST_ID)
        }

        photoGalleryButton?.setOnClickListener {
            Toast.makeText(this, "the photo gallery button is clicked", Toast.LENGTH_SHORT).show()
            val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID)
        }

        // see the next plant

        btnNextPlant.setOnClickListener {
            if(checkForInternetConnection()) {
                setProgressBar(true)
                try {
                    val innerClassObject = DownloadingPlantTask()
                    innerClassObject.execute()
                }catch (ex: Exception){
                    ex.printStackTrace()
                }
                //var gradientColors = arrayOf(Color.parseColor("#FFFF66"), Color.parseColor("#ff0008"))
                var gradientColors = IntArray(2)
                gradientColors[0] = Color.parseColor("#FFFF66")
                gradientColors[1] = Color.parseColor("#ff0008")
                var gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientColors)
                var convertDipValue = dipToFloat(this@MainActivity, 50f)
                gradientDrawable.cornerRadius = convertDipValue
                gradientDrawable.setStroke(5, Color.parseColor("#E6D9D9"))



                button1.background = gradientDrawable
                button2.background = gradientDrawable
                button3.background = gradientDrawable
                button4.background = gradientDrawable

            }
        }

    }

    private fun dipToFloat(context: Context, dipVal: Float): Float{
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipVal, metrics)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == OPEN_CAMERA_BUTTON_REQUEST_ID){
            if(resultCode == Activity.RESULT_OK){
                val imageData = data?.extras?.get("data") as Bitmap
                imageTaken?.setImageBitmap(imageData)
            }
        }
        if(requestCode == OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID){
            if(resultCode == Activity.RESULT_OK){
                val contentURI = data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                imageTaken?.setImageBitmap(bitmap)
            }
        }
    }

    fun button1IsClicked(buttonView: View){
        specifyTheRightAndWrongAnswer(0)
    }
    fun button2IsClicked(buttonView: View){
        specifyTheRightAndWrongAnswer(1)
    }
    fun button3IsClicked(buttonView: View){
        specifyTheRightAndWrongAnswer(2)
    }
    fun button4IsClicked(buttonView: View){
        specifyTheRightAndWrongAnswer(3)
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadingPlantTask: AsyncTask<String, Int, List<Plant>>(){

        override fun doInBackground(vararg params: String?): List<Plant>? {
            // can access background thread. Not user interface thread
            try{
//            val downloadingObject = DownloadingObject()
//            var jsonData = downloadingObject.downloadJSONDataFromLink("http://plantplaces.com/perl/mobile/flashcard.pl")
//            Log.i("JSON", jsonData)
                val parsePlant = ParsePlantUtility()

            return parsePlant.parsePlantObjectsFromJSONData()
        }
            catch (e: Exception){
                Log.i("errorexe", e.message)
            }
            return null
        }


        override fun onPostExecute(result: List<Plant>?) {
            super.onPostExecute(result)

            var numberOfPlants = result?.size ?: 0

            if (numberOfPlants > 0){
                var randomPlantIndexForButton1: Int = ( Math.random() * result!!.size ).toInt()
                var randomPlantIndexForButton2: Int = ( Math.random() * result!!.size ).toInt()
                var randomPlantIndexForButton3: Int = ( Math.random() * result!!.size ).toInt()
                var randomPlantIndexForButton4: Int = ( Math.random() * result!!.size ).toInt()

                var allRandomPlants = ArrayList<Plant>()
                allRandomPlants.add(result[randomPlantIndexForButton1])
                allRandomPlants.add(result[randomPlantIndexForButton2])
                allRandomPlants.add(result[randomPlantIndexForButton3])
                allRandomPlants.add(result[randomPlantIndexForButton4])

                button1.text = result[randomPlantIndexForButton1].toString()
                button2.text = result[randomPlantIndexForButton2].toString()
                button3.text = result[randomPlantIndexForButton3].toString()
                button4.text = result[randomPlantIndexForButton4].toString()

                correctAnswerIndex = (Math.random() * allRandomPlants.size).toInt()
                correctPlant = allRandomPlants[correctAnswerIndex]

                val downloadingImageTask = DownloadingImageTask()
                downloadingImageTask.execute(allRandomPlants[correctAnswerIndex].pictureName)
            }
            // can access user interface thread. Not background thread
        }
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "THE OnStart METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(this, "THE OnResume METHOD IS CALLED", Toast.LENGTH_SHORT).show()
        checkForInternetConnection()
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(this, "THE OnPause METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(this, "THE OnStop METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(this, "THE OnRestart METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "THE OnDestroy METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }

    fun imageViewIsClicked(view: View){
        val randomNumber: Int = (Math.random() * 6).toInt() + 1
        Log.i("TAG", "the random number is: $randomNumber")
        when (randomNumber) {
            1 -> btnOpenCamera.setBackgroundColor(Color.YELLOW)
            2 -> btnOpenPhotoGallery.setBackgroundColor(Color.MAGENTA)
            3 -> button1.setBackgroundColor(Color.DKGRAY)
            4 -> button2.setBackgroundColor(Color.GREEN)
            5 -> button3.setBackgroundColor(Color.RED)
            else -> button4.setBackgroundColor(Color.BLUE)
        }
    }

    // check for internet connection

    private fun checkForInternetConnection(): Boolean{

        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isDeviceConnectedToInternet = networkInfo != null && networkInfo.isConnected

        return if(isDeviceConnectedToInternet){
             true
        }
        else {
            createAlert()
            return false
        }
    }
    private fun createAlert(){
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Network Error")
        alertDialog.setMessage("Please check for internet connection!!!")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") {
                _: DialogInterface?, _: Int ->
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") {
                _: DialogInterface?, _: Int ->
            Toast.makeText(this@MainActivity, "You must be connected to the internet", Toast.LENGTH_SHORT).show()
            finish()
        }
        alertDialog.show()
    }

    private fun specifyTheRightAndWrongAnswer(userGuess: Int){
            when(correctAnswerIndex){
                0 -> button1.setBackgroundColor(Color.CYAN)
                1 -> button2.setBackgroundColor(Color.CYAN)
                2 -> button3.setBackgroundColor(Color.CYAN)
                3 -> button4.setBackgroundColor(Color.CYAN)
            }
        if(userGuess == correctAnswerIndex){
            txtState.text = "Right!"
            numberOfTimesUserAnsweredCorrectly++
            txtRightAnswers.text = "$numberOfTimesUserAnsweredCorrectly"
        } else{
            val correctPlantName = correctPlant.toString()
            txtState.text = "Wrong. Choose : $correctPlantName"
            numberOfTimesUserAnsweredInCorrectly++
            txtWrongAnswers.text = "$numberOfTimesUserAnsweredInCorrectly"
        }
    }

    // downloading image process

    inner class DownloadingImageTask: AsyncTask<String, Int, Bitmap?>(){
        override fun doInBackground(vararg pictureName: String?): Bitmap? {
            try {
                val downloadingObject = DownloadingObject()
                return downloadingObject.downloadPlantPicture(pictureName[0])
            } catch (ex: Exception){
                ex.printStackTrace()
            }
                return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            setProgressBar(false)
            displayUIWidgets(true)
            playAnimationOnView(imageTaken, Techniques.Tada)
            playAnimationOnView(button1, Techniques.RollIn)
            playAnimationOnView(button2, Techniques.RollIn)
            playAnimationOnView(button3, Techniques.RollIn)
            playAnimationOnView(button4, Techniques.RollIn)
            playAnimationOnView(txtState, Techniques.Swing)
            playAnimationOnView(txtWrongAnswers, Techniques.Landing)
            playAnimationOnView(txtRightAnswers, Techniques.FlipInX)
            imgTaken.setImageBitmap(result)
        }
    }

    private fun setProgressBar(show: Boolean){
        if(show){
            linearLayoutProgress.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE // to show progress bar
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else if(!show){
            linearLayoutProgress.visibility = View.GONE
            progressBar.visibility = View.GONE // to hide progress bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    // set visibility of ui widgets

    private fun displayUIWidgets(display: Boolean){

        if(display){
            imgTaken.visibility = View.VISIBLE
            button1.visibility = View.VISIBLE
            button2.visibility = View.VISIBLE
            button3.visibility = View.VISIBLE
            button4.visibility = View.VISIBLE
            txtState.visibility = View.VISIBLE
            txtRightAnswers.visibility = View.VISIBLE
            txtWrongAnswers.visibility = View.VISIBLE

        } else if (!display){
            imgTaken.visibility = View.INVISIBLE
            button1.visibility = View.INVISIBLE
            button2.visibility = View.INVISIBLE
            button3.visibility = View.INVISIBLE
            button4.visibility = View.INVISIBLE
            txtState.visibility = View.INVISIBLE
            txtRightAnswers.visibility = View.INVISIBLE
            txtWrongAnswers.visibility = View.INVISIBLE
        }

    }

    // Playing Animations

    private fun playAnimationOnView(view: View?, technique: Techniques){
        YoYo.with(technique).duration(700).repeat(0).playOn(view)
    }

}
