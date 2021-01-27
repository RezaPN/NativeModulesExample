package com.test.network

interface BaseEndpoint {

    companion object {
        val baseUrlFaceID = "https://api-idn.megvii.com"
        val faceIDAppSecret = "S66KAQzbwhrBUQ6rePNaoAj7iutYS9Id"
        val faceIDApiKey = "_bYvreG6p02BotOam5YZcBkrz_qDwhuk"
        val livenessBizToken = "/faceid/v3/sdk/get_biz_token"
        val livenessVerify = "/faceid/v3/sdk/verify"
    }
}