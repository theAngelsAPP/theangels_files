// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.profile.support;

// IMPORTS
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import co.median.android.a2025_theangels_new.R;

// FAQAdapter - Expandable list adapter showing FAQ questions and answers
public class FAQAdapter extends BaseExpandableListAdapter {

    // VARIABLES
    private final Context context;
    private final List<String> questionList;
    private final HashMap<String, String> answerMap;

    // Constructor - Saves question and answer data
    public FAQAdapter(Context context, List<String> questionList, HashMap<String, String> answerMap) {
        this.context = context;
        this.questionList = questionList;
        this.answerMap = answerMap;
    }

    // getGroupCount - Total number of questions
    @Override
    public int getGroupCount() {
        return questionList.size();
    }

    // getChildrenCount - Each question has one answer
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    // getGroup - Returns question text
    @Override
    public Object getGroup(int groupPosition) {
        return questionList.get(groupPosition);
    }

    // getChild - Returns answer text for the given question
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return answerMap.get(questionList.get(groupPosition));
    }

    // getGroupId - Uses group index as ID
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // getChildId - Uses child index as ID
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // hasStableIds - Required override (returns false)
    @Override
    public boolean hasStableIds() {
        return false;
    }

    // getGroupView - Inflates question row and rotates arrow
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String question = (String) getGroup(groupPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_faq_question, parent, false);
        }

        TextView questionText = convertView.findViewById(R.id.question_text);
        ImageView arrowIcon = convertView.findViewById(R.id.question_arrow);

        questionText.setText(question);

        // Rotate arrow based on expansion state
        arrowIcon.setRotation(isExpanded ? 180f : 0f);

        return convertView;
    }

    // getChildView - Inflates answer row
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        String answer = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_faq_answer, parent, false);
        }

        TextView answerText = convertView.findViewById(R.id.answer_text);
        answerText.setText(answer);

        return convertView;
    }

    // isChildSelectable - Answers are not selectable
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
