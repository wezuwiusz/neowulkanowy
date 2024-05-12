package io.github.wulkanowy

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.sdk.Sdk
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk

fun createWulkanowySdkFactoryMock(sdk: Sdk) = mockk<WulkanowySdkFactory>()
    .apply {
        every { createBase() } returns sdk
        coEvery { create() } returns sdk
        coEvery { create(any(), any()) } returns sdk
    }
