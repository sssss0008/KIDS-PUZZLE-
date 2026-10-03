package com.example.data

data class PhonicsLetter(
    val letter: Char,
    val word: String,
    val phonicsSound: String,
    val emoji: String,
    val funSentence: String,
    val colorHex: Long
)

data class NumberItem(
    val number: Int,
    val word: String,
    val itemEmoji: String,
    val colorHex: Long
)

data class AnimalItem(
    val name: String,
    val emoji: String,
    val soundText: String,
    val voiceSpeech: String,
    val funFact: String,
    val colorHex: Long
)

data class ShapeItem(
    val name: String,
    val emoji: String,
    val description: String,
    val sides: Int,
    val colorHex: Long
)

data class ColorItem(
    val name: String,
    val colorHex: Long,
    val exampleEmoji: String,
    val exampleName: String
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val speechPrompt: String,
    val visualHint: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object KidsLearningData {

    val ALPHABET_LIST = listOf(
        PhonicsLetter('A', "Apple", "/æ/ says Ah", "🍎", "A is for Apple! Sweet and crunchy.", 0xFFFF5A5F),
        PhonicsLetter('B', "Butterfly", "/b/ says Buh", "🦋", "B is for Butterfly! Flutters high.", 0xFF4D96FF),
        PhonicsLetter('C', "Cat", "/k/ says Kuh", "🐱", "C is for Cat! Purrs and plays.", 0xFFFFB703),
        PhonicsLetter('D', "Dolphin", "/d/ says Duh", "🐬", "D is for Dolphin! Jumps in the sea.", 0xFF00B4D8),
        PhonicsLetter('E', "Elephant", "/e/ says Eh", "🐘", "E is for Elephant! Big long trunk.", 0xFF845EC2),
        PhonicsLetter('F', "Frog", "/f/ says Fuh", "🐸", "F is for Frog! Ribbit and hop.", 0xFF06D6A0),
        PhonicsLetter('G', "Giraffe", "/g/ says Guh", "🦒", "G is for Giraffe! Tallest in the park.", 0xFFFF9671),
        PhonicsLetter('H', "Honeybee", "/h/ says Huh", "🐝", "H is for Honeybee! Buzzy buzzing bee.", 0xFFFFC75F),
        PhonicsLetter('I', "Ice Cream", "/aɪ/ says Eye", "🍦", "I is for Ice Cream! Chilly and yummy.", 0xFFFF6F91),
        PhonicsLetter('J', "Jellyfish", "/dʒ/ says Juh", "🪼", "J is for Jellyfish! Glowing in the ocean.", 0xFF2C73D2),
        PhonicsLetter('K', "Kangaroo", "/k/ says Kuh", "🦘", "K is for Kangaroo! Boing boing bounce.", 0xFFD65DB1),
        PhonicsLetter('L', "Lion", "/l/ says Luh", "🦁", "L is for Lion! Roaring king of beasts.", 0xFFFF9671),
        PhonicsLetter('M', "Monkey", "/m/ says Muh", "🐵", "M is for Monkey! Loves bananas.", 0xFF845EC2),
        PhonicsLetter('N', "Nest", "/n/ says Nuh", "🪺", "N is for Nest! Cozy bird home.", 0xFF00C9A7),
        PhonicsLetter('O', "Owl", "/ɒ/ says Ah", "🦉", "O is for Owl! Wise in the night.", 0xFF4B4453),
        PhonicsLetter('P', "Penguin", "/p/ says Puh", "🐧", "P is for Penguin! Waddles on the ice.", 0xFF0089BA),
        PhonicsLetter('Q', "Queen", "/kw/ says Kwuh", "👑", "Q is for Queen! Golden shiny crown.", 0xFFFFD166),
        PhonicsLetter('R', "Rainbow", "/r/ says Ruh", "🌈", "R is for Rainbow! Seven magic colors.", 0xFFFF6B6B),
        PhonicsLetter('S', "Sun", "/s/ says Suh", "☀️", "S is for Sun! Warm and bright.", 0xFFFFB703),
        PhonicsLetter('T', "Tiger", "/t/ says Tuh", "🐯", "T is for Tiger! Striped and speedy.", 0xFFFF793F),
        PhonicsLetter('U', "Umbrella", "/ʌ/ says Uh", "☂️", "U is for Umbrella! Keeps off the rain.", 0xFF6C5CE7),
        PhonicsLetter('V', "Violin", "/v/ says Vuh", "🎻", "V is for Violin! Sweet lovely music.", 0xFF00B894),
        PhonicsLetter('W', "Whale", "/w/ says Wuh", "🐋", "W is for Whale! Giant gentle friend.", 0xFF0984E3),
        PhonicsLetter('X', "Xylophone", "/z/ says Zz", "🎼", "X is for Xylophone! Ding dong melody.", 0xFFFD79A8),
        PhonicsLetter('Y', "Yacht", "/j/ says Yuh", "⛵", "Y is for Yacht! Sailing on the waves.", 0xFF4D96FF),
        PhonicsLetter('Z', "Zebra", "/z/ says Zz", "🦓", "Z is for Zebra! Black and white stripes.", 0xFF2D3436)
    )

    val NUMBERS_LIST = listOf(
        NumberItem(1, "One", "⭐", 0xFFFF5A5F),
        NumberItem(2, "Two", "🎈", 0xFF00B4D8),
        NumberItem(3, "Three", "🍎", 0xFF06D6A0),
        NumberItem(4, "Four", "🚗", 0xFFFFB703),
        NumberItem(5, "Five", "🍭", 0xFF845EC2),
        NumberItem(6, "Six", "🌻", 0xFFFF9671),
        NumberItem(7, "Seven", "🦋", 0xFF2C73D2),
        NumberItem(8, "Eight", "🍓", 0xFFFF6B6B),
        NumberItem(9, "Nine", "🚀", 0xFF6C5CE7),
        NumberItem(10, "Ten", "🏆", 0xFFFFD166),
        NumberItem(11, "Eleven", "🐸", 0xFF00C9A7),
        NumberItem(12, "Twelve", "🧁", 0xFFFD79A8),
        NumberItem(15, "Fifteen", "⚽", 0xFF0984E3),
        NumberItem(20, "Twenty", "👑", 0xFFFF793F)
    )

    val ANIMALS_LIST = listOf(
        AnimalItem("Lion", "🦁", "Roaaar!", "The lion says Roaaar! King of the jungle!", "Lions love to nap in the warm sun.", 0xFFFFAA00),
        AnimalItem("Puppy", "🐶", "Woof Woof!", "The puppy says Woof Woof! Loyal friend!", "Dogs wag their tails when they are happy.", 0xFFE17055),
        AnimalItem("Kitty", "🐱", "Meow Meow!", "The kitten says Meow! Purr purr!", "Cats can jump five times their own height.", 0xFFFF7675),
        AnimalItem("Elephant", "🐘", "Pawoo!", "The elephant trumpets Pawoo! Big ears waving!", "Elephants use their trunk like a giant hand.", 0xFF74B9FF),
        AnimalItem("Monkey", "🐵", "Ooh Ooh Aah Aah!", "The monkey says Ooh Ooh Aah Aah!", "Monkeys love to swing between tree branches.", 0xFF845EC2),
        AnimalItem("Frog", "🐸", "Ribbit Ribbit!", "The frog says Ribbit Ribbit! Splash into pond!", "Frogs drink water through their skin.", 0xFF00B894),
        AnimalItem("Cow", "🐮", "Moo Moo!", "The cow says Moo Moo! Giving yummy milk!", "Cows love eating fresh green grass.", 0xFFFDCB6E),
        AnimalItem("Duck", "🦆", "Quack Quack!", "The duck says Quack Quack! Swimming happily!", "Duck feathers are completely waterproof.", 0xFF0984E3),
        AnimalItem("Sheep", "🐑", "Baaa Baaa!", "The sheep says Baaa Baaa! Soft fluffy wool!", "Sheep stay together in happy flocks.", 0xFF6C5CE7),
        AnimalItem("Dolphin", "🐬", "Click Click Eeee!", "The dolphin says Eeee! Friendly ocean diver!", "Dolphins are super smart and love playing tag.", 0xFF00CEC9),
        AnimalItem("Owl", "🦉", "Hoo Hoo!", "The owl says Hoo Hoo! Flying under the moon!", "Owls can turn their heads almost all the way around.", 0xFF636E72),
        AnimalItem("Bear", "🐻", "Grrr!", "The bear says Grrr! Honey lover!", "Bears have an amazing sense of smell.", 0xFFB33927)
    )

    val SHAPES_LIST = listOf(
        ShapeItem("Circle", "⚪", "Round and smooth, no corners!", 0, 0xFFFF7675),
        ShapeItem("Square", "⬛", "Four equal sides and four straight corners!", 4, 0xFF0984E3),
        ShapeItem("Triangle", "🔺", "Three sharp corners and three sides!", 3, 0xFF00B894),
        ShapeItem("Star", "⭐", "Five shiny points that twinkle in the sky!", 5, 0xFFFFAA00),
        ShapeItem("Heart", "❤️", "Full of love, smiles, and kindness!", 0, 0xFFFF5A5F),
        ShapeItem("Diamond", "💎", "Sparkling kite shape with four angles!", 4, 0xFF6C5CE7),
        ShapeItem("Hexagon", "⬡", "Six awesome sides just like a bee honeycomb!", 6, 0xFF00CEC9),
        ShapeItem("Oval", "🥚", "Stretched like a happy dinosaur egg!", 0, 0xFFFD79A8)
    )

    val COLORS_LIST = listOf(
        ColorItem("Sunny Red", 0xFFFF4757, "🍎", "Crisp Red Apple"),
        ColorItem("Sky Blue", 0xFF1E90FF, "🐬", "Ocean Dolphin"),
        ColorItem("Leaf Green", 0xFF2ED573, "🐸", "Happy Green Frog"),
        ColorItem("Sunflower Yellow", 0xFFFFA502, "⭐", "Twinkling Star"),
        ColorItem("Juicy Orange", 0xFFFF7F50, "🍊", "Sweet Orange Fruit"),
        ColorItem("Royal Purple", 0xFF8E44AD, "🍇", "Grapes Bunch"),
        ColorItem("Bubblegum Pink", 0xFFFF6B81, "🌸", "Cherry Blossom")
    )

    val QUIZ_LIST = listOf(
        QuizQuestion(
            id = 1,
            question = "Which sweet animal says 'Meow Meow'?",
            speechPrompt = "Which sweet animal says Meow Meow?",
            visualHint = "🐾",
            options = listOf("🐶 Puppy", "🐱 Kitty", "🐸 Frog", "🦁 Lion"),
            correctIndex = 1,
            explanation = "Hooray! The kitten says Meow Meow!"
        ),
        QuizQuestion(
            id = 2,
            question = "Which letter does 'Apple' start with?",
            speechPrompt = "Which letter does Apple start with?",
            visualHint = "🍎",
            options = listOf("Letter B", "Letter A", "Letter D", "Letter M"),
            correctIndex = 1,
            explanation = "Super job! A is for Apple!"
        ),
        QuizQuestion(
            id = 3,
            question = "How many stars are shining here? ⭐ ⭐ ⭐",
            speechPrompt = "How many stars are shining here? Let's count them!",
            visualHint = "⭐⭐⭐",
            options = listOf("2 Stars", "3 Stars", "4 Stars", "5 Stars"),
            correctIndex = 1,
            explanation = "Awesome! 1, 2, 3 stars!"
        ),
        QuizQuestion(
            id = 4,
            question = "Which shape has 3 sides and 3 corners?",
            speechPrompt = "Which shape has three sides and three corners?",
            visualHint = "🔺",
            options = listOf("Square", "Circle", "Triangle", "Star"),
            correctIndex = 2,
            explanation = "Bingo! A triangle has 3 sides!"
        ),
        QuizQuestion(
            id = 5,
            question = "What color is a ripe banana?",
            speechPrompt = "What color is a ripe banana?",
            visualHint = "🍌",
            options = listOf("Blue", "Purple", "Yellow", "Black"),
            correctIndex = 2,
            explanation = "Delicious! Bananas are bright sunny yellow!"
        ),
        QuizQuestion(
            id = 6,
            question = "Which giant animal has a long trunk and big ears?",
            speechPrompt = "Which giant animal has a long trunk and big ears?",
            visualHint = "🐘",
            options = listOf("Elephant", "Monkey", "Dog", "Duck"),
            correctIndex = 0,
            explanation = "Yay! The gentle elephant has a long trunk!"
        )
    )
}
