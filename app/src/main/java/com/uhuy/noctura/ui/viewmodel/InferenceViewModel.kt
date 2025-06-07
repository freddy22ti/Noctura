package com.uhuy.noctura.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhuy.noctura.data.model.ModelInput
import com.uhuy.noctura.data.model.SleepPostRequest
import com.uhuy.noctura.data.model.User
import com.uhuy.noctura.data.repository.UserRepository
import com.uhuy.noctura.utils.OnnxModelRunner
import com.uhuy.noctura.utils.TimeConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InferenceViewModel(
    context: Context,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _prediction = MutableStateFlow<Float?>(null)
    val prediction: StateFlow<Float?> = _prediction.asStateFlow()

    private val modelRunner = OnnxModelRunner(context)

    /*
    Input:
    Age: Int ✅
    Gender, 1: Male, 0: Female ✅
    Bedtime: Float (hours) ✅
    Wakeup Time: Float (hours) ✅
    Sleep Duration: Float (hours)
    REM Sleep Percentage: float
    Deep Sleep Percentage: float
    Light Sleep Percentage: float
    Awakenings: float (n times)
    Caffeine Consumption:  float (ml)
    Alcohol Consumption: float (ml)
    Smoking Status: 1: yes, 0: no
    Excercise Frequency: (n times)
     */

    private fun convertInput(user: User, sleepPostRequest: SleepPostRequest): ModelInput {
        val age: Float = user.umur.toFloat()
        val gender: Float = if (user.jenisKelamin == "Laki-laki") 1.0f else 0.0f
        val bedTime: Float = TimeConverter.convertTimeToFloat(sleepPostRequest.sleepTime)
        val wakeupTime: Float = TimeConverter.convertTimeToFloat(sleepPostRequest.wakeTime)
        val sleepDuration: Float = sleepPostRequest.sleepDuration
        val remSleep = 25.0f
        val deepSleep = 20.0f
        val lightSLeep = 55.0f
        val awakenings: Float = sleepPostRequest.awakenings.toFloat()
        val caffeineConsumption: Float = sleepPostRequest.caffeineConsumption.toFloat()
        val alcoholConsumption: Float = sleepPostRequest.alcoholConsumption.toFloat()
        val smokingStatus: Float = if (user.smokingStatus) 1.0f else 0.0f
        val excerciseFrequency: Float = user.frekuensiOlahraga.toFloat()

        val convertedInput = ModelInput(
            age,
            gender,
            bedTime,
            wakeupTime,
            sleepDuration,
            remSleep,
            deepSleep,
            lightSLeep,
            awakenings,
            caffeineConsumption,
            alcoholConsumption,
            smokingStatus,
            excerciseFrequency
        )
        return convertedInput
    }

    fun predict(sleepPostRequest: SleepPostRequest) {
        viewModelScope.launch {
            val user = userRepository.fetchUserProfile()

            if (user != null) {
                try {
                    // Konversi input ke format yang sesuai
                    val modelInput = convertInput(user, sleepPostRequest)

                    // Ubah ModelInput menjadi FloatArray
                    val inputData = floatArrayOf(
                        modelInput.age,
                        modelInput.gender,
                        modelInput.bedTime,
                        modelInput.wakeupTime,
                        modelInput.sleepDuration,
                        modelInput.remSleep,
                        modelInput.deepSleep,
                        modelInput.lightSleep,
                        modelInput.awakenings,
                        modelInput.caffeineConsumption,
                        modelInput.alcoholConsumption,
                        modelInput.smokingStatus,
                        modelInput.excerciseFrequency
                    )

                    val inputShape = longArrayOf(1, 13)
                    val result = modelRunner.runModel(inputData, inputShape)

                    // Update prediction value
                    _prediction.value = result

                } catch (e: Exception) {
                    // Handle error
                    e.printStackTrace()
                    _prediction.value = null
                }
            }
        }
    }

    fun resetPrediction() {
        _prediction.value = null
    }
}