package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.LuckyNumber
import java.time.LocalDate
import kotlin.random.Random

val debugLuckyNumber
    get() = LuckyNumber(
        studentId = 0,
        date = LocalDate.now(),
        luckyNumber = Random.nextInt(1, 128),
    )
