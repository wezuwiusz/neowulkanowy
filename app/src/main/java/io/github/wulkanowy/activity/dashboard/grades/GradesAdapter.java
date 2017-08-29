package io.github.wulkanowy.activity.dashboard.grades;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import io.github.wulkanowy.R;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class GradesAdapter extends ExpandableRecyclerViewAdapter<GradesAdapter.SubjectViewHolder, GradesAdapter.GradeViewHolder> {

    private Activity activity;

    public GradesAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        activity = (Activity) context;
    }

    @Override
    public SubjectViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public GradeViewHolder onCreateChildViewHolder(ViewGroup child, int viewType) {
        View view = LayoutInflater.from(child.getContext()).inflate(R.layout.grade_item, child, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(SubjectViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.bind(group);
    }

    @Override
    public void onBindChildViewHolder(GradeViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        holder.bind((GradeItem) group.getItems().get(childIndex));
    }

    public class SubjectViewHolder extends GroupViewHolder {

        private TextView subjectName;

        private ImageView indicatorDown;

        private ImageView indicatorUp;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            subjectName = (TextView) itemView.findViewById(R.id.subject_text);
            indicatorDown = (ImageView) itemView.findViewById(R.id.group_indicator_down);
            indicatorUp = (ImageView) itemView.findViewById(R.id.group_indicator_up);

        }

        public void bind(ExpandableGroup group) {
            subjectName.setText(group.getTitle());

            if (isGroupExpanded(group)) {
                indicatorDown.setVisibility(View.INVISIBLE);
                indicatorUp.setVisibility(View.VISIBLE);
            } else {
                indicatorDown.setVisibility(View.VISIBLE);
                indicatorUp.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void expand() {
            RotateAnimation rotate =
                    new RotateAnimation(-360, -180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(false);
            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //Empty method definition
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    indicatorDown.setVisibility(View.INVISIBLE);
                    indicatorUp.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //Empty method definition
                }
            });
            indicatorDown.setAnimation(rotate);
        }

        @Override
        public void collapse() {
            RotateAnimation rotate =
                    new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(false);
            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //Empty method definition
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    indicatorDown.setVisibility(View.VISIBLE);
                    indicatorUp.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //Empty method definition
                }
            });
            indicatorUp.setAnimation(rotate);
        }
    }

    public class GradeViewHolder extends ChildViewHolder {

        private TextView gradeValue;

        private TextView descriptionGrade;

        private TextView dateGrade;

        private GradeItem grade;

        public GradeViewHolder(final View itemView) {
            super(itemView);
            gradeValue = (TextView) itemView.findViewById(R.id.grade_text);
            descriptionGrade = (TextView) itemView.findViewById(R.id.description_grade_text);
            dateGrade = (TextView) itemView.findViewById(R.id.grade_date_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GradesDialogFragment gradesDialogFragment = GradesDialogFragment.newInstance(grade);
                    gradesDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                    gradesDialogFragment.show(activity.getFragmentManager(), grade.toString());
                }
            });
        }

        public void bind(GradeItem grade) {
            this.grade = grade;
            gradeValue.setText(grade.getValue());
            gradeValue.setBackgroundResource(grade.getValueColor());
            dateGrade.setText(grade.getDate());

            if (grade.getDescription().equals("") || grade.getDescription() == null) {
                if (!grade.getSymbol().equals("")) {
                    descriptionGrade.setText(grade.getSymbol());
                } else {
                    descriptionGrade.setText(R.string.noDescription_text);
                }
            } else {
                descriptionGrade.setText(grade.getDescription());
            }

        }
    }
}
