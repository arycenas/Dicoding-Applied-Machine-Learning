package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extractImageUri()
    }

    private fun extractImageUri() {
        val imageUri = intent.getStringExtra(EXTRA)
        if (imageUri != null) {
            val imageUriParser = Uri.parse(imageUri)
            showImage(imageUriParser)

            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let {
                            showResults(it)
                        }
                    }

                    override fun onError(error: String) {
                        Log.d(TAG, "Error $error")
                        showError(error)
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(imageUriParser)
        } else {
            Log.d(TAG, "No image")
            finish()
        }
    }

    private fun showImage(uri: Uri) {
        Log.d(TAG, "Showing image $uri")
        binding.resultImage.setImageURI(uri)
    }

    private fun showResults(result: List<Classifications>) {
        val onTopResult = result[0]
        val label = onTopResult.categories[0].label
        val score = onTopResult.categories[0].score

        fun Float.turnToString(): String {
            return String.format("%.2f%%", this * 100)
        }
        binding.resultText.text = "$label ${score.turnToString()}"
    }

    @SuppressLint("StringFormatInvalid")
    private fun showError(error: String) {
        binding.resultText.text = getString(R.string.error_message, error)
    }

    companion object {
        private const val TAG = "ImagePicker"
        const val EXTRA = "extra_uri"
    }
}