package be.tinvision.webslt

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform