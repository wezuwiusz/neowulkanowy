package io.github.wulkanowy

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.sdk.Sdk
import io.mockk.every
import io.mockk.mockk

fun createWulkanowySdkFactoryMock(sdk: Sdk) = mockk<WulkanowySdkFactory>()
    .apply {
        every { create() } returns sdk
        every { create(any(), any()) } answers { callOriginal() }
    }
