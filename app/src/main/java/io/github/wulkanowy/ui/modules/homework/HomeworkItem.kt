package io.github.wulkanowy.ui.modules.homework

data class HomeworkItem<out T>(val value: T, val viewType: ViewType) {

    enum class ViewType(val id: Int) {
        HEADER(1),
        ITEM(2)
    }
}
