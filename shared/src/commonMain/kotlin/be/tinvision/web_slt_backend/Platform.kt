package be.tinvision.web_slt_backend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform