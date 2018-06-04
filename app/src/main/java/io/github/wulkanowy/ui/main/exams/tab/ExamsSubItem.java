package io.github.wulkanowy.ui.main.exams.tab;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Exam;
import io.github.wulkanowy.ui.main.exams.ExamsDialogFragment;

public class ExamsSubItem
        extends AbstractSectionableItem<ExamsSubItem.SubItemViewHolder, ExamsHeader> {

    private Exam exam;

    ExamsSubItem(ExamsHeader header, Exam exam) {
        super(header);
        this.exam = exam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ExamsSubItem that = (ExamsSubItem) o;

        return new EqualsBuilder()
                .append(exam, that.exam)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(exam)
                .toHashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.exams_subitem;
    }

    @Override
    public SubItemViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new SubItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, SubItemViewHolder holder, int position, List<Object> payloads) {
        holder.onBind(exam);
    }

    static class SubItemViewHolder extends FlexibleViewHolder {

        @BindView(R.id.exams_subitem_subject)
        TextView subject;

        @BindView(R.id.exams_subitems_teacher)
        TextView teacher;

        @BindView(R.id.exams_subitems_type)
        TextView type;

        private Exam item;

        SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        void onBind(Exam exam) {
            item = exam;

            subject.setText(item.getSubjectAndGroup());
            teacher.setText(item.getTeacher());
            type.setText(item.getType());
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            showDialog();

        }

        private void showDialog() {
            ExamsDialogFragment dialogFragment = ExamsDialogFragment.newInstance(item);
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialogFragment.show(((FragmentActivity) getContentView().getContext()).getSupportFragmentManager(),
                    item.toString());
        }
    }
}
