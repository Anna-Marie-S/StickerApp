package com.example.stickerapp

data class Sticker (
    val id: Int,
    val name: String,
    val image: Int,
    var posX: Float = 50F,
    var posY: Float = 50F
)

val stickers = listOf(
    Sticker(
        id = 1,
        name = "Taco",
        image = R.drawable.icons8_taco_64
    ),
    Sticker(
        id = 2,
        name = "Ghost",
        image = R.drawable.icons8_ghost_64
),
    Sticker(
        id = 1,
        name = "Castle",
        image = R.drawable.icons8_castle_48
    )
)