package com.uhuy.noctura.utils

import ai.onnxruntime.OnnxTensor
import android.content.Context
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.FloatBuffer

class OnnxModelRunner(context: Context) {
    private var ortEnv: OrtEnvironment? = null
    private var session: OrtSession? = null

    init {
        try {
            // Memuat model machine learning dari folder asset
            ortEnv = OrtEnvironment.getEnvironment()
            val modelPath = loadModelFromAssets(context, "gradient_boosting.onnx")
            session = ortEnv?.createSession(modelPath, OrtSession.SessionOptions())
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize ONNX Runtime: ${e.message}", e)
        }
    }

    @Throws(IOException::class)
    //    Ambil model dari assets
    private fun loadModelFromAssets(context: Context, modelName: String): String {
        val assetManager = context.assets
        val modelFile = File(context.filesDir, modelName)

        assetManager.open(modelName).use { input ->
            FileOutputStream(modelFile).use { output ->
                input.copyTo(output)
            }
        }

        return modelFile.absolutePath
    }

    //    Jalanin model
    fun runModel(inputData: FloatArray, inputShape: LongArray): Float {
        requireNotNull(ortEnv) { "OrtEnvironment not initialized" }
        requireNotNull(session) { "OrtSession not initialized" }

        // Validate input shape
        val expectedElements = inputShape.fold(1L) { acc, dim -> acc * dim }
        require(inputData.size == expectedElements.toInt()) {
            "Input data size doesn't match the specified shape"
        }

        val inputName = session?.inputNames?.iterator()?.next()
            ?: throw IllegalStateException("Model has no inputs")

        return try {
            // Convert FloatArray to FloatBuffer
            val floatBuffer = FloatBuffer.wrap(inputData)

            OnnxTensor.createTensor(ortEnv, floatBuffer, inputShape).use { inputTensor ->
                session?.run(mapOf(inputName to inputTensor))?.use { output ->
                    // Handle 2D array output
                    val result = output[0]?.value as Array<FloatArray>
                    result[0][0]  // Ambil nilai pertama dari output
                } ?: throw IllegalStateException("Model produced no output")
            }
        } catch (e: Exception) {
            throw RuntimeException("Error running model inference: ${e.message}", e)
        }
    }

    fun close() {
        try {
            session?.close()
            ortEnv?.close()
        } finally {
            session = null
            ortEnv = null
        }
    }
}