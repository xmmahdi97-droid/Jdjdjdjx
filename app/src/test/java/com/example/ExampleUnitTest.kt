package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testVlessParsingSanity() {
        val link = "vless://50414e45-4c5f-5a45-5553-015280c92561@45.80.108.169:443?path=%2Fstream%2FPANEL_ZEUS%2F015280c92561%2Floc-0&security=tls&encryption=none&host=qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev&fp=unsafe&type=ws&allowInsecure=0&sni=qxzur5enu0os.mmmmjjjjzzzzeeee.workers.dev#ZEUS%20%7C%20%F0%9F%87%BA%F0%9F%87%B8%20%7C%20RHECD1YD"
        assertEquals(true, link.startsWith("vless://"))
    }
}
