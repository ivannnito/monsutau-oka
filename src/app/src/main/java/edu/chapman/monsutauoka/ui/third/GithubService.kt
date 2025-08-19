//package edu.chapman.monsutauoka.ui.third
//
//import android.util.Base64
//import org.json.JSONObject
//import java.io.IOException
//import java.net.HttpURLConnection
//import java.net.URL
//import java.net.URLEncoder
//import java.nio.charset.StandardCharsets
//
//class GithubService(
//    private val token: String,
//    private val owner: String,
//    private val repo: String,
//    private val path: String,
//    private val branch: String? = null
//) {
//    data class LoadResult(val text: String, val sha: String?) // sha==null => new file
//
//    fun loadFile(): LoadResult {
//        Thread.sleep(1000)
//        val url = contentsUrl(branch)
//        val c = open("GET", url)
//        val code = c.responseCode
//        val body = readBody(c)
//
//        if (code == 404) return LoadResult("", null)
//        if (code !in 200..299) throw IOException(ghError(code, body))
//
//        val json = JSONObject(body)
//        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//        val sha = json.optString("sha", null).takeIf { !it.isNullOrEmpty() }
//        val raw = json.optString("content", "").replace("\n", "")
//        val bytes = if (raw.isNotBlank()) Base64.decode(raw, Base64.DEFAULT) else ByteArray(0)
//        val text = String(bytes, StandardCharsets.UTF_8)
//        return LoadResult(text, sha)
//    }
//
//    /** Saves text; returns the new content sha. Pass sha=null to create the file. */
//    fun saveFile(
//        newText: String,
//        sha: String?,
//        commitMessage: String? = null
//    ): String {
//        Thread.sleep(1000)
//        val url = contentsUrl(ref = null) // branch is specified in body for PUT
//        val c = open("PUT", url)
//        c.doOutput = true
//        c.setRequestProperty("Content-Type", "application/json; charset=utf-8")
//
//        val encoded = Base64.encodeToString(newText.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
//        val bodyJson = JSONObject()
//            .put("message", commitMessage ?: "Update $path via app")
//            .put("content", encoded)
//            .apply { if (sha != null) put("sha", sha) }
//            .apply { if (branch != null) put("branch", branch) }
//            .toString()
//
//        c.outputStream.use { it.write(bodyJson.toByteArray(StandardCharsets.UTF_8)) }
//
//        val code = c.responseCode
//        val body = readBody(c)
//        if (code !in 200..299) throw IOException(ghError(code, body))
//
//        val json = JSONObject(body)
//        return json.optJSONObject("content")?.optString("sha") ?: (sha ?: "")
//    }
//
//    // ---- helpers ----
//    private fun contentsUrl(ref: String?): String {
//        val safePath = path.split("/").joinToString("/") {
//            URLEncoder.encode(it, "UTF-8").replace("+", "%20")
//        }
//        val base = "https://api.github.com/repos/$owner/$repo/contents/$safePath"
//        return if (!ref.isNullOrBlank()) "$base?ref=$ref" else base
//    }
//
//    private fun open(method: String, urlStr: String): HttpURLConnection =
//        (URL(urlStr).openConnection() as HttpURLConnection).apply {
//            requestMethod = method
//            connectTimeout = 15_000
//            readTimeout = 20_000
//            setRequestProperty("Authorization", "Bearer $token")
//            setRequestProperty("Accept", "application/vnd.github+json")
//        }
//
//    private fun readBody(c: HttpURLConnection): String {
//        val stream = if (c.responseCode in 200..299) c.inputStream else c.errorStream
//        return stream?.bufferedReader()?.use { it.readText() }.orEmpty()
//    }
//
//    private fun ghError(code: Int, body: String): String =
//        try { "GitHub $code: " + JSONObject(body).optString("message", body) }
//        catch (_: Throwable) { "GitHub $code: $body" }
//}