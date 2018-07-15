package io.github.wulkanowy.ui.main.grades;

import android.content.Context;
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
import eu.davidea.viewholders.FlexibleViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.utils.GradeUtils;

public class GradesSubItem
        extends AbstractSectionableItem<GradesSubItem.SubItemViewHolder, GradesHeader> {

    private Grade grade;

    private static int numberOfNotReadGrade;

    private View subjectAlertImage;

    GradesSubItem(GradesHeader header, Grade grade) {
        super(header);
        this.grade = grade;
    }

    public Grade getGrade() {
        return grade;
    }

    void setSubjectAlertImage(View subjectAlertImage) {
        this.subjectAlertImage = subjectAlertImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GradesSubItem that = (GradesSubItem) o;

        return new EqualsBuilder()
                .append(grade, that.grade)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(grade)
                .toHashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grades_subitem;
    }

    @Override
    public SubItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new SubItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SubItemViewHolder holder, int position, List payloads) {
        holder.onBind(grade, subjectAlertImage);
    }

    static class SubItemViewHolder extends FlexibleViewHolder {

        @BindView(R.id.grade_subitem_value)
        TextView value;

        @BindView(R.id.grade_subitem_description)
        TextView description;

        @BindView(R.id.grade_subitem_date)
        TextView date;

        @BindView(R.id.grade_subitem_weight)
        TextView weight;

        @BindView(R.id.grade_subitem_alert_image)
        View alert;

        private View subjectAlertImage;

        private Context context;

        private Grade item;

        SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            context = view.getContext();
            view.setOnClickListener(this);
        }

        void onBind(Grade item, View subjectAlertImage) {
            this.item = item;
            this.subjectAlertImage = subjectAlertImage;

            value.setText(item.getValue());
            value.setBackgroundResource(GradeUtils.getValueColor(item.getValue()));
            description.setText(getDescriptionString());
            date.setText(item.getDate());
            weight.setText(String.format("%s: %s", context.getResources().getString(R.string.grade_weight_text), item.getWeight()));
            alert.setVisibility(item.getRead() ? View.INVISIBLE : View.VISIBLE);

            if (!item.getRead()) {
                numberOfNotReadGrade++;
            }
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            showDialog();

            if (!item.getRead()) {
                numberOfNotReadGrade--;

                if (numberOfNotReadGrade == 0) {
                    subjectAlertImage.setVisibility(View.INVISIBLE);
                }
                item.setIsNew(false);
                item.setRead(true);
                item.update();
                alert.setVisibility(View.INVISIBLE);
            }
        }

        private String getDescriptionString() {
            if (item.getDescription() == null || "".equals(item.getDescription())) {
                if (!"".equals(item.getSymbol())) {
                    return item.getSymbol();
                } else {
                    return context.getString(R.string.noDescription_text);
                }
            } else {
                return item.getDescription();
            }
        }

        private void showDialog() {
            GradesDialogFragment dialogFragment = GradesDialogFragment.newInstance(item);
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), item.toString());
        }
    }
}
