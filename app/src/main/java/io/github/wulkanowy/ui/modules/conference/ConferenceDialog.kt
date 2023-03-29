package io.github.wulkanowy.ui.modules.conference

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.databinding.DialogConferenceBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.serializable
import io.github.wulkanowy.utils.toFormattedString

@AndroidEntryPoint
class ConferenceDialog : BaseDialogFragment<DialogConferenceBinding>() {

    private lateinit var conference: Conference

    companion object {

        private const val ARGUMENT_KEY = "item"

        fun newInstance(conference: Conference) = ConferenceDialog().apply {
            arguments = bundleOf(ARGUMENT_KEY to conference)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        conference = requireArguments().serializable(ARGUMENT_KEY)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(DialogConferenceBinding.inflate(layoutInflater).apply { binding = this }.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            conferenceDialogClose.setOnClickListener { dismiss() }

            conferenceDialogSubjectValue.text = conference.subject
            conferenceDialogDateValue.text = conference.date.toFormattedString("dd.MM.yyyy HH:mm")
            conferenceDialogHeaderValue.text = conference.title
            conferenceDialogAgendaValue.text = conference.agenda
            conferenceDialogPresentValue.text = conference.presentOnConference
            conferenceDialogPresentValue.isVisible = conference.presentOnConference.isNotBlank()
            conferenceDialogPresentTitle.isVisible = conference.presentOnConference.isNotBlank()
            conferenceDialogAgendaValue.isVisible = conference.agenda.isNotBlank()
            conferenceDialogAgendaTitle.isVisible = conference.agenda.isNotBlank()
        }
    }
}
