package com.devsneha.pingme.data.repository

import com.devsneha.pingme.data.remote.ApiService
import com.devsneha.pingme.model.OtpVerificationResponse
import com.devsneha.pingme.model.SendOtpRequest
import com.devsneha.pingme.model.SendOtpResponse
import retrofit2.Response
import javax.inject.Inject

class ApiServiceRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun verifyOtp(phoneNumber: String, otp: String): Response<OtpVerificationResponse> {
        val stringResponse = apiService.verifyOtp(phoneNumber, otp)
        
        return if (stringResponse.isSuccessful && stringResponse.body() != null) {
            val responseBody = stringResponse.body()!!
            // Treat the response as a simple message
            val simpleResponse = OtpVerificationResponse(message = responseBody)
            Response.success(simpleResponse)
        } else {
            Response.error(stringResponse.code(), stringResponse.errorBody()!!)
        }
    }
    
    suspend fun sendOtp(username: String, phoneNumber: String): Response<SendOtpResponse> {
        val request = SendOtpRequest(username = username, phoneNumber = phoneNumber)
        val stringResponse = apiService.sendOtp(request)
        
        return if (stringResponse.isSuccessful && stringResponse.body() != null) {
            val responseBody = stringResponse.body()!!
            // Treat the response as a simple message
            val simpleResponse = SendOtpResponse(message = responseBody)
            Response.success(simpleResponse)
        } else {
            Response.error(stringResponse.code(), stringResponse.errorBody()!!)
        }
    }
}